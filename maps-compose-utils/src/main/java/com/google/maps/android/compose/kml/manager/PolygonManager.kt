package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.DRAW_ORDER_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.TESSELLATE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import com.google.maps.android.compose.kml.parser.ExtendedData
import com.google.maps.android.compose.kml.parser.KmlParser.Companion.convertPropertyToBoolean
import com.google.maps.android.compose.kml.parser.KmlParser.Companion.convertPropertyToFloat

public class PolygonManager(
    private val coordinates: List<LatLng>
) : KmlComposableManager() {
    private var polygonData: MutableState<PolygonProperties> =
        mutableStateOf(PolygonProperties())

    override fun setProperties(data: HashMap<String, Any>) {
        polygonData.value = PolygonProperties.from(data)
    }

    /**
     * Returns a copy of the polygon properties
     *
     * @return PolygonProperties
     */
    public fun getProperties(): PolygonProperties = polygonData.value.copy()

    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>
    ) {
        val styleUrl = polygonData.value.styleUrl
        val normalStyleId = styleMaps[styleUrl]?.getNormalStyleId()
        val selectedStyle = styles[normalStyleId]

        style = selectedStyle ?: KmlStyle()
        applyStylesToProperties()
    }

    /**
     * Applies all available styles to properties
     */
    private fun applyStylesToProperties() {
//        setColor(style.getLineColor())
//        setWidth(style.getLineWidth())
    }

    /**
     * Sets polygons stroke joint type
     *
     * @param Int [JointType]
     */
    public fun setStrokeJointType(jointType: Int) {
        polygonData.value = polygonData.value.copy(strokeJointType = jointType)
    }

    /**
     * Sets polygons stroke color
     *
     * @param color color of the line
     */
    public fun setStrokeColor(color: Color) {
        polygonData.value = polygonData.value.copy(color = color)
    }

    /**
     * Sets the pattern for the polygon
     *
     * @param pattern List of patternItems creating the pattern
     */
    public fun setStrokePattern(pattern: List<PatternItem>) {
        polygonData.value = polygonData.value.copy(strokePattern = pattern)
    }

    /**
     * Sets the visibility of the polygon
     *
     * @param visible True when line should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        polygonData.value = polygonData.value.copy(visibility = visible)
    }

    /**
     * Sets polygon width
     *
     * @param width width of the line
     */
    public fun setWidth(width: Float) {
        polygonData.value = polygonData.value.copy(width = width)
    }


    @Composable
    override fun Render() {
        val data = polygonData.value

        Polygon(
            points = coordinates,
            fillColor = data.fillColor,
            geodesic = data.tessellate,
            zIndex = data.drawOrder,
            visible = data.visibility,
            strokeColor = data.strokeColor,
            strokeJointType = data.strokeJointType,
            strokePattern = data.strokePattern,
            strokeWidth = data.strokeWidth,
            clickable = true,
            onClick = {
//                listener?.onEvent(KmlEvent.polygon.Clicked(polygonData.value))
            }
        )
    }

    public data class PolygonProperties(
        val name: String = DEFAULT_NAME,
        val description: String = DEFAULT_DESCRIPTION,
        val visibility: Boolean = DEFAULT_VISIBILITY,
        val drawOrder: Float = DEFAULT_DRAW_ORDER,
        val color: Color = DEFAULT_COLOR,
        val tessellate: Boolean = DEFAULT_TESSELLATE,
        val styleUrl: String? = DEFAULT_STYLE_URL,
        val fillColor: Color = DEFAULT_COLOR,
        val strokeColor: Color = DEFAULT_COLOR,
        val strokeJointType: Int = DEFAULT_STROKE_JOINT_TYPE,
        val strokePattern: List<PatternItem>? = DEFAULT_STROKE_PATTERN,
        val strokeWidth: Float = DEFAULT_STROKE_WIDTH,
        val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA
    ) {
        internal companion object {
            internal fun from(properties: HashMap<String, Any>): PolygonProperties {
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
                return PolygonProperties(
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
            private val DEFAULT_STROKE_PATTERN = null
            private const val DEFAULT_STROKE_JOINT_TYPE = JointType.DEFAULT
            private const val DEFAULT_TESSELLATE = false
            private const val DEFAULT_STROKE_WIDTH = 10f
        }
    }
}
