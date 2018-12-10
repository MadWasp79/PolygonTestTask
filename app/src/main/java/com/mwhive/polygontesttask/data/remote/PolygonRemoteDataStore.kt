package com.mwhive.polygontesttask.data.remote

import com.mwhive.polygontesttask.domain.models.route.DirectionsResponse
import io.reactivex.Flowable


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */

object PolygonRemoteDataStore : RemoteDataStore {

    private val api: Api by lazy { RetrofitCreator.initApi() }

    override fun getDirections(
        origin: String,
        destination: String,
        sensor: Boolean,
        mode: String,
        apiKey: String
    ): Flowable<DirectionsResponse> =
        api.getDirections(origin = origin, destination = destination, sensor = sensor, mode = mode, apiKey = apiKey)


}