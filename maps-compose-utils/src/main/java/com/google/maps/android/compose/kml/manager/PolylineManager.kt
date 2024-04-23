package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.TESSELLATE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import com.google.maps.android.compose.kml.parser.ExtendedData
import com.google.maps.android.compose.kml.parser.KmlParser.Companion.convertPropertyToBoolean

public class PolylineManager(
    private val coordinates: List<LatLng>
) : KmlComposableManager() {
    private var polylineData: MutableState<PolylineProperties> =
        mutableStateOf(PolylineProperties())

    override fun setProperties(data: HashMap<String, Any>) {
        polylineData.value = PolylineProperties.from(data)
    }

    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>
    ) {
        val styleUrl = polylineData.value.styleUrl
        val normalStyleId = styleMaps[styleUrl]?.getNormalStyleId()
        val selectedStyle = styles[normalStyleId]

        style = selectedStyle ?: KmlStyle()
        applyStylesToProperties()
    }

    /**
     * Applies all available styles to properties
     */
    private fun applyStylesToProperties() {
        setColor(style.getLineColor())
        setWidth(style.getLineWidth())
    }

    /**
     * Sets line color
     *
     * @param color color of the line
     */
    public fun setColor(color: Color) {
        polylineData.value = polylineData.value.copy(color = color)
    }

    /**
     * Sets line width
     *
     * @param width width of the line
     */
    public fun setWidth(width: Float) {
        polylineData.value = polylineData.value.copy(width = width)
    }


    @Composable
    override fun Render() {
        val data = polylineData.value

        if (data.visibility) {
            Polyline(
                points = coordinates,
                color = data.color,
                geodesic = data.tessellate,
                width = data.width,
                zIndex = data.drawOrder,
                clickable = true
            )
        }
    }

    public data class PolylineProperties(
        val name: String = DEFAULT_NAME,
        val description: String = DEFAULT_DESCRIPTION,
        val visibility: Boolean = DEFAULT_VISIBILITY,
        val drawOrder: Float = DEFAULT_DRAW_ORDER,
        val color: Color = DEFAULT_COLOR,
        val tessellate: Boolean = DEFAULT_TESSELLATE,
        val width: Float = DEFAULT_WIDTH,
        val styleUrl: String? = DEFAULT_STYLE_URL,
        val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA
    ) {
        internal companion object {
            internal fun from(properties: HashMap<String, Any>): PolylineProperties {
                val name: String by properties.withDefault { DEFAULT_NAME }
                val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
                val visibility: Boolean =
                    convertPropertyToBoolean(properties, VISIBILITY_TAG, DEFAULT_VISIBILITY)
                val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
                val styleUrl: String? by properties.withDefault { DEFAULT_STYLE_URL }
                val extendedData: List<ExtendedData>? =
                    properties[EXTENDED_DATA_TAG] as? List<ExtendedData>
                val tessellate: Boolean =
                    convertPropertyToBoolean(properties, TESSELLATE_TAG, DEFAULT_TESSELLATE)
                return PolylineProperties(
                    name = name,
                    description = description,
                    visibility = visibility,
                    drawOrder = drawOrder,
                    styleUrl = styleUrl,
                    extendedData = extendedData,
                    tessellate = tessellate
                )
            }

            private val DEFAULT_COLOR = Color.Black
            private const val DEFAULT_TESSELLATE = false
            private const val DEFAULT_WIDTH = 1f
        }
    }
}
