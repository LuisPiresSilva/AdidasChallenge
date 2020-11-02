package com.adidas.luissilva.ui.screens.goals

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.adidas.luissilva.R
import com.adidas.luissilva.app.GLOBAL_CALLS_INTERVAL
import com.adidas.luissilva.data.common.NetworkState
import com.adidas.luissilva.data.common.ServerErrors
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.databinding.ActivityGoalsBinding
import com.adidas.luissilva.ui.base.BaseActivity
import com.adidas.luissilva.ui.base.BigGenericOtherErrors
import com.adidas.luissilva.ui.base.BigNoInternetErrors
import com.adidas.luissilva.ui.base.adapter.ClickType
import com.adidas.luissilva.ui.base.adapter.GenericStateCard
import com.adidas.luissilva.ui.base.adapter.GenericStateCardErrorViews
import com.adidas.luissilva.ui.widgets.itemdecorators.SpaceItemDecorations
import com.adidas.luissilva.utils.helper.extensions.DpInPx
import com.adidas.luissilva.utils.helper.extensions.removeView
import com.adidas.luissilva.utils.helper.extensions.showView
import com.adidas.luissilva.utils.manager.CallManager
import com.adidas.luissilva.utils.manager.PreferencesManager
import com.adidas.luissilva.workers.FetchGoalsWorker
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

/**
 * Activity to apply the goals screen.
 */
class GoalsActivity : BaseActivity<ActivityGoalsBinding, GoalsViewModel>() {


    @Inject
    lateinit var callManager: CallManager

    @Inject
    lateinit var preferencesManager: PreferencesManager


    override var drawBehindStatusBar = true
    override var darkStatusIcons = true


    override fun layoutToInflate() = R.layout.activity_goals

    override fun getViewModelClass() = GoalsViewModel::class.java

    private val goalsAdapter = GoalsAdapter(::genericCardClickBlock, ::itemClickBlock, ::genericCardErrorBlock)

    override fun doOnCreated() {

        viewModel.goals.observe(this, Observer {
            if (it.isEmpty()) {
                goalsAdapter.setList(GenericStateCard.getLoadingEmptyList())
                fetchGoalsWorker(false)
            } else {
                fetchGoalsWorker(true)
                goalsAdapter.setList(it)
            }
        })





        dataBinding.goalsList.apply {
            layoutManager = object : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally() = false
            }
            if (itemDecorationCount <= 0) {
                addItemDecoration(SpaceItemDecorations.sectionedVerticalSpaceItemDecoration(this, 15F.DpInPx(this.context)))
            }
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            adapter = goalsAdapter
        }

    }


//    I did not persist the last call time (which i should to properly follow these rules) thats why it calls twice the first time
//    also this code would make more sense (would not even be here) if the global call was actually a periodic worker
//    i dont have enough info to decide if it should be a period worker or not
    private fun fetchGoalsWorker(isGlobal: Boolean){
        if(isGlobal) {
            if (viewModel.fetchGoalsTime.isBefore(LocalDateTime.now().minusHours(GLOBAL_CALLS_INTERVAL))) {
                viewModel.fetchGoalsTime = LocalDateTime.now()
                FetchGoalsWorker.startUniqueGlobalTask(this)
            }
        } else {
            val uuid = FetchGoalsWorker.startUniqueUITask(this)
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(uuid)
                    .observe(this, { workInfo ->
                        if (workInfo != null) {
                            if(workInfo.state == WorkInfo.State.SUCCEEDED) {
                                viewModel.fetchGoalsTime = LocalDateTime.now()
                            } else {
                                FetchGoalsWorker.getProgressResult(workInfo)?.let {
                                    if (GenericStateCard.isListGenericCard(viewModel.goals.value?.toMutableList())) {
                                        if (it.status.isFailed) {
                                            goalsAdapter.setList(GenericStateCard.getErrorEmptyList(it.status))
                                        }
                                    }
                                }
                            }
                        }
                    })

        }
    }

    private fun genericCardErrorBlock(emptyViews: GenericStateCardErrorViews, errorView: GenericStateCardErrorViews, isFullHeight: Boolean, state: NetworkState?) {
        when {
            state?.isEmpty == true -> {
                emptyViews.icon?.apply {
                    removeView()
                }
                emptyViews.title?.removeView()
                emptyViews.body?.apply {
                    showView()
                }
                emptyViews.button?.removeView()
            }
            state?.isFailed == true -> {
                when (state.error?.serverError) {
                    ServerErrors.NO_INTERNET -> {
                        if (isFullHeight) {
                            BigNoInternetErrors(this, errorView)
                        } else {
                            //here we do not have pagination issues
                        }
                    }
                    ServerErrors.API -> {
                        //here we do not check for specific error codes
                        if (isFullHeight) {
                            BigGenericOtherErrors(this, errorView)
                        } else {
                            //here we do not have pagination issues
                        }
                    }
                    else -> {
                        if (isFullHeight) {
                            BigGenericOtherErrors(this, errorView)
                        } else {
                            //here we do not have pagination issues
                        }
                    }
                }
            }
        }
    }

    private fun itemClickBlock(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, index: Int, item: Goal, type: Enum<*>) {
        when (type) {
            ClickType.CARD -> {
                navigationDebouncer.debounceRunFirst {
                    startActivity(callManager.goalDetail(this, item.id))
                }
            }
        }
        adapter.notifyItemChanged(index)
    }

    private fun genericCardClickBlock(type: ClickType, card: GenericStateCard) {
        clickDebouncer.debounceRunFirst {
            when (type) {
                ClickType.ERROR -> {
                    goalsAdapter.setList(GenericStateCard.getLoadingEmptyList())
                    fetchGoalsWorker(false)
                }
                else -> { /* Do nothing here ... */
                }
            }
        }
    }


}
