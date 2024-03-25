package com.google.maps.android.compose.kml.manager

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap

internal interface KmlComposableManager {
    var style: KmlStyle

    suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        context: Context
    )

    fun setProperties(data: HashMap<String, Any>)

    @Composable
    fun Render()
}