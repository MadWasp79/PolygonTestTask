package com.mwhive.polygontesttask.presentation.mainScreen

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil
import com.mvhive.polygontesttask.tools.Constants.COLOR_BLUE_TRANSP_ARGB
import com.mvhive.polygontesttask.tools.Constants.COLOR_ORANGE_ARGB
import com.mvhive.polygontesttask.tools.Constants.COLOR_PURPLE_ARGB
import com.mvhive.polygontesttask.tools.Constants.DASH
import com.mvhive.polygontesttask.tools.Constants.GAP
import com.mwhive.polygontesttask.base.BaseViewModel
import io.reactivex.Observable


/**
 * Created by Denis Kolomiets on 08-Dec-18.
 */

class MapScreenViewModel : BaseViewModel(), LifecycleObserver {

    val myCurrentLocation = MutableLiveData<Location>()
    val polygonLive = MutableLiveData<PolygonOptions>()
    val pointsLive = MutableLiveData<List<LatLng>>()
    val deletePolygonLiveData = MutableLiveData<Boolean>()

    var isDrawingMode = MutableLiveData<Boolean>()
    var isStarted = true
    var currentPolygonOptions: PolygonOptions? = null
    val currentPoints = mutableListOf<LatLng>()
    var currentPolygon: Polygon? = null


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
            .fillColor(COLOR_BLUE_TRANSP_ARGB)

    }

    fun addPoint(latLng: LatLng) {
        if (isDrawingMode.value!!) {
            currentPoints.add(latLng)
            if (currentPoints.size <= 3) currentPolygonOptions?.add(latLng)
            pointsLive.postValue(currentPoints)
        }
    }

    fun deletePolygon(id: Int) {
        if (isDrawingMode.value!!) {
            currentPoints.clear()
            deletePolygonLiveData.postValue(true)
            isDrawingMode.postValue(false)
            repository.deletePolygons()

        }
    }


    fun isInRange(): Boolean? {
        return myCurrentLocation.value?.let {
            PolyUtil.containsLocation(
                LatLng(myCurrentLocation.value!!.latitude, myCurrentLocation.value!!.longitude),
                currentPoints, false
            )
        }
    }

}