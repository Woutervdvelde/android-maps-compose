package com.google.maps.android.compose.kml.manager

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

public class MarkerManager (
    private val position: LatLng
): KmlComposableManager {
    override var properties: HashMap<String, String> = hashMapOf()
    private var description: String = ""
    private var name = ""
    private var visibility = true
    private var drawOrder = 0

    public fun getPosition(): LatLng = position

    override fun applyProperties() {
        
        //TODO()
    }

    @Composable
    override fun Render() {
        val markerState = rememberMarkerState(position = position)
        Marker(
            state = markerState,
            snippet = "Test",
            title = "Title",
        )
        markerState.showInfoWindow()
    }
}