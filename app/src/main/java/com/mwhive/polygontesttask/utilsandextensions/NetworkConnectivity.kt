package com.mwhive.polygontesttask.utilsandextensions


import com.mwhive.polygontesttask.App
import com.mwhive.polygontesttask.data.remote.error.NoInternetConnectionException
import com.mwhive.polygontesttask.utilsandextensions.extensions.hasNetworkConnection
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable


object NetworkConnectivity {

    fun getStateFlowable(): Flowable<Boolean> = Flowable.create({ emitter ->
        if (App.applicationContext().hasNetworkConnection()) {
            emitter.onNext(true)
        } else {
            emitter.onError(NoInternetConnectionException())
        }
        emitter.onComplete()
    }, BackpressureStrategy.LATEST)
}