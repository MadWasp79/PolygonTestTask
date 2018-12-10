package com.mvhive.polygontesttask.tools

import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap

/**
 * Constants
 */
object Constants {

    const val COLOR_BLACK_ARGB = -0x1000000
    const val COLOR_WHITE_ARGB = -0x1
    const val COLOR_GREEN_ARGB = -0xc771c4
    const val COLOR_PURPLE_ARGB = -0x7e387c
    const val COLOR_ORANGE_ARGB = -0xa80e9
    const val COLOR_BLUE_ARGB = -0x657db
    const val COLOR_BLUE_TRANSP_ARGB = -0x770657db

    const val POLYLINE_STROKE_WIDTH_PX = 12
    const val POLYGON_STROKE_WIDTH_PX = 8
    const val PATTERN_DASH_LENGTH_PX = 20
    const val PATTERN_GAP_LENGTH_PX = 20
    val DOT = Dot()
    val DASH = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    val GAP = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

}