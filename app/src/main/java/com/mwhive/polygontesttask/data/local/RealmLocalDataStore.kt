package com.mwhive.polygontesttask.data.local

import com.mwhive.polygontesttask.domain.models.polygon.PolygonModel
import com.mwhive.polygontesttask.domain.models.polygon.PolygonRealm
import com.mwhive.polygontesttask.utilsandextensions.coordListToRealm
import com.mwhive.polygontesttask.utilsandextensions.realmListToList
import io.realm.Realm


/**
 * Created by Denis Kolomiets on 11-Dec-18.
 */

object RealmLocalDataStore : LocalDataStore {

    override fun savePolygon(polygon: PolygonModel) {
        val rlmObj = PolygonRealm(polygon.id, polygon.tag, polygon.message, polygon.photoLink, polygon.date, coordListToRealm(polygon.polygonPoints))
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction{it.copyToRealmOrUpdate(rlmObj)}
        realm.close()
    }

    override fun updatePolygon(polygon: PolygonModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPolygons(): List<PolygonModel> {
        val polygonsList = mutableListOf<PolygonModel>()
        val realm = Realm.getDefaultInstance()
        val polygonsRealmList = realm.where(PolygonRealm::class.java).findAll()
        polygonsRealmList.forEach{polygonsList.add(PolygonModel(it.id, it.tag, it.message, it.photoLink, it.date, realmListToList(it.latLngRealm!!)))}
        realm.close()


        return polygonsList
    }

    override fun getPolygonByTag(tag: String): PolygonModel? {
        var polygon:PolygonModel? = null
        val realm = Realm.getDefaultInstance()
        val findPolygon = realm.where(PolygonRealm::class.java).equalTo("tag", tag).findFirst()
        findPolygon?.let {polygon = PolygonModel(findPolygon.id, findPolygon.tag, findPolygon.message, findPolygon.photoLink, findPolygon.date, realmListToList(findPolygon.latLngRealm!!))}
        realm.close()

        return  polygon
    }

    override fun deletePolygonByTag(tag: String) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction { realm -> realm.where(PolygonRealm::class.java).equalTo("tag", tag).findAll().deleteAllFromRealm() }
        realm.close()
    }
}