package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.DRAW_ORDER_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import com.google.maps.android.compose.kml.event.KmlEvent
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ALPHA
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ANCHOR
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_COLOR
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DESCRIPTION
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DRAW_ORDER
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_EXTENDED_DATA
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ICON
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_NAME
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_ROTATION
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_STYLE_URL
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_VISIBILITY
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.convertPropertyToBoolean
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.convertPropertyToFloat
import com.google.maps.android.compose.kml.parser.Anchor
import com.google.maps.android.compose.kml.parser.ExtendedData
import com.google.maps.android.compose.kml.parser.KmlStyleParser
import com.google.maps.android.compose.rememberMarkerState

public class MarkerManager(
    private val position: LatLng
) : KmlComposableManager<MarkerProperties>() {
    override val _properties: MutableState<MarkerProperties> = mutableStateOf(MarkerProperties())

    override fun setProperties(data: HashMap<String, Any>) {
        _properties.value = MarkerProperties.from(data)
        val visiblitiy = convertPropertyToBoolean(data, VISIBILITY_TAG, DEFAULT_VISIBILITY)
        setVisibility(visiblitiy)
    }

    public override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        parentVisibility: Boolean
    ) {
        val styleUrl = _properties.value.styleUrl
        val normalStyleId = styleMaps[styleUrl]?.getNormalStyleId()
        style = styles[normalStyleId] ?: styles[styleUrl] ?: style

        applyStylesToProperties()
        generateIcon(images)

        if (isActive.value) // if it's own visibility is false don't apply parent visibility
            setVisibility(parentVisibility)
    }

    /**
     * Sets the visibility of the marker
     *
     * @param visible True when marker should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        setActive(visible)
    }

    /**
     * Sets alpha of the marker, value between 0f and 1f.
     * 0f means the icon is fully transparent and  1f will make the marker fully opaque.
     *
     * @param alpha Float value between 0f and 1f
     */
    public fun setAlpha(alpha: Float) {
        _properties.value = _properties.value.copy(alpha = alpha)
    }

    /**
     * Sets rotation of the marker in degrees
     *
     * @param rotation in degrees, 0 - 360
     */
    public fun setRotation(rotation: Int) {
        _properties.value = _properties.value.copy(rotation = rotation)
    }

    /**
     * Sets icon anchor, only supports fractions
     *
     * @param anchor Anchor value
     */
    public fun setAnchor(anchor: Anchor) {
        //TODO("handle other unit types, only supports fractions at the moment")
        if (anchor.xUnit == KmlStyleParser.HOTSPOT_UNIT_FRACTION && anchor.yUnit == KmlStyleParser.HOTSPOT_UNIT_FRACTION) {
            _properties.value = _properties.value.copy(anchor = anchor)
        } else {
            _properties.value = _properties.value.copy(anchor = Anchor())
        }
    }

    /**
     * Sets marker color, also applies random color if RandomColorMode is enabled
     *
     * @param color Color as integer
     */
    public fun setColor(color: Int) {
        var finalColor = color.toFloat()
        if (style.getIconRandomColorMode()) {
            val randomColor = KmlStyleParser.computeRandomColor(color)
            finalColor = KmlStyleParser.convertIntColorToHueValue(randomColor)
        }
        _properties.value = _properties.value.copy(color = finalColor)
    }

    /**
     * Applies all available styles to properties
     */
    private fun applyStylesToProperties() {
        setAlpha(style.getIconAlpha())
        setAnchor(style.getIconAnchor())
        setRotation(style.getIconHeading())
        style.getIconColor()?.let { setColor(it.toInt()) }
    }

    private fun getIcon(bitmap: Bitmap?): BitmapDescriptor {
        return bitmap?.let {
            BitmapDescriptorFactory.fromBitmap(it)
        } ?: _properties.value.color?.let {
            BitmapDescriptorFactory.defaultMarker(it)
        } ?: BitmapDescriptorFactory.defaultMarker()
    }

    /**
     * Generates icon that can be used by the Marker composable.
     * Sets the value in markerData
     *
     * @param images All images when present in KMZ file
     */
    private suspend fun generateIcon(images: HashMap<String, Bitmap>) {
        style.getIconUrl()?.let { url ->
            getBitmap(url, images)?.let {
                _properties.value = _properties.value.copy(
                    icon = resizeIcon(it, style.getIconScale())
                )
            }
        }
    }

    /**
     * Resizes icon bitmap based on icon aspect ratio, scale and display density
     *
     * @param icon The icon Bitmap
     * @param scale Scale that should be applied
     */
    private fun resizeIcon(icon: Bitmap, scale: Float): Bitmap {
        if (icon.height == 0 || icon.density == 0)
            return icon

        val dpi = icon.density
        val iconAspectRatio = icon.width.toFloat() / icon.height.toFloat()
        val (defaultWidth, defaultHeight) = if (iconAspectRatio < 1) {
            Pair(DEFAULT_ICON_WIDTH * iconAspectRatio, DEFAULT_ICON_HEIGHT)
        } else {
            Pair(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT / iconAspectRatio)
        }
        val scaleFactor = dpi / DEFAULT_DPI
        val width = defaultWidth * scale * scaleFactor
        val height = defaultHeight * scale * scaleFactor

        return Bitmap.createScaledBitmap(icon, width.toInt(), height.toInt(), true)
    }

    /**
     * Renders the Marker applying all its properties and styles to it
     */
    @Composable
    override fun Render() {
        val markerState = rememberMarkerState(position = position)
        val data = _properties.value

        Marker(
            state = markerState,
            alpha = data.alpha,
            anchor = Offset(data.anchor.x, data.anchor.y),
            rotation = data.rotation.toFloat(),
            snippet = data.description,
            title = data.name,
            visible = isActive.value,
            zIndex = data.drawOrder,
            icon = getIcon(data.icon),
            onClick = {
                listener?.onEvent(KmlEvent.Marker.Clicked(properties))
                true
            }
        )
    }

    private companion object {
        const val DEFAULT_DPI: Float = 560f
        const val DEFAULT_ICON_WIDTH: Float = 110f
        const val DEFAULT_ICON_HEIGHT: Float = 110f
    }
}

/**
 * Helper data class containing all maker properties and styles
 */
public data class MarkerProperties(
    override val name: String = DEFAULT_NAME,
    override val description: String = DEFAULT_DESCRIPTION,
    override val drawOrder: Float = DEFAULT_DRAW_ORDER,
    override val styleUrl: String? = DEFAULT_STYLE_URL,
    override val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA,

    val alpha: Float = DEFAULT_ALPHA,
    val anchor: Anchor = DEFAULT_ANCHOR,
    val color: Float? = DEFAULT_COLOR,
    val icon: Bitmap? = DEFAULT_ICON,
    val rotation: Int = DEFAULT_ROTATION,
) : IKmlComposableProperties {
    internal companion object {
        internal fun from(properties: HashMap<String, Any>): MarkerProperties {
            val name: String by properties.withDefault { DEFAULT_NAME }
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val drawOrder: Float =
                convertPropertyToFloat(properties, DRAW_ORDER_TAG, DEFAULT_DRAW_ORDER)
            val extendedData: List<ExtendedData>? =
                properties[EXTENDED_DATA_TAG] as? List<ExtendedData>
            val styleUrl: String? by properties.withDefault { DEFAULT_STYLE_URL }

            return MarkerProperties(
                name = name,
                description = description,
                drawOrder = drawOrder,
                styleUrl = styleUrl,
                extendedData = extendedData,
            )
        }
    }
}
