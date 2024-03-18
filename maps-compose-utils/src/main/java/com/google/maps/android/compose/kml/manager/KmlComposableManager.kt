package com.google.maps.android.compose.kml.manager

import androidx.compose.runtime.Composable

internal interface KmlComposableManager {
    fun setProperties(data: HashMap<String, Any>)

    @Composable
    fun Render()
}