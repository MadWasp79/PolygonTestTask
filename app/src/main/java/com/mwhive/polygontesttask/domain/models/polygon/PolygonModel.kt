package com.mwhive.polygontesttask.domain.models.polygon

import com.google.android.gms.maps.model.LatLng


/**
 * Created by Denis Kolomiets on 07-Dec-18.
 */

data class PolygonModel(
    var id: Int = -1,
    var tag: String = "",
    var message: String = "",
    var photoLink: String = "",
    var date: Long = 0L,
    var polygonPoints: MutableList<LatLng> = mutableListOf()
)