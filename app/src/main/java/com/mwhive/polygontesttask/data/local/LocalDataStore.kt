package com.mwhive.polygontesttask.data.local

import com.mwhive.polygontesttask.domain.models.polygon.PolygonModel


/**
 * Created by Denis Kolomiets on 07-Dec-18.
 */

interface LocalDataStore {

    fun savePolygon(polygon: PolygonModel)
    fun updatePolygon(polygon: PolygonModel)
    fun getPolygons():List<PolygonModel>
    fun getPolygonByTag(tag:String):PolygonModel?
    fun deletePolygonByTag(tag:String)
}