package com.mwhive.polygontesttask.domain

import com.mwhive.polygontesttask.data.local.LocalDataStore
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


    //in real app we shoud change this to instance of Realm database and store all polygon data there
    private val listOfPolygons = mutableListOf<PolygonModel>()

    //in real app change to Realm database methods
    override fun savePolygon(polygon: PolygonModel) {
        listOfPolygons.add(polygon)
    }

    fun updatePolygon(polygon: PolygonModel) {
        if(listOfPolygons.isNotEmpty() && listOfPolygons.any { it.tag == polygon.tag }) {
            listOfPolygons.remove(listOfPolygons.filter { it.tag == polygon.tag }[0])
            listOfPolygons.add(polygon)
        } else {
            savePolygon(polygon)
        }
    }

    fun getPolygonByTag(tag:String):PolygonModel {
        return if(listOfPolygons.isNotEmpty() && listOfPolygons.any { it.tag == tag }) {
            listOfPolygons.filter { it.tag == tag }[0]
        } else PolygonModel()
    }

    fun deletePolygons() = listOfPolygons.clear()

    //in real app change to Realm database methods
    override fun getPolygons(): List<PolygonModel> {
        return listOfPolygons
    }


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