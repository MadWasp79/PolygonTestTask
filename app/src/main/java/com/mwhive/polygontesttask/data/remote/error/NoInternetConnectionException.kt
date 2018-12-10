package com.mwhive.polygontesttask.data.remote.error


import com.mwhive.polygontesttask.App
import com.mwhive.polygontesttask.R

class NoInternetConnectionException
    : Exception(App.applicationContext().getString(R.string.exception_no_internet_connection))