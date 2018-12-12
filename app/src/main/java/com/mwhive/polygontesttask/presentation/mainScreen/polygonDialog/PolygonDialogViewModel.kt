package com.mwhive.polygontesttask.presentation.mainScreen.polygonDialog

import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.mwhive.polygontesttask.base.BaseViewModel
import com.mwhive.polygontesttask.domain.Repository
import com.mwhive.polygontesttask.domain.models.polygon.PolygonModel
import com.mwhive.polygontesttask.utilsandextensions.SingleLiveEvent
import com.mwhive.polygontesttask.utilsandextensions.extensions.cropToString
import com.mwhive.polygontesttask.utilsandextensions.extensions.toFormattedDate
import timber.log.Timber
import java.io.File
import java.util.*


/**
 * Created by Denis Kolomiets on 10-Dec-18.
 */

class PolygonDialogViewModel : BaseViewModel() {


    var polygonTag = ""
    var polygonData: PolygonModel? = null
    var pickedImage: File? = null

    val currentDate = MutableLiveData<String>()
    val tagLiveData = MutableLiveData<String>()
    val polygonAreaLiveData = MutableLiveData<String>()
    val infoTextLiveData = MutableLiveData<String>()
    val imageUri = MutableLiveData<String>()

    val isReadyForDismiss = SingleLiveEvent<Boolean>()

    fun setTag(polygonTag: String, polygonPoints: MutableList<LatLng>) {
        this.polygonTag = polygonTag

        polygonData = repository.getPolygonByTag(polygonTag)

        if(polygonData == null ) {
            polygonData = PolygonModel()
            polygonData?.tag = polygonTag
            polygonData?.polygonPoints?.addAll(polygonPoints)
        }

        calculateAreaAndPoints(polygonData!!.polygonPoints)

        with(polygonData!!) {
            setDateToView(date)
            tagLiveData.postValue(tag)
            infoTextLiveData.postValue(message)
            setImageUriToLiveData(photoLink)
        }

    }

    private fun transformLatLngToList(polygonPoints: List<List<Double>>): List<LatLng> {
        val list = mutableListOf<LatLng>()
        polygonPoints.forEach { list.add(LatLng(it[0],it[1])) }
        return list
    }

    private fun transformListToLatLng(polygonPoints: List<LatLng>): List<List<Double>> {
        val list = mutableListOf<List<Double>>()
        polygonPoints.forEach { list.add(listOf(it.latitude, it.longitude)) }
        return list
    }

    private fun calculateAreaAndPoints(polygonPoints: MutableList<LatLng>) {
        polygonAreaLiveData.postValue(
            "${(SphericalUtil.computeArea(polygonPoints) / 1000000).cropToString(2)} km2"
        )
    }

    fun setDateToModel(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        polygonData!!.date = GregorianCalendar(year, monthOfYear, dayOfMonth).timeInMillis / 1000
        Timber.w("Polygon Date ${polygonData!!.date}")
        setDateToView(polygonData!!.date)
    }



    private fun setDateToView(timestamp: Long) {

        if (timestamp == 0L) {
            currentDate.postValue((System.currentTimeMillis() / 1000).toFormattedDate())
        } else currentDate.postValue(timestamp.toFormattedDate())
    }

    private fun setImageUriToLiveData(uri: String) {
        Timber.w("Uri: $uri")
        if (uri.isNotEmpty()) {
            imageUri.postValue(uri)
        }
    }

    fun saveImage(path: String) {
        polygonData!!.photoLink = path


    }

    fun saveData(message: String) {
        polygonData?.message = message
        Timber.i("Polygon info $polygonData")

        repository.savePolygon(polygonData!!)

        isReadyForDismiss.postValue(true)
    }


}