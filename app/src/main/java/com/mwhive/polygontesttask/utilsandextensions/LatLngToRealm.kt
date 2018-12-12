package com.mwhive.polygontesttask.utilsandextensions

import com.google.android.gms.maps.model.LatLng
import com.mwhive.polygontesttask.domain.models.polygon.LatLngRealm
import io.realm.RealmList


/**
 * Created by Denis Kolomiets on 11-Dec-18.
 */

fun latLngToRealm(latLng: LatLng): LatLngRealm = LatLngRealm(latLng.latitude, latLng.longitude)

fun realmToLatLng(latLngRlm: LatLngRealm) = LatLng(latLngRlm.lat, latLngRlm.lng)

fun coordListToRealm(coords: List<LatLng>): RealmList<LatLngRealm> {
    val realmLst:RealmList<LatLngRealm> =  RealmList()
    coords.forEach { realmLst.add(latLngToRealm(it)) }
    return realmLst
}

fun realmListToList(coordsFromRealm:RealmList<LatLngRealm>):MutableList<LatLng> {
    val list = mutableListOf<LatLng>()
    coordsFromRealm.forEach { list.add(realmToLatLng(it)) }
    return list
}