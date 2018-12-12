package com.mwhive.polygontesttask.domain.models.polygon

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


/**
 * Created by Denis Kolomiets on 11-Dec-18.
 */

open class PolygonRealm(
    var id: Int = -1,
    @PrimaryKey
    var tag: String = "",
    var message: String = "",
    var photoLink: String = "",
    var date: Long = 0L,
    var latLngRealm: RealmList<LatLngRealm>? = null
) : RealmObject()