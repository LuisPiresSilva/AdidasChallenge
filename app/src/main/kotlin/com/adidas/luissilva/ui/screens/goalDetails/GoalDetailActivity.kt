package com.adidas.luissilva.ui.screens.goalDetails


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.adidas.luissilva.R
import com.adidas.luissilva.data.enum.CountType
import com.adidas.luissilva.data.enum.ExerciseType
import com.adidas.luissilva.data.enum.GoogleFitStrategy
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.databinding.ActivityGoalDetailBinding
import com.adidas.luissilva.ui.base.BaseActivity
import com.adidas.luissilva.utils.helper.extensions.android.getColor
import com.adidas.luissilva.utils.helper.extensions.removeView
import com.adidas.luissilva.utils.helper.extensions.showView
import com.adidas.luissilva.utils.helper.extensions.time.withZeroTime
import com.adidas.luissilva.utils.manager.CallManager
import com.adidas.luissilva.utils.manager.PreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import org.threeten.bp.*
import permissions.dispatcher.PermissionUtils
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Activity to show goal progress.
 */
class GoalDetailActivity : BaseActivity<ActivityGoalDetailBinding, GoalDetailViewModel>() {

    companion object {
        const val KEY_ID = "KEY_ID"

        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1896
    }

    override var drawBehindStatusBar = true
    override var darkStatusIcons = true


    override fun layoutToInflate() = R.layout.activity_goal_detail

    override fun getViewModelClass() = GoalDetailViewModel::class.java

    private var goalID = ""

    override fun getArguments(arguments: Bundle) {
        goalID = arguments.getString(KEY_ID, goalID)
        if (goalID.isBlank()) {
            //show some error and finish on click
            finishAfterTransition()
        }
    }

    private val locationPermissionCode = 0


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        dataBinding.goalsLogo.x = ((dataBinding.topAppBar.width / 2) - (dataBinding.goalsLogo.width / 2)).toFloat()
    }

    override fun doOnCreated() {
        setSupportActionBar(dataBinding.topAppBar)
        dataBinding.topAppBar.setNavigationIcon(R.drawable.ic_back)
        dataBinding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel.goal.observe(this, {
            if(it != null) {
                dataBinding.goalDetailId.text = it.description

                if (!PermissionUtils.hasSelfPermissions(this, Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION),
                                locationPermissionCode)
                    }
                } else {
                    googleAuth()
                }
            } else {
                viewModel.goalDetailState.postValue(GoalDetailState.Error(
                        "Error\nSeems that this goal was deleted", "Exit", { finishAfterTransition() }
                ))
            }
        })
        viewModel.getGoal(goalID)




        viewModel.goalDetailState.observe(this, {goalState ->
            when (goalState) {
                is GoalDetailState.Loading -> {
                    dataBinding.goalDetailProgressIndicator.isIndeterminate = true
                    dataBinding.goalDetailProgressIndicator.trackColor = android.R.color.transparent.getColor(this)

                    dataBinding.goalDetailErrorContainer.removeView()
                    dataBinding.goalDetailProgressText.removeView()
                    dataBinding.goalDetailDataContainer.showView()
                    dataBinding.goalDetailProgressIndicator.showView()
                }
                is GoalDetailState.Results -> {
                    dataBinding.goalDetailProgressIndicator.isIndeterminate = false
                    dataBinding.goalDetailProgressIndicator.trackColor = R.color.black_10pct.getColor(this)
                    dataBinding.goalDetailProgressIndicator.setProgressCompat(goalState.progress, true)

                    if (goalState.progress == 100) {
                        dataBinding.goalDetailProgressText.text = "Consgratulations\nYou won ${viewModel.goal.value?.reward?.points} points"
                    } else {
                        dataBinding.goalDetailProgressText.text = "${goalState.progress}%"
                    }

                    dataBinding.goalDetailErrorContainer.removeView()
                    dataBinding.goalDetailDataContainer.showView()
                    dataBinding.goalDetailProgressText.showView()
                    dataBinding.goalDetailProgressIndicator.showView()

                }
                is GoalDetailState.Error -> {

                    dataBinding.goalDetailErrorTextBody.text = goalState.message
                    dataBinding.goalDetailErrorBtnRetry.setOnClickListener {
                        goalState.action()
                    }

                    dataBinding.goalDetailDataContainer.removeView()
                    dataBinding.goalDetailErrorContainer.showView()

                }
            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleAuth()
                } else {
                    showError("not accepted",
                            "should we present anything to user, automaticaly navigate, tell user to press later?\n\nshould we only warn user if he only allows location with app running in foreground??",
                            "ok",
                            { finishAfterTransition() },
                            null,
                            { finishAfterTransition() })
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    var fitnessOptions: GoogleSignInOptionsExtension = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

    private fun googleAuth() {
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                    account,
                    fitnessOptions)
        } else {
            accessGoogleFit(viewModel.goal.value!!)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit(viewModel.goal.value!!)
            }
        }
    }


    private fun accessGoogleFit(goal: Goal) {
        val startTime = OffsetDateTime.now().withZeroTime().toEpochSecond()
        val endTime = OffsetDateTime.now().plusDays(1).withZeroTime().toEpochSecond()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        when (GoogleFitStrategy.convert(goal.type)) {
            GoogleFitStrategy.SPECIFIC -> {
                val activity = ExerciseType.convert(goal.type)
                val countType = CountType.convert(goal.type)
                if (activity != ExerciseType.UNKNOWN
                        && countType != CountType.UNKNOWN) {
                    specific(goal, account, startTime, endTime, activity, countType)
                } else {
                    viewModel.goalDetailState.postValue(GoalDetailState.Error(
                            "Error\nThis goal is not supported", "Exit", { finishAfterTransition() }
                    ))
                }

            }
            GoogleFitStrategy.GENERIC -> {
                val countType = CountType.convert(goal.type)
                if (countType != CountType.UNKNOWN) {
                    generic(goal, account, startTime, endTime, countType)
                } else {
                    viewModel.goalDetailState.postValue(GoalDetailState.Error(
                            "Error\nThis goal is not supported", "Exit", { finishAfterTransition() }
                    ))
                }
            }
        }

    }


    private fun specific(goal: Goal, account: GoogleSignInAccount, startTime: Long, endTime: Long, activity: ExerciseType, count: CountType) {
        val sessionRequest = SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.SECONDS)
                .read(when (count) {
                    CountType.STEP -> DataType.TYPE_STEP_COUNT_DELTA
                    CountType.DISTANCE -> DataType.TYPE_DISTANCE_DELTA
                    else -> DataType.TYPE_STEP_COUNT_DELTA //should never reach here
                })
                .readSessionsFromAllApps()
                .build()

        Fitness.getSessionsClient(this, account)
                .readSession(sessionRequest)
                .addOnSuccessListener { response ->

                    var result = 0f
                    response?.sessions?.forEach { session ->
                        if (session.activity.contains(activity.name, ignoreCase = true)) {
                            response.getDataSet(session).forEach { dataset ->
                                dataset.dataPoints.forEach {
                                    when (count) {
                                        CountType.STEP -> result += it.getValue(Field.FIELD_STEPS)?.asFloat()
                                                ?: 0f
                                        CountType.DISTANCE -> result += it.getValue(Field.FIELD_DISTANCE)?.asFloat()
                                                ?: 0f
                                        else -> result = 0f
                                    }
                                }
                            }
                        }

                    }

                    val progress = ((result * 100) / goal.goal).toInt().run { if (this !in 0..100) 100 else this }

                    viewModel.goalDetailState.postValue(GoalDetailState.Results(progress))

                }
                .addOnFailureListener { e ->
                    viewModel.goalDetailState.postValue(GoalDetailState.Error(
                            "Error querying your data", "retry", { accessGoogleFit(goal) }
                    ))
                }
                .addOnCompleteListener {
                    Timber.i("Session Completed()")
                }
    }


    private fun generic(goal: Goal, account: GoogleSignInAccount, startTime: Long, endTime: Long, count: CountType) {
        val readRequest = DataReadRequest.Builder()
                .read(when (count) {
                    CountType.STEP -> DataType.TYPE_STEP_COUNT_DELTA
                    CountType.DISTANCE -> DataType.TYPE_DISTANCE_DELTA
                    else -> DataType.TYPE_STEP_COUNT_DELTA //should never reach here
                })
                .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build()

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener { response ->

                    var result = 0f
                    response?.buckets?.forEach { bucket ->
                        bucket.dataSets.forEach { dataset ->
                            dataset.dataPoints.forEach {
                                when (count) {
                                    CountType.STEP -> result += it.getValue(Field.FIELD_STEPS)?.asInt()
                                            ?: 0
                                    CountType.DISTANCE -> result += it.getValue(Field.FIELD_DISTANCE)?.asFloat()
                                            ?: 0f
                                    else -> result = 0f
                                }
                            }
                        }
                    }

                    val progress = ((result * 100) / goal.goal).toInt().run { if (this !in 0..100) 100 else this }
                    viewModel.goalDetailState.postValue(GoalDetailState.Results(progress))
                }
                .addOnFailureListener { e ->
                    viewModel.goalDetailState.postValue(GoalDetailState.Error(
                            "Error querying your data", "retry", { accessGoogleFit(goal) }
                    ))
                }
                .addOnCompleteListener {
                    Timber.i("History Completed()")
                }
    }


}
