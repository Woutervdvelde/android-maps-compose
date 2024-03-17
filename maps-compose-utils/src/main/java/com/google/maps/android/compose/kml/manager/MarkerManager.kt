package com.google.maps.android.compose.kml.manager

import com.google.android.gms.maps.model.LatLng

public class MarkerManager (
    private val position: LatLng
) {
    public fun getPosition(): LatLng = position
}