import com.google.maps.android.compose.kml.manager.PolygonManager
import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * These tests are focused on the correct parsing of KML <Polygon> into a PolygonManager.
 *
 * For this test, the file "PolygonTest.kml" has been written with variations of a <Polygon>
 * In total, there are 2 polygons defined each with different values, the following list will show the variations
 *
 *  ┌─────────────────────────┐ ┌─────────────────────┐
 *  │ first                   │ │ second              │
 *  ├─────────────────────────┤ ├─────────────────────┤
 *  │ Outer Points: 4         │ │ Outer Points: 4     │
 *  │ Inner Points: 4         │ │ Inner Points: 4     │
 *  │ Fill Color: Red         │ │ Fill Color: Green   │
 *  │ Fill: 0                 │ │ Fill: 1             │
 *  │ Stroke Color: Blue      │ │ Stroke Color: Green │
 *  │ Stroke Width: undefined │ │ Stroke Width: 4     │
 *  │ Stoke: 0                │ │ Stroke: 1           │
 *  │ Geodesic: 1             │ │ Geodesic: 0         │
 *  │ Visible: 1              │ │ Visible: 0          │
 *  │ zIndex: 1               │ │ zIndex: 2           │
 *  └─────────────────────────┘ └─────────────────────┘
 *
 */

public class KmlParserPolygonTest {
    private lateinit var parser: KmlParser
    private lateinit var polygon1: PolygonManager
    private lateinit var polygon2: PolygonManager

    @Before
    public fun setUp(): Unit = runBlocking {
        val containerNestingKml = javaClass.classLoader?.getResourceAsStream("PolygonTest.kml")
            ?: throw AssertionError("Could not load sample KML file")
        val parsedKmlData = MapFileParser.parseStream(containerNestingKml)
        parsedKmlData.parser?.applyStyles(parsedKmlData.media)
        parser = parsedKmlData.parser!!

        polygon1 = parser.container.getPolygons()[0]
        polygon2 = parser.container.getPolygons()[1]
    }

    @Test
    public fun testContainerContainingAllPolygons() {
        val polygons = parser.container.getPolygons()
        Assert.assertEquals(2, polygons.count())
    }

    @Test
    public fun testCorrectNameParsed() {
        Assert.assertEquals("Test Polygon 1", polygon1.properties.name)
        Assert.assertEquals("Test Polygon 2", polygon2.properties.name)
    }

//    @Test
//    public fun testPolygon1
}
