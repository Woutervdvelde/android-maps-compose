package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GroundOverlay
import com.google.maps.android.compose.GroundOverlayPosition
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import com.google.maps.android.compose.kml.parser.ExtendedData
import com.google.maps.android.compose.kml.parser.KmlParser

public class GroundOverlayManager : KmlComposableManager() {
    private var groundOverlayData: MutableState<GroundOverlayProperties> = mutableStateOf(
        GroundOverlayProperties()
    )

    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        parentVisibility: Boolean
    ) {
        groundOverlayData.value = groundOverlayData.value.copy(
            icon = getBitmap(groundOverlayData.value.iconUrl, images)
        )

        setVisibility(parentVisibility)
    }

    override fun setProperties(data: HashMap<String, Any>) {
        groundOverlayData.value = GroundOverlayProperties.from(data)
        setVisibility(KmlParser.convertPropertyToBoolean(data, VISIBILITY_TAG, DEFAULT_VISIBILITY))
    }

    /**
     * Sets alpha of the ground overlay, value between 0f and 1f.
     * 0f means the icon is fully transparent and  1f will make the overlay fully opaque.
     *
     * @param alpha Float value between 0f and 1f
     */
    public fun setAlpha(alpha: Float) {
        groundOverlayData.value = groundOverlayData.value.copy(alpha = alpha)
    }

    /**
     * Sets the bounds of the overlay, maps the corners of the image to the specified coordinates.
     *
     * @param positionBounds LatLngBounds containing two [LatLng] objects southwest and northeast
     */
    public fun setCompass(positionBounds: LatLngBounds) {
        groundOverlayData.value = groundOverlayData.value.copy(
            positionBounds = positionBounds
        )
    }

    /**
     * Sets the visibility of the ground overlay
     *
     * @param visible True when overlay should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        groundOverlayData.value = groundOverlayData.value.copy(
            visibility = visible
        )
    }

    @Composable
    override fun Render() {
        val data = groundOverlayData.value

        if (data.visibility) {
            GroundOverlay(
                position = GroundOverlayPosition.create(data.positionBounds!!),
                image = BitmapDescriptorFactory.fromBitmap(data.icon!!),
                transparency = data.alpha * -1 + 1,
                bearing = -data.rotation.toFloat(), // - (negative) since KML is defined counterclockwise and compose clockwise
                zIndex = data.drawOrder
            )
        }
    }

    public data class GroundOverlayProperties(
        val name: String = DEFAULT_NAME,
        val description: String = DEFAULT_DESCRIPTION,
        val visibility: Boolean = DEFAULT_VISIBILITY,
        val alpha: Float = DEFAULT_ALPHA,
        val drawOrder: Float = DEFAULT_DRAW_ORDER,
        val rotation: Int = DEFAULT_ROTATION,
        val iconUrl: String = DEFAULT_ICON_URL,
        val styleUrl: String? = DEFAULT_STYLE_URL,
        val icon: Bitmap? = DEFAULT_ICON,
        val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA,
        val positionBounds: LatLngBounds? = DEFAULT_POSITION_BOUNDS
    ) {
        internal companion object {
            internal fun from(properties: HashMap<String, Any>): GroundOverlayProperties {
                val name: String by properties.withDefault { DEFAULT_NAME }
                val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
                val visibility: Boolean =
                    KmlParser.convertPropertyToBoolean(properties, VISIBILITY_TAG, DEFAULT_VISIBILITY)
                val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
                val rotation: Int by properties.withDefault { DEFAULT_ROTATION }
                val href: String by properties.withDefault { DEFAULT_ICON_URL }
                val extendedData: List<ExtendedData>? = properties[EXTENDED_DATA_TAG] as? List<ExtendedData>
                return GroundOverlayProperties(
                    name = name,
                    description = description,
                    visibility = visibility,
                    alpha = DEFAULT_ALPHA,
                    drawOrder = drawOrder,
                    rotation = rotation,
                    iconUrl = href,
                    extendedData = extendedData
                )
            }

            internal val DEFAULT_POSITION_BOUNDS = null
            internal const val DEFAULT_ICON_URL = ""
        }
    }
}