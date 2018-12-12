package com.mwhive.polygontesttask.presentation.launchScreen


import android.os.Bundle
import com.mwhive.polygontesttask.R
import com.mwhive.polygontesttask.base.BaseActivity
import com.mwhive.polygontesttask.presentation.mainScreen.MapScreenActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : BaseActivity<StartViewModel>() {

    override fun layoutResId(): Int = R.layout.activity_start

    override fun viewModelClass(): Class<StartViewModel> = StartViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        startActivityProceedBtn.setOnClickListener { proceedToMapScreen() }
    }

    private fun proceedToMapScreen() = startActivity(MapScreenActivity.newIntent(this))



    override fun onChangeProgressBarVisibility(isVisible: Boolean) {
        //skip
    }

    override fun onShowError(message: String) {
        //skip
    }


}
