package com.mwhive.polygontesttask.domain.models.polygon

import io.realm.RealmObject


/**
 * Created by Denis Kolomiets on 11-Dec-18.
 */

open class LatLngRealm(
    var lat:Double = 0.0,
    var lng:Double = 0.0
):RealmObject()