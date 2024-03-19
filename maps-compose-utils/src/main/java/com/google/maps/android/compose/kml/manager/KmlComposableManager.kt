package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap

internal interface KmlComposableManager {
    var style: KmlStyle?

    fun setStyle(styleMaps: HashMap<String, KmlStyleMap>, styles: HashMap<String, KmlStyle>)
    fun setProperties(data: HashMap<String, Any>)

    @Composable
    fun Render(images: HashMap<String, Bitmap>)
}