package com.mwhive.polygontesttask.domain

import com.mwhive.polygontesttask.data.local.LocalDataStore
import com.mwhive.polygontesttask.data.local.RealmLocalDataStore
import com.mwhive.polygontesttask.data.remote.PolygonRemoteDataStore
import com.mwhive.polygontesttask.data.remote.RemoteDataStore
import com.mwhive.polygontesttask.domain.models.polygon.PolygonModel
import com.mwhive.polygontesttask.domain.models.route.DirectionsResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */

object Repository: RemoteDataStore, LocalDataStore {


    private val localDataStore:LocalDataStore = RealmLocalDataStore



    override fun savePolygon(polygon: PolygonModel) = localDataStore.savePolygon(polygon)

    override fun updatePolygon(polygon: PolygonModel) = localDataStore.updatePolygon(polygon)

    override fun getPolygonByTag(tag:String):PolygonModel? = localDataStore.getPolygonByTag(tag)

    override fun getPolygons(): List<PolygonModel> = localDataStore.getPolygons()

    override fun deletePolygonByTag(tag: String) = localDataStore.deletePolygonByTag(tag)


    //didn't used in current project.
    private val remoteDataStore :RemoteDataStore = PolygonRemoteDataStore

    //didn't used in current project (this returns path between two points)
    override fun getDirections(
        origin: String,
        destination: String,
        sensor: Boolean,
        mode: String,
        apiKey: String
    ): Flowable<DirectionsResponse>  = remoteDataStore.getDirections(origin,destination,sensor,mode, apiKey)




}