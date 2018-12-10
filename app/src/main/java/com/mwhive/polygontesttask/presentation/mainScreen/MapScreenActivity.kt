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
import android.support.v7.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mvc.imagepicker.ImagePicker
import com.mwhive.devmindtestproject.utilsandextensions.visibleOrGone
import com.mwhive.polygontesttask.R
import com.mwhive.polygontesttask.base.BaseActivity
import com.mwhive.polygontesttask.presentation.mainScreen.polygonDialog.DialogListener
import com.mwhive.polygontesttask.presentation.mainScreen.polygonDialog.PolygonDialogFragment
import com.mwhive.polygontesttask.utilsandextensions.extensions.toast
import kotlinx.android.synthetic.main.map_screen.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File

class MapScreenActivity : BaseActivity<MapScreenViewModel>(), OnMapReadyCallback {


    companion object {

        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MapScreenActivity::class.java)
            //here we could put extra if needed. But not in this case
            //intent.putExtra(EXTRA_ID, data)

            return intent
        }
    }

    var map: GoogleMap? = null

    val polygonList = mutableListOf<Polygon>()

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
            Timber.i("drawPolygon state ${viewModel.isDrawingMode}")
        }

        deleteLastPointButton.setOnClickListener {
            viewModel.deletePolygon(0)
        }
    }

    fun createNewPolygon() {
        viewModel.createPolygonOptions()

    }

    override fun onBindLiveData() {
        super.onBindLiveData()

        observe(viewModel.isDrawingMode) { state ->
            state?.let {
                deleteLastPointButton.visibleOrGone(state)
                when (state) {
                    true -> {
                        drawPolygonFAB.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.colorEditOn))
                        drawPolygonFAB.setImageResource(R.drawable.ic_done_black_24dp)
                    }
                    false -> {
                        drawPolygonFAB.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.colorEditOff))
                        drawPolygonFAB.setImageResource(R.drawable.ic_add_black_24dp)
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
            it?.let { points ->
                when {
                    points.isEmpty() -> {
                        //deletePolygon(0)
                    }
                    points.size == 1 -> {
                    }
                    points.size == 2 -> {
                    }
                    points.size == 3 -> {
                        addPolygonToMap(viewModel.currentPolygonOptions!!, "polygon_")
                    }
                    points.size > 3 -> {
                        updatePolygonOnMap(points)
                    }
                }

            }
        }

        observe(viewModel.deletePolygonLiveData) {
            if (it!!) deletePolygon(0)
        }
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
            viewModel.currentPolygon = it
            Timber.i("selected polygon: \n $it.")
            openDataDialog(it)
        }

    }

    private fun addPolygonToMap(polygonOptions: PolygonOptions?, tag: String) {
        map?.let {
            polygonList.add(map!!.addPolygon(polygonOptions))
            polygonList[polygonList.size - 1].tag = "${tag}${polygonList.size - 1}"
        }
    }

    private fun updatePolygonOnMap(points: List<LatLng>) {
        polygonList[0].points = points
    }

    private fun openDataDialog(polygon: Polygon?) {

        val polygonTag = polygon?.tag.toString()

        if (viewModel.isInRange()!!) {

            PolygonDialogFragment.newInstance(polygonTag, viewModel.currentPoints, dialogListener)
                .show(supportFragmentManager, "PolygonDialogFragment")
        } else {
            toast("Your Current Location is outside polygon ${polygon?.tag}")
        }
    }

    private val dialogListener = object : DialogListener {

    }

    private fun deletePolygon(id: Int) {
        polygonList.clear()
        map?.clear()
        map?.addMarker(locationMarkerOptions)
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

        supportFragmentManager.findFragmentByTag("PolygonDialogFragment")?.
            onActivityResult(requestCode, resultCode, data)
    }
}
