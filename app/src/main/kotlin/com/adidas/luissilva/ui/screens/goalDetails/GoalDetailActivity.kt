package com.adidas.luissilva.ui.screens.goalDetails


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adidas.luissilva.R
import com.adidas.luissilva.databinding.ActivityGoalDetailBinding
import com.adidas.luissilva.ui.base.BaseActivity
import com.adidas.luissilva.utils.manager.CallManager
import com.adidas.luissilva.utils.manager.PreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.SessionsClient
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field.FIELD_ACTIVITY
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import org.threeten.bp.OffsetDateTime
import permissions.dispatcher.PermissionUtils
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Activity to apply the goals screen.
 */
class GoalDetailActivity : BaseActivity<ActivityGoalDetailBinding, GoalDetailViewModel>() {

    companion object {
        const val KEY_ID = "KEY_ID"

        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1896
    }

    @Inject
    lateinit var callManager: CallManager

    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun layoutToInflate() = R.layout.activity_goal_detail

    override fun getViewModelClass() = GoalDetailViewModel::class.java

    private var goalID = ""

    override fun getArguments(arguments: Bundle) {
        goalID = arguments.getString(KEY_ID, goalID)
        if(goalID.isBlank()) {
            //show some error and finish on click
            finishAfterTransition()
        }
    }

    val locationPermissionCode = 0
    override fun doOnCreated() {

        viewModel.getGoal(goalID).observe(this, {
            dataBinding.goalDetailId.text = it.id
        })


        if (!PermissionUtils.hasSelfPermissions(this, Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermissionCode)
            }
        } else {
            googleAuth()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleAuth()
                } else {
                    showError("not accepted", "should we present anything to user, automaticaly navigate, tell user to press later?\n\nshould we only warn user if he only allows location with app running in foreground??", "ok")
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun googleAuth() {
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                    account,
                    fitnessOptions);
        } else {
            accessGoogleFit()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit()
            }
        }
    }


    private fun accessGoogleFit() {
        val startTime = OffsetDateTime.now().minusDays(3).toEpochSecond()
        val endTime = OffsetDateTime.now().toEpochSecond()


        val readRequest = DataReadRequest.Builder()

                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build()
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener { response: DataReadResponse? ->
                    // Use response data here
                    Timber.i("OnSuccess() - " + response?.dataSets.toString())
                    Timber.i("OnSuccess() - " + response?.buckets.toString())
                }
                .addOnFailureListener { e: Exception? -> Timber.i("OnFailure()" + e.toString()) }
                .addOnCompleteListener {
                    Timber.i("Completed()")
                }



    }


    var fitnessOptions: GoogleSignInOptionsExtension = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()




}
