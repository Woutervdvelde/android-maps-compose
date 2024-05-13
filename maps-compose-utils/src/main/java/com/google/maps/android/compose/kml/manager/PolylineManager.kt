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
import com.google.maps.android.compose.kml.parser.ExtendedData
import com.google.maps.android.compose.kml.parser.KmlParser.Companion.convertPropertyToBoolean
import com.google.maps.android.compose.kml.parser.KmlParser.Companion.convertPropertyToFloat

public class PolylineManager(
    private val coordinates: List<LatLng>
) : KmlComposableManager() {
    private var polylineData: MutableState<PolylineProperties> =
        mutableStateOf(PolylineProperties())

    override fun setProperties(data: HashMap<String, Any>) {
        polylineData.value = PolylineProperties.from(data)
    }

    /**
     * Returns a copy of the polyline properties
     *
     * @return PolylineProperties
     */
    public fun getProperties(): PolylineProperties = polylineData.value.copy()

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
     * Sets the cap at the start vertex of the polyline
     *
     * @param cap any [Cap] subclass
     */
    public fun setStartCap(cap: Cap) {
        polylineData.value = polylineData.value.copy(startCap = cap)
    }

    /**
     * Sets the cap at the end vertex of the polyline
     *
     * @param cap any [Cap] subclass
     */
    public fun setEndCap(cap: Cap) {
        polylineData.value = polylineData.value.copy(endCap = cap)
    }

    /**
     *
     */
    public fun setJointType(jointType: Int) {
        polylineData.value = polylineData.value.copy(jointType = jointType)
    }

    /**
     * Sets polyline color
     *
     * @param color color of the line
     */
    public fun setColor(color: Color) {
        polylineData.value = polylineData.value.copy(color = color)
    }

    /**
     * Sets the pattern for the polyline
     *
     * @param pattern List of patternItems creating the pattern
     */
    public fun setPattern(pattern: List<PatternItem>) {
        polylineData.value = polylineData.value.copy(pattern = pattern)
    }

    /**
     * Sets the visibility of the polyline
     *
     * @param visible True when line should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        polylineData.value = polylineData.value.copy(visibility = visible)
    }

    /**
     * Sets polyline width
     *
     * @param width width of the line
     */
    public fun setWidth(width: Float) {
        polylineData.value = polylineData.value.copy(width = width)
    }


    @Composable
    override fun Render() {
        val data = polylineData.value

        Polyline(
            points = coordinates,
            color = data.color,
            geodesic = data.tessellate,
            width = data.width,
            zIndex = data.drawOrder,
            visible = data.visibility,
            clickable = true,
            jointType = data.jointType,
            startCap = data.startCap,
            endCap = data.endCap,
            pattern = data.pattern,
            onClick = {
                listener?.onEvent(KmlEvent.Polyline.Clicked(polylineData.value))
            }
        )
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
        val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA,
        val startCap: Cap = DEFAULT_CAP,
        val endCap: Cap = DEFAULT_CAP,
        val jointType: Int = DEFAULT_JOINT_TYPE,
        val pattern: List<PatternItem>? = DEFAULT_PATTERN
    ) {
        internal companion object {
            internal fun from(properties: HashMap<String, Any>): PolylineProperties {
                val name: String by properties.withDefault { DEFAULT_NAME }
                val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
                val visibility: Boolean =
                    convertPropertyToBoolean(properties, VISIBILITY_TAG, DEFAULT_VISIBILITY)
                val drawOrder: Float = convertPropertyToFloat(properties, DRAW_ORDER_TAG, DEFAULT_DRAW_ORDER)
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
            private val DEFAULT_CAP = ButtCap()
            private val DEFAULT_PATTERN = null
            private const val DEFAULT_JOINT_TYPE = JointType.DEFAULT
            private const val DEFAULT_TESSELLATE = false
            private const val DEFAULT_WIDTH = 1f
        }
    }
}
