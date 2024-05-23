import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.kml.manager.MarkerManager
import com.google.maps.android.compose.kml.parser.Anchor
import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.Field

/**
 * For this test, the file "MarkerTest.kml" has been written with variations of <Placemark> elements representing markers.
 * In total, there are 3 markers defined, each with different properties. The following table illustrates the variations:
 *
 * ┌─────────────┬───────────────────────┬─────────────────────┐
 * │ Marker      │ First Marker          │ Second Marker       │
 * ├─────────────┼───────────────────────┼─────────────────────┤
 * │ Position    │ -122.083739,37.422065 │ 139.6917,35.6895    │
 * │ Title       │ First Marker          │ Second Marker       │
 * │ Snippet     │ This is the first     │ This is the second  │
 * │             │ marker                │ marker              │
 * │ Icon        │ Blue Marker Icon      │ Green Marker Icon   │
 * │ Rotation    │ 45 degrees            │ 90 degrees          │
 * │ Visibility  │ Visible               │ Visible             │
 * │ zIndex      │ 1                     │ 2                   │
 * │ color       │ ff0000ff              │ ff00ff00            │
 * │ colorMode   │ unset                 │ unset               │
 * │ scale       │ unset                 │ 10                  │
 * └─────────────┴───────────────────────┴─────────────────────┘
 * ┌─────────────┬───────────────────────┐
 * │ Marker      │ Third Marker          │
 * ├─────────────┼───────────────────────┤
 * │ Position    │ -0.1276,51.5074       │
 * │ Title       │ Third Marker          │
 * │ Snippet     │ This is the third     │
 * │             │ marker                │
 * │ Icon        │ unset                 │
 * │ Rotation    │ 135 degrees           │
 * │ Visibility  │ Not Visible           │
 * │ zIndex      │ 3                     │
 * │ color       │ ffff0000              │
 * │ colorMode   │ random                │
 * │ hotSpot     │ fraction 0.5, 0.5     │
 * └─────────────┴───────────────────────┘
 */

@RunWith(RobolectricTestRunner::class)
public class KmlParserMarkerTest {
    private lateinit var parser: KmlParser
    private lateinit var marker1: MarkerManager
    private lateinit var marker2: MarkerManager
    private lateinit var marker3: MarkerManager

    // copied from MarkerTest.kml
    private val marker1Description = "<b>Placemark 1</b><br/>This is the first placemark.<br/>"
    private val marker2Description = "<b>Placemark 2</b><br/>This is the second placemark.<br/>"
    private val marker3Description = "<b>Placemark 3</b><br/>This is the third placemark.<br/>"
    private val marker1Position = LatLng(37.422065, -122.083739)
    private val marker2Position = LatLng(35.6895, 139.6917)
    private val marker3Position = LatLng(51.5074, -0.1276)
    private val blueIconUrl = "https://maps.google.com/mapfiles/kml/paddle/blu-blank.png"
    private val greenIconUrl = "https://maps.google.com/mapfiles/kml/paddle/grn-blank.png"

    private fun getPosition(manager: MarkerManager): LatLng {
        val field: Field = MarkerManager::class.java.getDeclaredField("position")
        field.isAccessible = true
        return field.get(manager) as LatLng
    }

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

    @Test
    public fun testMarkerPositionCorrect() {
        Assert.assertEquals(marker1Position, getPosition(marker1))
        Assert.assertEquals(marker2Position, getPosition(marker2))
        Assert.assertEquals(marker3Position, getPosition(marker3))
    }

    @Test
    public fun testMarkerIconUrlIsSet() {
        Assert.assertEquals(blueIconUrl, marker1.style.getIconUrl())
        Assert.assertEquals(greenIconUrl, marker2.style.getIconUrl())
    }

    @Test
    public fun testMarkerIconBitmapIsSet() {
        Assert.assertNotNull(marker1.getProperties().icon)
        Assert.assertNotNull(marker2.getProperties().icon)
    }

    @Test
    public fun testMarkerIconNotSet() {
        Assert.assertNull(marker3.getProperties().icon)
    }

    @Test
    public fun testMarkerDescription() {
        Assert.assertEquals(marker1Description, marker1.getProperties().description)
        Assert.assertEquals(marker2Description, marker2.getProperties().description)
        Assert.assertEquals(marker3Description, marker3.getProperties().description)
    }

    @Test
    public fun testMarkerRotation() {
        Assert.assertEquals(0, marker1.getProperties().rotation)
        Assert.assertEquals(90, marker2.getProperties().rotation)
        Assert.assertEquals(135, marker3.getProperties().rotation)
    }

    @Test
    public fun testMarkerVisibility() {
        Assert.assertEquals(true, marker1.getProperties().visibility)
        Assert.assertEquals(true, marker2.getProperties().visibility)
        Assert.assertEquals(false, marker3.getProperties().visibility)
    }

    @Test
    public fun testMarkerColor() {
        val marker1HueColor = 240f
        val marker2HueColor = 120f
        Assert.assertEquals(marker1HueColor, marker1.getProperties().color)
        Assert.assertEquals(marker2HueColor, marker2.getProperties().color)
    }

    @Test
    public fun testMarkerColorMode() {
        Assert.assertEquals(false, marker1.style.getIconRandomColorMode())
        Assert.assertEquals(false, marker2.style.getIconRandomColorMode())
        Assert.assertEquals(true, marker3.style.getIconRandomColorMode())
    }

    @Test
    public fun testMarkerDrawOrder() {
        Assert.assertEquals(1f, marker1.getProperties().drawOrder)
        Assert.assertEquals(2f, marker2.getProperties().drawOrder)
        Assert.assertEquals(3f, marker3.getProperties().drawOrder)
    }

    @Test
    public fun testMarkerScale() {
        val DEFAULT_ICON_SCALE = 1f // taken from KmlStyle companion object
        Assert.assertEquals(DEFAULT_ICON_SCALE, marker1.style.getIconScale())
        Assert.assertEquals(10f, marker2.style.getIconScale())
        Assert.assertEquals(DEFAULT_ICON_SCALE, marker3.style.getIconScale())
    }

    @Test
    public fun testMarkerAnchor() {
        val DEFAULT_ANCHOR = Anchor() // taken from KmlComposableManager companion object
        Assert.assertEquals(DEFAULT_ANCHOR, marker1.style.getIconAnchor())
        Assert.assertEquals(DEFAULT_ANCHOR, marker2.style.getIconAnchor())
        Assert.assertEquals(
            Anchor(0.5f, 0.5f, "fraction", "fraction"),
            marker3.style.getIconAnchor()
        )
    }
}