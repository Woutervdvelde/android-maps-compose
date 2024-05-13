import com.google.maps.android.compose.kml.manager.MarkerManager
import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * For this test, the file "MarkerTest.kml" has been written with variations of <Placemark> elements representing markers.
 * In total, there are 3 markers defined, each with different properties. The following table illustrates the variations:
 *
 * ┌─────────────┬──────────────────────┬─────────────────────┐
 * │ Marker      │ First Marker         │ Second Marker       │
 * ├─────────────┼──────────────────────┼─────────────────────┤
 * │ Position    │ 52.509, 6.087        │ 52.508, 6.086       │
 * │ Title       │ First Marker         │ Second Marker       │
 * │ Snippet     │ This is the first    │ This is the second  │
 * │             │ marker               │ marker              │
 * │ Icon        │ Blue Marker Icon     │ Green Marker Icon   │
 * │ Rotation    │ 45 degrees           │ 90 degrees          │
 * │ Visibility  │ Visible              │ Visible             │
 * │ zIndex      │ 1                    │ 2                   │
 * └─────────────┴──────────────────────┴─────────────────────┘
 * ┌─────────────┬──────────────────────┐
 * │ Marker      │ Third Marker         │
 * ├─────────────┼──────────────────────┤
 * │ Position    │ 52.507, 6.085        │
 * │ Title       │ Third Marker         │
 * │ Snippet     │ This is the third    │
 * │             │ marker               │
 * │ Icon        │ Red Marker Icon      │
 * │ Rotation    │ 135 degrees          │
 * │ Visibility  │ Not Visible          │
 * │ zIndex      │ 3                    │
 * └─────────────┴──────────────────────┘
 */
public class KmlParserMarkerTest {
    private lateinit var parser: KmlParser
    private lateinit var marker1: MarkerManager
    private lateinit var marker2: MarkerManager
    private lateinit var marker3: MarkerManager

    @Before
    public fun setUp(): Unit = runBlocking {
        val containerNestingKml = javaClass.classLoader?.getResourceAsStream("MarkerTest.kml")
            ?: throw AssertionError("Could not load sample KML file")
        val parsedKmlData = MapFileParser.parseStream(containerNestingKml)
        parsedKmlData.parser?.applyStyles(parsedKmlData.media)
        parser = parsedKmlData.parser!!

        val markers = parser.container.getMarkers()
        marker1 = markers[0]
        marker2 = markers[1]
        marker3 = markers[2]
    }

    @Test
    public fun testContainerContainingAllMarkers() {
        val markers = parser.container.getMarkers()
        Assert.assertEquals(3, markers.count())
    }

    @Test
    public fun testCorrectNameParsed() {
        Assert.assertEquals("First Marker", marker1.getProperties().name)
        Assert.assertEquals("Second Marker", marker2.getProperties().name)
        Assert.assertEquals("Third Marker", marker3.getProperties().name)
    }


}