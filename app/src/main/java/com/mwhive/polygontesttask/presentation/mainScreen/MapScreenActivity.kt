package com.mwhive.polygontesttask.presentation.mainScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mwhive.devmindtestproject.utilsandextensions.visibleOrGone
import com.mwhive.polygontesttask.R
import com.mwhive.polygontesttask.base.BaseActivity
import com.mwhive.polygontesttask.presentation.mainScreen.polygonDialog.DialogListener
import com.mwhive.polygontesttask.presentation.mainScreen.polygonDialog.PolygonDialogFragment
import com.mwhive.polygontesttask.utilsandextensions.extensions.toast
import com.mwhive.polygontesttask.utilsandextensions.extensions.toastD
import kotlinx.android.synthetic.main.map_screen.*
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.util.*

class MapScreenActivity : BaseActivity<MapScreenViewModel>(), OnMapReadyCallback {


    companion object {

        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MapScreenActivity::class.java)

            /**here we could put extra if needed. But not in this case*/
            /**intent.putExtra(EXTRA_ID, data)*/

            return intent
        }
    }

    var map: GoogleMap? = null

    private val polygonList = mutableListOf<Polygon>()
    private val pointsList = mutableListOf<Marker>()

    var locationMarkerOptions: MarkerOptions? = null

    override fun layoutResId(): Int = R.layout.map_screen

    override fun viewModelClass(): Class<MapScreenViewModel> = MapScreenViewModel::class.java

    override fun onChangeProgressBarVisibility(isVisible: Boolean) {
        progress_bar?.apply { visibleOrGone(isVisible) }
    }

    override fun onShowError(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.error)
            setMessage(message)
            create()
            show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (map == null) {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        EasyImage.configuration(this)
            .setImagesFolderName("PolygonImages")
            .saveInAppExternalFilesDir()
        setupListeners()
    }


    private fun setupListeners() {
        drawPolygonFAB.setOnClickListener {
            when (viewModel.isDrawingMode.value!!) {
                true -> {
                    viewModel.isDrawingMode.postValue(false)
                }
                false -> {
                    viewModel.isDrawingMode.postValue(true)
                    createNewPolygon()
                }
            }
        }

        deleteSelectedPolygonBtn.setOnClickListener {
            if (viewModel.selectedPolygonTag.isNotEmpty())
                deletePolygon(viewModel.selectedPolygonTag)
            else toast("Please select Polygon to Remove")
        }


    }

    private fun createNewPolygon() {
        viewModel.createPolygonOptions()
        viewModel.createNewPolygon()

    }

    override fun onBindLiveData() {
        super.onBindLiveData()

        observe(viewModel.isDrawingMode) { state ->
            state?.let {

                when (state) {
                    true -> {
                        messageTV.text = resources.getString(R.string.message_text2)
                        drawPolygonFAB.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.colorEditOn))
                        drawPolygonFAB.setImageResource(R.drawable.ic_done_black_24dp)
                        map?.setOnPolygonClickListener {  }
                    }
                    false -> {
                        pointsList.forEach { point -> point.remove() }
                        messageTV.text = resources.getString(R.string.message_text)
                        drawPolygonFAB.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.colorEditOff))
                        drawPolygonFAB.setImageResource(R.drawable.ic_add_black_24dp)
                        map?.setOnPolygonClickListener { onPolygonSelect(it) }
                    }
                }
            }
        }

        observe(viewModel.myCurrentLocation) {
            if (map != null) {

                locationMarkerOptions = MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )

                map?.addMarker(locationMarkerOptions)
            }
        }

        observe(viewModel.pointsLive) {
            if (it.isNotEmpty()) addMarker(it)
            Timber.i("Points size: ${it.size}")
            it?.let { points ->
                when {
                    points.size < 3 -> {
                    }
                    points.size == 3 -> {
                        val tag = "Polygon_${UUID.randomUUID().toString().subSequence(0, 8)}"
                        Timber.d(tag)
                        addPolygonToMap(viewModel.currentPolygonOptions!!, tag)
                        viewModel.newPolygonTag = tag
                    }
                    points.size > 3 -> {
                        updatePolygonOnMap(points, viewModel.newPolygonTag)
                    }
                }
            }
        }

        observe(viewModel.deletePolygonLiveData) {
//            if (it!!) deletePolygon(0)
        }

        observe(viewModel.mapOfPolygonOptionsWithTags) {
            addPolygonsFromRealmToMap(it)
        }
    }

    private fun addMarker(points: List<LatLng>) {
        val options = MarkerOptions()

        options.position(points[points.size - 1])
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.adjust_marker))
            .alpha(0.7f)
            .anchor(0.5f, 0.5f)
        pointsList.add(map?.addMarker(options)!!)

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    )
                }
            } else {
                getCurrentLocation()
            }
        } else {
            getCurrentLocation()
        }

        map?.animateCamera(CameraUpdateFactory.zoomTo(11.0f), 2000, null)

        map?.setOnMapClickListener { latLng ->
            viewModel.addPoint(latLng)
        }

        map?.setOnPolygonClickListener {
            onPolygonSelect(it)
        }

        viewModel.getPolygonsFromRealm()
    }

    private fun addPolygonToMap(polygonOptions: PolygonOptions?, tag: String) {
        map?.let {

            polygonList.add(map!!.addPolygon(polygonOptions))
            polygonList[polygonList.size - 1].tag = tag

        }
    }

    fun onPolygonSelect(it:Polygon) {
        changePolygonColorOnDeselect()
        viewModel.selectedPolygonTag = it.tag.toString()
//        toastD("Selected polygon: ${it.tag.toString()}")
//        viewModel.currentPolygon = it
        changeSelectedPolygonColor(it.tag.toString())
        openDataDialog(it)
    }



    private fun addPolygonsFromRealmToMap(realmPolygonOptions: Map<String, PolygonOptions>) {
        map?.let {
            for((k,v) in realmPolygonOptions) { addPolygonToMap(v,k) }
        }
    }

    private fun updatePolygonOnMap(points: List<LatLng>, tag: String) {
        polygonList.forEach { if (it.tag == tag) it.points = points }
    }

    private fun changeSelectedPolygonColor(tag:String) {
        polygonList.forEach { if (it.tag == tag) it.fillColor = ResourcesCompat.getColor(resources, R.color.blueTransp, null)}
    }

    private fun changePolygonColorOnDeselect() {
        if(viewModel.selectedPolygonTag.isNotEmpty()) {
            polygonList.forEach { if (it.tag == viewModel.selectedPolygonTag)
                it.fillColor = ResourcesCompat.getColor(resources, R.color.greenTransp, null)}
        }
    }

    private fun openDataDialog(polygon: Polygon?) {

        val polygonTag = polygon?.tag.toString()

        if (viewModel.isDrawingMode.value == false && viewModel.isInRange(polygon)!!) {

            PolygonDialogFragment.newInstance(polygonTag, polygon!!.points, dialogListener)
                .show(supportFragmentManager, "PolygonDialogFragment")
        } else {
            toast("Your Current Location is outside polygon ${polygon?.tag}")
        }
    }

    private val dialogListener = object : DialogListener {

    }

    private fun deletePolygon(tag: String) {
        polygonList.find { it.tag == tag }?.remove()
        pointsList.forEach { it.remove() }
        viewModel.deletePolygon(tag)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty()
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    getCurrentLocation()
                } else {
                    finish()
                }
                return
            }
            else -> {
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val currentLocation = LatLng(location.latitude, location.longitude)

            if (viewModel.isStarted) {
                map?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                viewModel.myCurrentLocation.postValue(location)
                viewModel.isStarted(false)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Timber.d("onStatusChanged provider: $provider, status: $status")
        }

        override fun onProviderEnabled(provider: String) {
            Timber.d("onProviderEnabled provider: $provider")
        }

        override fun onProviderDisabled(provider: String) {
            Timber.d("onProviderDisabled provider: $provider")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        supportFragmentManager.findFragmentByTag("PolygonDialogFragment")
            ?.onActivityResult(requestCode, resultCode, data)
    }
}
