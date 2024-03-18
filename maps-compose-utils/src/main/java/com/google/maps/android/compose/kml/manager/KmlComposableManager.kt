package com.google.maps.android.compose.kml.manager

import androidx.compose.runtime.Composable

internal interface KmlComposableManager {
    var properties: HashMap<String, String>

    fun setProperty(key: String, value: String) {
    }

    fun applyProperties()

    @Composable
    fun Render()
}