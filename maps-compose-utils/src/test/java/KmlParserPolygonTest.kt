import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.kml.manager.PolygonManager
import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field

/**
 * These tests are focused on the correct parsing of KML <Polygon> into a PolygonManager.
 *
 * For this test, the file "PolygonTest.kml" has been written with variations of a <Polygon>
 * In total, there are 3 polygons defined each with different values, the following list will show the variations
 *
 *  ┌─────────────────────────┐ ┌─────────────────────┐ ┌─────────────────────┐
 *  │ first                   │ │ second              │ │ third               │
 *  ├─────────────────────────┤ ├─────────────────────┤ ├─────────────────────┤
 *  │ Outer Points: 4         │ │ Outer Points: 4     │ │ Outer Points: 4     │
 *  │ Inner Points: 4         │ │ Inner Points: 4     │ │ Inner Points: 2x4   │
 *  │ Fill Color: Red         │ │ Fill Color: Green   │ │ Fill Color: unset   │
 *  │ Fill: 0                 │ │ Fill: 1             │ │ Fill: unset         │
 *  │ Stroke Color: Blue      │ │ Stroke Color: Green │ │ Stroke Color: unset │
 *  │ Stroke Width: undefined │ │ Stroke Width: 4     │ │ Stroke Width: unset │
 *  │ Stoke: 0                │ │ Stroke: 1           │ │ Stroke: unset       │
 *  │ Geodesic: 1             │ │ Geodesic: 0         │ │ Geodesic: unset     │
 *  │ Visible: 1              │ │ Visible: 0          │ │ Visible: unset      │
 *  │ zIndex: 1               │ │ zIndex: 2           │ │ zIndex: unset       │
 *  └─────────────────────────┘ └─────────────────────┘ └─────────────────────┘
 *  Only the third one contains two innerBoundaries, without any styling
 */

public class KmlParserPolygonTest {
    private lateinit var parser: KmlParser
    private lateinit var polygon1: PolygonManager
    private lateinit var polygon2: PolygonManager
    private lateinit var polygon3: PolygonManager

    // All data has been copied from the PolygonTest.kml file
    private val polygon1OuterBoundary = listOf(
        LatLng(37.824664,-122.364383),
        LatLng(37.824322,-122.364152),
        LatLng(37.824474,-122.363917),
        LatLng(37.824664,-122.364383),
    )

    private val polygon1InnerBoundary = listOf(
        LatLng(37.824531, -122.364212),
        LatLng(37.824394, -122.364001),
        LatLng(37.824493, -122.363755),
        LatLng(37.824531, -122.364212),
    )

    private val polygon2OuterBoundary = listOf(
        LatLng(37.825664, -122.365383),
        LatLng(37.825322, -122.365152),
        LatLng(37.825474, -122.364917),
        LatLng(37.825664, -122.365383),
    )

    private val polygon2InnerBoundary = listOf(
        LatLng(37.825531, -122.365212),
        LatLng(37.825394, -122.365001),
        LatLng(37.825493, -122.364755),
        LatLng(37.825531, -122.365212),
    )

    private val polygon3OuterBoundary = polygon2OuterBoundary
    private val polygon3InnerBoundaries = listOf(
        polygon2InnerBoundary,
        listOf(
            LatLng(38.825531, -123.365212),
            LatLng(38.825394, -123.365001),
            LatLng(38.825493, -123.364755),
            LatLng(38.825531, -123.365212),
        )
    )

    private fun getInnerBoundaries(manager: PolygonManager): List<List<LatLng>> {
        val field: Field = PolygonManager::class.java.getDeclaredField("innerBoundaries")
        field.isAccessible = true
        return field.get(manager) as List<List<LatLng>>
    }

    private fun getOuterBoundary(manager: PolygonManager): List<LatLng> {
        val field: Field = PolygonManager::class.java.getDeclaredField("outerBoundary")
        field.isAccessible = true
        return field.get(manager) as List<LatLng>
    }

    @Before
    public fun setUp(): Unit = runBlocking {
        val containerNestingKml = javaClass.classLoader?.getResourceAsStream("PolygonTest.kml")
            ?: throw AssertionError("Could not load sample KML file")
        val parsedKmlData = MapFileParser.parseStream(containerNestingKml)
        parsedKmlData.parser?.applyStyles(parsedKmlData.media)
        parser = parsedKmlData.parser!!

        polygon1 = parser.container.getPolygons()[0]
        polygon2 = parser.container.getPolygons()[1]
        polygon3 = parser.container.getPolygons()[2]
    }

    @Test
    public fun testContainerContainingAllPolygons() {
        val polygons = parser.container.getPolygons()
        Assert.assertEquals(3, polygons.count())
    }

    @Test
    public fun testCorrectNameParsed() {
        Assert.assertEquals("Test Polygon 1", polygon1.properties.name)
        Assert.assertEquals("Test Polygon 2", polygon2.properties.name)
    }

    @Test
    public fun testPolygon1CorrectInnerBoundary() {
        val innerBoundaries = getInnerBoundaries(polygon1)
        Assert.assertArrayEquals(
            polygon1InnerBoundary.toTypedArray(),
            innerBoundaries[0].toTypedArray()
        )
    }

    @Test
    public fun testPolygon1CorrectOuterBoundary() {
        val outerBoundary = getOuterBoundary(polygon1)
        Assert.assertArrayEquals(
            polygon1OuterBoundary.toTypedArray(),
            outerBoundary.toTypedArray()
        )
    }

    @Test
    public fun testPolygon2CorrectInnerBoundary() {
        val innerBoundaries = getInnerBoundaries(polygon2)
        Assert.assertArrayEquals(
            polygon2InnerBoundary.toTypedArray(),
            innerBoundaries[0].toTypedArray()
        )
    }

    @Test
    public fun testPolygon2CorrectOuterBoundary() {
        val outerBoundary = getOuterBoundary(polygon2)
        Assert.assertArrayEquals(
            polygon2OuterBoundary.toTypedArray(),
            outerBoundary.toTypedArray()
        )
    }

    @Test
    public fun testPolygon3ContainsMultipleInnerBoundaries() {
        val innerBoundaries = getInnerBoundaries(polygon3)
        Assert.assertEquals(2, innerBoundaries.count())
    }

    @Test
    public fun testPolygon3CorrectInnerBoundary() {
        val innerBoundaries = getInnerBoundaries(polygon3)

        innerBoundaries.forEachIndexed { index, list ->
            Assert.assertArrayEquals(
                polygon3InnerBoundaries[index].toTypedArray(),
                list.toTypedArray()
            )
        }
    }

    @Test
    public fun testPolygon3CorrectOuterBoundary() {
        val outerBoundary = getOuterBoundary(polygon3)
        Assert.assertArrayEquals(
            polygon3OuterBoundary.toTypedArray(),
            outerBoundary.toTypedArray()
        )
    }

    @Test
    public fun testFillColor() {
        val polygon1Color = Color(1f, 0f, 0f, 1f)
        val polygon2Color = Color(0f, 1f, 0f, 1f)
        val defaultFillColor = Color.Black // Extracted from DEFAULT_COLOR PolygonManager

        Assert.assertEquals(polygon1Color, polygon1.properties.fillColor)
        Assert.assertEquals(polygon2Color, polygon2.properties.fillColor)
        Assert.assertEquals(defaultFillColor, polygon3.properties.fillColor)
    }
}
