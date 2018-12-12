package com.mwhive.polygontesttask.presentation.mainScreen.polygonDialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.mwhive.polygontesttask.R
import com.mwhive.polygontesttask.base.BaseFragmentDialog
import com.mwhive.polygontesttask.utilsandextensions.extensions.toast
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_polygon.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import java.util.*


/**
 * Created by Denis Kolomiets on 10-Dec-18.
 */

class PolygonDialogFragment : BaseFragmentDialog<PolygonDialogViewModel>() {

    companion object {
        private const val TAG = "PolygonDialogFragment"

        fun newInstance(tag: String, listOfPoints: List<LatLng>, listener: DialogListener) =
            PolygonDialogFragment().apply {
                this.listener = listener
                this.polygonTag = tag
                this.listOfPoints.addAll(listOfPoints)
            }
    }

    var polygonTag: String? = null
    var listener: DialogListener? = null
    val listOfPoints: MutableList<LatLng> = mutableListOf()
    val compositeDisposable = CompositeDisposable()

    override fun layoutResId(): Int = R.layout.dialog_polygon

    override fun viewModelClass(): Class<PolygonDialogViewModel> =
        PolygonDialogViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        polygonTag?.let { viewModel.setTag(polygonTag!!, listOfPoints) }
        setListeners()
        onBindLiveData()
        configureEasyImage()

    }

    private fun setListeners() {
        dateTV.setOnClickListener { selectDate() }
        imageView.setOnClickListener { addImage() }
        savePolygonDataBtn.setOnClickListener { saveDialog() }
        closeBtn.setOnClickListener { dismiss() }
    }

    override fun onBindLiveData() {
        super.onBindLiveData()
        observe(viewModel.currentDate) { dateTV.text = it }
        observe(viewModel.polygonAreaLiveData) { polygon_area_value.text = it }
        observe(viewModel.tagLiveData) { polygon_title.text = it }
        observe(viewModel.infoTextLiveData) { textInfoET.setText(it) }
        observe(viewModel.imageUri) { setImage(it) }
        observe(viewModel.isReadyForDismiss) { dismiss() }
    }

    private fun configureEasyImage() {
        EasyImage.configuration(activity!!)
            .setImagesFolderName("PolygonImages")
            .saveInAppExternalFilesDir()
    }

    private fun setImage(url: String) {
        Glide.with(imageView)
            .load(url)
            .into(imageView)
    }

    @SuppressLint("CheckResult")
    private fun addImage() {
        compositeDisposable.add(
            RxPermissions(activity!!)
                .request(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe { granted ->
                    if (granted) {
                        EasyImage.configuration(activity!!)
                            .setImagesFolderName("PolygonImages")
                            .saveInAppExternalFilesDir()
                        EasyImage.openCamera(this, 0)
                    } else {
                        toast(getString(R.string.error_no_permissions))
                    }
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            activity!!,
            object : DefaultCallback() {
                override fun onImagePicked(
                    imageFile: File?,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {
                    Timber.w("Photo path: ${imageFile?.absolutePath}")
                    viewModel.saveImage(imageFile?.absolutePath!!)
                    viewModel.imageUri.postValue(imageFile.absolutePath!!)
                }
            })

    }


    private fun saveDialog() {
        viewModel.saveData(textInfoET.text.toString())
    }

    private fun selectDate() {
        val cal: Calendar = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                viewModel.setDateToModel(year, monthOfYear, dayOfMonth)


            }, y, m, d
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()

    }

    override fun onDestroy() {
        listener = null
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onChangeProgressBarVisibility(isVisible: Boolean) {}

    override fun onShowError(message: String) {}

}