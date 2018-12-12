package com.mwhive.polygontesttask.domain.models.polygon

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


/**
 * Created by Denis Kolomiets on 07-Dec-18.
 */

class PolygonModel(
    var id: Int = -1,
    var tag: String = "",
    var message: String = "",
    var photoLink: String = "",
    var date: Long = 0L,
    var polygonPoints: MutableList<LatLng> = mutableListOf()
) {
    override fun toString(): String {
        return "PolygonModel(id=$id, tag='$tag', message='$message', photoLink='$photoLink', date=$date, polygonPoints=$polygonPoints)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PolygonModel

        if (id != other.id) return false
        if (tag != other.tag) return false
        if (message != other.message) return false
        if (photoLink != other.photoLink) return false
        if (date != other.date) return false
        if (polygonPoints != other.polygonPoints) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + tag.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + photoLink.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + polygonPoints.hashCode()
        return result
    }


}

