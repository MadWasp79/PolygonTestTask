package com.mwhive.polygontesttask.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mwhive.polygontesttask.utilsandextensions.extensions.inflate


/**
 * Created by Denis Kolomiets on 10-Dec-18.
 */

abstract class BaseFragmentDialog<VM : BaseViewModel> : DialogFragment() {

    protected val viewModel: VM by lazy { setupViewModel() }

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun viewModelClass(): Class<VM>

    protected abstract fun onChangeProgressBarVisibility(isVisible: Boolean)

    protected abstract fun onShowError(message: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        onBindLiveData()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId(), container)

    protected open fun onBindLiveData() {
        observe(viewModel.progressBarVisibility, ::onChangeProgressBarVisibility)
        observe(viewModel.errorVisibility, ::onShowError)
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }




    protected fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this, Observer { value ->
            value?.let(onChanged)
        })
    }

    private fun setupViewModel() = ViewModelProviders.of(this).get(viewModelClass())

}