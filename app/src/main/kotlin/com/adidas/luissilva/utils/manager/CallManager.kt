package com.adidas.luissilva.utils.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.adidas.luissilva.ui.base.BaseActivity
import com.adidas.luissilva.ui.screens.goalDetails.GoalDetailActivity
import com.adidas.luissilva.ui.widgets.afm.FragmentCall
import javax.inject.Inject
import kotlin.reflect.KClass

class CallManager @Inject constructor() {

    //APP NAVIGATIONS/CALLS
    fun goalDetail(context: Context?, id: String) = Intent(context, GoalDetailActivity::class.java).apply {
        putExtra(GoalDetailActivity.KEY_ID, id)
    }

    //OUTSIDE NAVIGATIONS/CALLS
    private fun replaceFragment(activity: BaseActivity<*, *>, fragmentClass: KClass<out Fragment>, bundle: Bundle) {
        FragmentCall.init(activity, fragmentClass).setTransitionType(FragmentCall.TransitionType.REPLACE).setBundle(bundle).build()
    }

    private fun addFragment(activity: BaseActivity<*, *>, fragmentClass: KClass<out Fragment>, bundle: Bundle) {
        FragmentCall.init(activity, fragmentClass).setTransitionType(FragmentCall.TransitionType.ADD).setBundle(bundle).build()
    }

    fun openEmail(subject: String, email: String) = Intent(Intent.ACTION_SENDTO).apply {
        type = "plain/text"
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }

    fun openBrowser(url : String) = Intent(Intent.ACTION_VIEW).apply {
        val urlFixed = if(!url.startsWith("http://")){ "http://$url" } else {url}
        data = Uri.parse(urlFixed)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    fun openSettings() = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    fun openDial(phone: String) = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

}