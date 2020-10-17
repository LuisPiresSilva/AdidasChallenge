package com.adidas.luissilva.ui.screens.goals

import androidx.recyclerview.widget.RecyclerView
import com.adidas.luissilva.R
import com.adidas.luissilva.data.common.NetworkState
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.databinding.ItemGoalBinding
import com.adidas.luissilva.ui.base.adapter.*

class GoalsAdapter(
        private val genericCardClickListener: (ClickType, GenericStateCard) -> Unit = { _, _ -> },
        private val clickListener: (adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, index: Int, obj: Goal, type: Enum<*>) -> Unit = { _, _, _, _ -> },
        private val genericCardErrorListener: (emptyViews: GenericStateCardErrorViews, errorViews: GenericStateCardErrorViews, isFullHeight: Boolean, contextualObject: NetworkState?) -> Unit = { _, _, _, _ -> })
    : BaseAdapter<Goal, ItemGoalBinding, GoalViewHolder, GoalsDiffCallback>(GoalViewHolder::class, Goal::class,
        genericCardClickListener, clickListener, genericCardErrorListener) {

    override val adapterDiff = GoalsDiffCallback()
    override val itemLayout = R.layout.item_goal


}

class GoalsDiffCallback : BaseDiffCallback<Goal>(Goal::class) {

    override fun itemsTheSame(oldItem: Goal, newItem: Goal) =
            oldItem.id == newItem.id


    override fun contentsTheSame(oldItem: Goal, newItem: Goal) =
            oldItem.description == newItem.description && oldItem.goal == newItem.goal
                    && oldItem.title == newItem.title
                    && oldItem.reward.trophy == newItem.reward.trophy
                    && oldItem.reward.points == newItem.reward.points

}