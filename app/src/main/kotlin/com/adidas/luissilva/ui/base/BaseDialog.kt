package com.adidas.luissilva.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import com.adidas.luissilva.utils.helper.DebounceTimer
import javax.inject.Inject

abstract class BaseDialog<T : ViewDataBinding, VM : ViewModel> : DialogFragment(), HasAndroidInjector {

    interface Callback {
        fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent)
    }

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val dataBinding: T by lazy {
        DataBindingUtil.inflate<T>(LayoutInflater.from(context), layoutToInflate(), null, false)
    }

    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(getViewModelClass())
    }

    protected val clickDebouncer: DebounceTimer by lazy { DebounceTimer(lifecycle) }

    @LayoutRes
    abstract fun layoutToInflate(): Int

    abstract fun getViewModelClass() : Class<VM>

    abstract fun doOnCreated()

    override fun androidInjector() = androidInjector

    open fun getArguments(arguments: Bundle) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidSupportInjection.inject(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        initDataBinding()
        if (viewModel is LifecycleObserver)lifecycle.addObserver(viewModel as LifecycleObserver)
        if (arguments != null) {
            getArguments(requireArguments())
        }

        doOnCreated()
        return dataBinding.root
    }

    override fun onDestroy() {
        dataBinding.unbind()
        super.onDestroy()
    }

    private fun initDataBinding() {
        dataBinding.lifecycleOwner = this
    }

    fun show(fragment: Fragment) {
        show(fragment.childFragmentManager, javaClass.name)
    }

    fun show(activity: AppCompatActivity) {
        show(activity.supportFragmentManager, javaClass.name)
    }

    fun showForResult(fragment: Fragment, requestCode: Int) {
        setTargetFragment(fragment, requestCode)
        show(fragment.requireFragmentManager(), javaClass.name)
    }

    fun showForResult(activity: BaseActivity<*,*>, requestCode: Int) {
        var args = arguments
        if (args == null)
            args = Bundle()
        args.putInt(activity.javaClass.name + "requestCode", requestCode)
        arguments = args
        show(activity.supportFragmentManager, javaClass.name)
    }

    protected fun setResult(resultCode: Int) {
        setResult(resultCode, Intent())
    }

    protected fun setResult(resultCode: Int, data: Intent) {
        var callback: Callback? = null
        var requestCode = 0

        val fragment = targetFragment
        if (fragment != null && fragment is Callback) {
            callback = fragment
            requestCode = targetRequestCode
        } else if (fragment == null) {
            val activity = activity
            if (activity != null && activity is Callback) {
                callback = activity
                requestCode = requireArguments().getInt(activity.javaClass.name + "requestCode")
            }
        }

        callback?.onDialogResult(requestCode, resultCode, data)
    }

}