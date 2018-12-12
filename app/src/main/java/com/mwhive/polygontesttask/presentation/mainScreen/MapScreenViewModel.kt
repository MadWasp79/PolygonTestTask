package com.mwhive.polygontesttask.presentation.mainScreen

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil
import com.mvhive.polygontesttask.tools.Constants.COLOR_ORANGE_TRANSP_ARGB
import com.mvhive.polygontesttask.tools.Constants.COLOR_GREEN_ARGB
import com.mvhive.polygontesttask.tools.Constants.COLOR_ORANGE_ARGB
import com.mvhive.polygontesttask.tools.Constants.COLOR_PURPLE_ARGB
import com.mvhive.polygontesttask.tools.Constants.COLOR_GREEN_TRANSP_ARGB
import com.mvhive.polygontesttask.tools.Constants.DASH
import com.mvhive.polygontesttask.tools.Constants.GAP
import com.mwhive.polygontesttask.base.BaseViewModel
import timber.log.Timber


/**
 * Created by Denis Kolomiets on 08-Dec-18.
 */

class MapScreenViewModel : BaseViewModel(), LifecycleObserver {

    val myCurrentLocation = MutableLiveData<Location>()
    val pointsLive = MutableLiveData<List<LatLng>>()
    val deletePolygonLiveData = MutableLiveData<Boolean>()
    val mapOfPolygonOptionsWithTags = MutableLiveData<Map<String, PolygonOptions>>()


    var newPolygonTag:String = ""
    var selectedPolygonTag:String = ""
    var isDrawingMode = MutableLiveData<Boolean>()
    var isStarted = true
    var currentPolygonOptions: PolygonOptions? = null
    val currentPoints = mutableListOf<LatLng>()


    init {
        isDrawingMode.postValue(false)
        showLoading()
    }



    fun isStarted(state: Boolean) {
        isStarted = state
        if (!state) hideLoading()
    }

    fun createPolygonOptions() {
        currentPolygonOptions = null
        currentPolygonOptions = PolygonOptions()
            .clickable(true)
            .strokeColor(COLOR_ORANGE_ARGB)
            .strokePattern(listOf(GAP, DASH))
            .strokeColor(COLOR_PURPLE_ARGB)
            .fillColor(COLOR_ORANGE_TRANSP_ARGB)

    }

    fun addPoint(latLng: LatLng) {
        if (isDrawingMode.value!!) {
            currentPoints.add(latLng)
            if (currentPoints.size <= 3) currentPolygonOptions?.add(latLng)
            pointsLive.postValue(currentPoints)
        }
    }

    fun deletePolygon(tag:String) {
        repository.deletePolygonByTag(tag)
        if (isDrawingMode.value!!) {
            currentPoints.clear()
//            deletePolygonLiveData.postValue(true)
            isDrawingMode.postValue(false)


        }
    }

    fun getPolygonsFromRealm() {
        val mapOfOptions = mutableMapOf<String, PolygonOptions>()
        repository.getPolygons().forEach {
            Timber.d("Tag from db: $it.tag")
            mapOfOptions[it.tag] = PolygonOptions()
                .clickable(true)
                .strokeColor(COLOR_GREEN_ARGB)
                .strokePattern(listOf(GAP, DASH))
                .strokeColor(COLOR_PURPLE_ARGB)
                .fillColor(COLOR_GREEN_TRANSP_ARGB)
                .addAll(it.polygonPoints)
        }

        mapOfPolygonOptionsWithTags.postValue(mapOfOptions)


    }

    fun isInRange(polygon: Polygon?): Boolean? {
        val points = polygon!!.points
        return myCurrentLocation.value?.let {
            PolyUtil.containsLocation(
                LatLng(myCurrentLocation.value!!.latitude, myCurrentLocation.value!!.longitude),
                points, false
            )
        }
    }

    fun createNewPolygon() {
        currentPoints.clear()
        pointsLive.postValue(currentPoints)
    }

}