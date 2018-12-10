package com.mwhive.polygontesttask.data.remote

import com.mwhive.polygontesttask.domain.models.route.DirectionsResponse
import io.reactivex.Flowable


/**
 * Created by Denis Kolomiets on 07-Dec-18.
 */

interface RemoteDataStore {

    fun getDirections(
            origin: String,
            destination: String,
            sensor: Boolean,
            mode: String,
            apiKey: String
    ): Flowable<DirectionsResponse>
}