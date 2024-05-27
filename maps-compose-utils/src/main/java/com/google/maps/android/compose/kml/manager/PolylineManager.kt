package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.DRAW_ORDER_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.TESSELLATE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import com.google.maps.android.compose.kml.event.KmlEvent
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DESCRIPTION
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DRAW_ORDER
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_EXTENDED_DATA
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_NAME
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_STYLE_URL
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_VISIBILITY
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.convertPropertyToBoolean
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.convertPropertyToFloat
import com.google.maps.android.compose.kml.parser.ExtendedData

public class PolylineManager(
    private val coordinates: List<LatLng>
) : KmlComposableManager<PolylineProperties>() {
    override val _properties: MutableState<PolylineProperties> =
        mutableStateOf(PolylineProperties())

    override fun setProperties(data: HashMap<String, Any>) {
        _properties.value = PolylineProperties.from(data)
        setVisibility(convertPropertyToBoolean(data, VISIBILITY_TAG, DEFAULT_VISIBILITY))
    }

    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        parentVisibility: Boolean
    ) {
        super.setStyle(styleMaps, styles, images, parentVisibility)
    }

    /**
     * Applies all available styles to properties
     */
    override fun applyStylesToProperties() {
        setColor(style.getLineColor())
        setWidth(style.getLineWidth())
    }

    /**
     * Sets the cap at the start vertex of the polyline
     *
     * @param cap any [Cap] subclass
     */
    public fun setStartCap(cap: Cap) {
        _properties.value = _properties.value.copy(startCap = cap)
    }

    /**
     * Sets the cap at the end vertex of the polyline
     *
     * @param cap any [Cap] subclass
     */
    public fun setEndCap(cap: Cap) {
        _properties.value = _properties.value.copy(endCap = cap)
    }

    /**
     *
     */
    public fun setJointType(jointType: Int) {
        _properties.value = _properties.value.copy(jointType = jointType)
    }

    /**
     * Sets polyline color
     *
     * @param color color of the line
     */
    public fun setColor(color: Color) {
        _properties.value = _properties.value.copy(color = color)
    }

    /**
     * Sets the pattern for the polyline
     *
     * @param pattern List of patternItems creating the pattern
     */
    public fun setPattern(pattern: List<PatternItem>) {
        _properties.value = _properties.value.copy(pattern = pattern)
    }

    /**
     * Sets the visibility of the polyline
     *
     * @param visible True when line should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        setActive(visible)
    }

    /**
     * Sets polyline width
     *
     * @param width width of the line
     */
    public fun setWidth(width: Float) {
        _properties.value = _properties.value.copy(width = width)
    }


    @Composable
    override fun Render() {
        val data = _properties.value

        Polyline(
            points = coordinates,
            color = data.color,
            geodesic = data.tessellate,
            width = data.width,
            zIndex = data.drawOrder,
            visible = isActive.value,
            clickable = true,
            jointType = data.jointType,
            startCap = data.startCap,
            endCap = data.endCap,
            pattern = data.pattern,
            onClick = {
                listener?.onEvent(KmlEvent.Polyline.Clicked(properties))
            }
        )
    }
}

public data class PolylineProperties(
    override val name: String = DEFAULT_NAME,
    override val description: String = DEFAULT_DESCRIPTION,
    override val drawOrder: Float = DEFAULT_DRAW_ORDER,
    override val styleUrl: String? = DEFAULT_STYLE_URL,
    override val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA,

    val color: Color = DEFAULT_COLOR,
    val tessellate: Boolean = DEFAULT_TESSELLATE,
    val width: Float = DEFAULT_WIDTH,
    val startCap: Cap = DEFAULT_CAP,
    val endCap: Cap = DEFAULT_CAP,
    val jointType: Int = DEFAULT_JOINT_TYPE,
    val pattern: List<PatternItem>? = DEFAULT_PATTERN
) : IKmlComposableProperties {
    internal companion object {
        internal fun from(properties: HashMap<String, Any>): PolylineProperties {
            val name: String by properties.withDefault { DEFAULT_NAME }
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val drawOrder: Float =
                convertPropertyToFloat(properties, DRAW_ORDER_TAG, DEFAULT_DRAW_ORDER)
            val styleUrl: String? by properties.withDefault { DEFAULT_STYLE_URL }
            val extendedData: List<ExtendedData>? =
                properties[EXTENDED_DATA_TAG] as? List<ExtendedData>
            val tessellate: Boolean =
                convertPropertyToBoolean(properties, TESSELLATE_TAG, DEFAULT_TESSELLATE)

            return PolylineProperties(
                name = name,
                description = description,
                drawOrder = drawOrder,
                styleUrl = styleUrl,
                extendedData = extendedData,
                tessellate = tessellate
            )
        }

        private val DEFAULT_COLOR = Color.Black
        private val DEFAULT_CAP = ButtCap()
        private val DEFAULT_PATTERN = null
        private const val DEFAULT_JOINT_TYPE = JointType.DEFAULT
        private const val DEFAULT_TESSELLATE = false
        private const val DEFAULT_WIDTH = 1f
    }
}
