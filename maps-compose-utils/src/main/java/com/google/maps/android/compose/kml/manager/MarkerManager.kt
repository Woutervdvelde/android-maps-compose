package com.google.maps.android.compose.kml.manager

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

public class MarkerManager (
    private val position: LatLng
): KmlComposableManager {
    public fun getPosition(): LatLng = position

    @Composable
    override fun Render() {
        Marker(
            state = MarkerState(position)
        )
    }
}