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
import com.google.maps.android.compose.kml.event.KmlEvent
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ALPHA
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DESCRIPTION
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DRAW_ORDER
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_EXTENDED_DATA
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ICON
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_NAME
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ROTATION
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_STYLE_URL
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_VISIBILITY
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.convertPropertyToBoolean
import com.google.maps.android.compose.kml.parser.ExtendedData

public class GroundOverlayManager : KmlComposableManager<GroundOverlayProperties>() {
    override val _properties: MutableState<GroundOverlayProperties> =
        mutableStateOf(GroundOverlayProperties())

    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        parentVisibility: Boolean
    ) {
        super.setStyle(styleMaps, styles, images, parentVisibility)
        _properties.value = _properties.value.copy(
            icon = getBitmap(_properties.value.iconUrl, images)
        )
    }

    override fun applyStylesToProperties() {}

    override fun setProperties(data: HashMap<String, Any>) {
        _properties.value = GroundOverlayProperties.from(data)
        setVisibility(convertPropertyToBoolean(data, VISIBILITY_TAG, DEFAULT_VISIBILITY))
    }

    /**
     * Sets alpha of the ground overlay, value between 0f and 1f.
     * 0f means the icon is fully transparent and  1f will make the overlay fully opaque.
     *
     * @param alpha Float value between 0f and 1f
     */
    public fun setAlpha(alpha: Float) {
        _properties.value = _properties.value.copy(alpha = alpha)
    }

    /**
     * Sets the bounds of the overlay, maps the corners of the image to the specified coordinates.
     *
     * @param positionBounds LatLngBounds containing two [LatLng] objects southwest and northeast
     */
    public fun setCompass(positionBounds: LatLngBounds) {
        _properties.value = _properties.value.copy(
            positionBounds = positionBounds
        )
    }

    /**
     * Sets the visibility of the ground overlay
     *
     * @param visible True when overlay should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        setActive(visible)
    }

    @Composable
    override fun Render() {
        val data = _properties.value

        GroundOverlay(
            position = GroundOverlayPosition.create(data.positionBounds!!),
            image = BitmapDescriptorFactory.fromBitmap(data.icon!!),
            transparency = data.alpha * -1 + 1,
            bearing = -data.rotation.toFloat(), // - (negative) since KML is defined counterclockwise and compose clockwise
            visible = isActive.value,
            zIndex = data.drawOrder,
            onClick = {
                listener?.onEvent(KmlEvent.GroundOverlay.Clicked(properties))
            }
        )
    }
}

public data class GroundOverlayProperties(
    override val name: String = DEFAULT_NAME,
    override val description: String = DEFAULT_DESCRIPTION,
    override val drawOrder: Float = DEFAULT_DRAW_ORDER,
    override val styleUrl: String? = DEFAULT_STYLE_URL,
    override val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA,

    val alpha: Float = DEFAULT_ALPHA,
    val iconUrl: String = DEFAULT_ICON_URL,
    val icon: Bitmap? = DEFAULT_ICON,
    val positionBounds: LatLngBounds? = DEFAULT_POSITION_BOUNDS,
    val rotation: Int = DEFAULT_ROTATION
) : IKmlComposableProperties {
    internal companion object {
        internal fun from(properties: HashMap<String, Any>): GroundOverlayProperties {
            val name: String by properties.withDefault { DEFAULT_NAME }
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
            val extendedData: List<ExtendedData>? =
                properties[EXTENDED_DATA_TAG] as? List<ExtendedData>

            val rotation: Int by properties.withDefault { DEFAULT_ROTATION }
            val href: String by properties.withDefault { DEFAULT_ICON_URL }
            return GroundOverlayProperties(
                name = name,
                description = description,
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
