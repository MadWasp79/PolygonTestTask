package com.mwhive.polygontesttask.utilsandextensions.extensions

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import com.mwhive.polygontesttask.BuildConfig


/**
 * Created by Denis Kolomiets on 09-Nov-18.
 */

fun Context.toastD(message: String?) {
    if (BuildConfig.DEBUG)
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
}

fun Context.toast(message: String?) {
    message?.let {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.toastD(message: String?) {
    if (BuildConfig.DEBUG)
        message?.let {
            Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
        }
}

fun Fragment.toast(message: String?) {
    message?.let {
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
    }
}