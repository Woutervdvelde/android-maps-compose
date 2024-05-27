
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import com.google.maps.android.compose.kml.manager.PolylineManager
import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field
import kotlin.math.round

/**
 * These tests are focused on the correct parsing of KML <LineString> into a PolylineManager.
 *
 * For this test the file "PolylineTest.kml" has been written with variations of a <LineString>
 * In total there are 6 lines defined each with different values, the following list will show the variations
 *
 *  ┌───────────────┐   ┌───────────────┐   ┌────────────────────────────┐
 *  │ first         │   │ second        │   │ third                      │
 *  ├───────────────┤   ├───────────────┤   ├────────────────────────────┤
 *  │ Points:   3   │   │ Points:   1   │   │ Points:   4                │
 *  │ Color    Black│   │ Color    Red  │   │ Color    Red (~50% alpha)  │
 *  │ Geodesic: 1   │   │ Geodesic: 1   │   │ Geodesic: 1                │
 *  │ Visible:  1   │   │ Visible:  1   │   │ Visible:  0                │
 *  │ Width:    1   │   │ Width:    15  │   │ Width:    20               │
 *  │ zIndex:   1   │   │ zIndex:   2   │   │ zIndex:   3                │
 *  └───────────────┘   └───────────────┘   └────────────────────────────┘
 *  ┌────────────────┐   ┌───────────────┐   ┌───────────────────┐
 *  │ fourth         │   │ fifth         │   │ sixth             │
 *  ├────────────────┤   ├───────────────┤   ├───────────────────┤
 *  │ Points:   3    │   │ Points:   3   │   │ Points:   40      │
 *  │ Color    Cyan  │   │ Color    Black│   │ Color     unset   │
 *  │ Geodesic: 0    │   │ Geodesic: 0   │   │ Geodesic: unset   │
 *  │ Visible:  0    │   │ Visible:  0   │   │ Visible:  unset   │
 *  │ Width:    25   │   │ Width:    100 │   │ Width:    unset   │
 *  │ zIndex:   4    │   │ zIndex:   5   │   │ zIndex:   unset   │
 *  └────────────────┘   └───────────────┘   └───────────────────┘
 *
 *  The Points represent the amount of positions, starts from 52.509,6.087 and continue down 1 third decimal at the time.
 */

public class KmlParserPolylineTest {
    private lateinit var parser: KmlParser
    private val testPositions = listOf(
        LatLng(52.509,6.087),
        LatLng(52.508,6.086),
        LatLng(52.507,6.085),
        LatLng(52.506,6.084),
        LatLng(52.505,6.083),
        LatLng(52.504,6.082),
        LatLng(52.503,6.081),
        LatLng(52.502,6.080),
        LatLng(52.501,6.079),
        LatLng(52.500,6.078),
        LatLng(52.499,6.077),
        LatLng(52.498,6.076),
        LatLng(52.497,6.075),
        LatLng(52.496,6.074),
        LatLng(52.495,6.073),
        LatLng(52.494,6.072),
        LatLng(52.493,6.071),
        LatLng(52.492,6.070),
        LatLng(52.491,6.069),
        LatLng(52.490,6.068),
        LatLng(52.489,6.067),
        LatLng(52.488,6.066),
        LatLng(52.487,6.065),
        LatLng(52.486,6.064),
        LatLng(52.485,6.063),
        LatLng(52.484,6.062),
        LatLng(52.483,6.061),
        LatLng(52.482,6.060),
        LatLng(52.481,6.059),
        LatLng(52.480,6.058),
        LatLng(52.479,6.057),
        LatLng(52.478,6.056),
        LatLng(52.477,6.055),
        LatLng(52.476,6.054),
        LatLng(52.475,6.053),
        LatLng(52.474,6.052),
        LatLng(52.473,6.051),
        LatLng(52.472,6.050),
        LatLng(52.471,6.049),
        LatLng(52.470,6.048)
    )

    private data class TestLines(
        val line1: PolylineManager,
        val line2: PolylineManager,
        val line3: PolylineManager,
        val line4: PolylineManager,
        val line5: PolylineManager,
        val line6: PolylineManager
    )

    private fun getTestLines(): TestLines {
        val lines = parser.container.getPolylines()
        val line1 = lines.find { it.properties.name == "first" }!!
        val line2 = lines.find { it.properties.name == "second" }!!
        val line3 = lines.find { it.properties.name == "third" }!!
        val line4 = lines.find { it.properties.name == "fourth" }!!
        val line5 = lines.find { it.properties.name == "fifth" }!!
        val line6 = lines.find { it.properties.name == "sixth" }!!

        return TestLines(line1, line2, line3, line4, line5, line6)
    }

    private fun getCoordinates(manager: PolylineManager): List<LatLng> {
        val field: Field = PolylineManager::class.java.getDeclaredField("coordinates")
        field.isAccessible = true
        return field.get(manager) as List<LatLng>
    }

    @Before
    public fun setUp(): Unit = runBlocking {
        val containerNestingKml = javaClass.classLoader?.getResourceAsStream("PolylineTest.kml")
            ?: throw AssertionError("Could not load sample KML file")
        val parsedKmlData = MapFileParser.parseStream(containerNestingKml)
        parsedKmlData.parser?.applyStyles(parsedKmlData.media)
        parser = parsedKmlData.parser!!
    }

    @Test
    public fun testContainerContainingAllLines() {
        val lines = parser.container.getPolylines()
        Assert.assertEquals(6, lines.count())
    }

    @Test
    public fun testCorrectNameParsed() {
        val lines = parser.container.getPolylines()
        val line1 = lines.find { it.properties.name == "first" }
        val line2 = lines.find { it.properties.name == "second" }
        val line3 = lines.find { it.properties.name == "third" }
        val line4 = lines.find { it.properties.name == "fourth" }
        val line5 = lines.find { it.properties.name == "fifth" }
        val line6 = lines.find { it.properties.name == "sixth" }

        Assert.assertNotNull(line1)
        Assert.assertNotNull(line2)
        Assert.assertNotNull(line3)
        Assert.assertNotNull(line4)
        Assert.assertNotNull(line5)
        Assert.assertNotNull(line6)
    }

    @Test
    public fun testPointsAmountCorrect() {
        val lines = getTestLines()

        Assert.assertEquals(3, getCoordinates(lines.line1).count())
        Assert.assertEquals(1, getCoordinates(lines.line2).count())
        Assert.assertEquals(4, getCoordinates(lines.line3).count())
        Assert.assertEquals(4, getCoordinates(lines.line4).count())
        Assert.assertEquals(4, getCoordinates(lines.line5).count())
        Assert.assertEquals(40, getCoordinates(lines.line6).count())
    }

    @Test
    public fun testPointsAmountNotWrong() {
        val lines = getTestLines()

        Assert.assertNotEquals(1, getCoordinates(lines.line1).count())
        Assert.assertNotEquals(3, getCoordinates(lines.line2).count())
        Assert.assertNotEquals(40, getCoordinates(lines.line3).count())
        Assert.assertNotEquals(40, getCoordinates(lines.line4).count())
        Assert.assertNotEquals(40, getCoordinates(lines.line5).count())
        Assert.assertNotEquals(0, getCoordinates(lines.line6).count())
    }

    @Test
    public fun testPointsLine1CorrectLatLongs() {
        val line = getTestLines().line1
        val coordinates = getCoordinates(line)

        Assert.assertEquals(testPositions[0], coordinates[0])
        Assert.assertEquals(testPositions[1], coordinates[1])
        Assert.assertEquals(testPositions[2], coordinates[2])
    }

    @Test
    public fun testPointsLine2CorrectLatLongs() {
        val line = getTestLines().line2

        Assert.assertEquals(testPositions[0], getCoordinates(line)[0])
    }

    @Test
    public fun testPointsLine3CorrectLatLongs() {
        val line = getTestLines().line3
        val coordinates = getCoordinates(line)

        Assert.assertEquals(testPositions[0], coordinates[0])
        Assert.assertEquals(testPositions[1], coordinates[1])
        Assert.assertEquals(testPositions[2], coordinates[2])
        Assert.assertEquals(testPositions[3], coordinates[3])
    }

    @Test
    public fun testPointsLine4CorrectLatLongs() {
        val line = getTestLines().line4
        val coordinates = getCoordinates(line)

        Assert.assertEquals(testPositions[0], coordinates[0])
        Assert.assertEquals(testPositions[1], coordinates[1])
        Assert.assertEquals(testPositions[2], coordinates[2])
        Assert.assertEquals(testPositions[3], coordinates[3])
    }

    @Test
    public fun testPointsLine5CorrectLatLongs() {
        val line = getTestLines().line5
        val coordinates = getCoordinates(line)

        Assert.assertEquals(testPositions[0], coordinates[0])
        Assert.assertEquals(testPositions[1], coordinates[1])
        Assert.assertEquals(testPositions[2], coordinates[2])
        Assert.assertEquals(testPositions[3], coordinates[3])
    }

    @Test
    public fun testPointsLine6CorrectLatLongs() {
        val line = getTestLines().line6
        val coordinates = getCoordinates(line)

        coordinates.forEachIndexed {index, latLng ->
            Assert.assertEquals(testPositions[index], latLng)
        }
    }

    @Test
    public fun testColorLine1CorrectColor() {
        val line = getTestLines().line1
        val color = line.properties.color

        Assert.assertEquals(1f, color.alpha)
        Assert.assertEquals(0f, color.red)
        Assert.assertEquals(0f, color.blue)
        Assert.assertEquals(0f, color.green)
    }

    @Test
    public fun testColorLine2CorrectColor() {
        val line = getTestLines().line2
        val color = line.properties.color

        Assert.assertEquals(1f, color.alpha)
        Assert.assertEquals(1f, color.red)
        Assert.assertEquals(0f, color.blue)
        Assert.assertEquals(0f, color.green)
    }

    @Test
    public fun testColorLine3CorrectColor() {
        val line = getTestLines().line3
        val color = line.properties.color

        Assert.assertEquals(.5f, round(color.alpha * 10) / 10)
        Assert.assertEquals(1f, color.red)
        Assert.assertEquals(0f, color.blue)
        Assert.assertEquals(0f, color.green)
    }

    @Test
    public fun testColorLine4CorrectColor() {
        val line = getTestLines().line4
        val color = line.properties.color

        Assert.assertEquals(1f, color.alpha)
        Assert.assertEquals(0f, color.red)
        Assert.assertEquals(1f, color.blue)
        Assert.assertEquals(1f, color.green)
    }

    @Test
    public fun testColorLine5CorrectColor() {
        val line = getTestLines().line5
        val color = line.properties.color

        Assert.assertEquals(1f, color.alpha)
        Assert.assertEquals(0f, color.red)
        Assert.assertEquals(0f, color.blue)
        Assert.assertEquals(1f, color.green)
    }

    @Test
    public fun testColorLine6CorrectColor() {
        val line = getTestLines().line1
        val color = line.properties.color
        val defaultColor = Color.Black // taken from PolylineManager Companion object DEFAULT_COLOR

        Assert.assertEquals(defaultColor, color)
    }

    @Test
    public fun testTessellateLinesCorrectGeodesic() {
        val lines = getTestLines()
        val defaultTessellate = false // taken from PolylineManager Companion object DEFAULT_TESSELLATE

        Assert.assertEquals(true, lines.line1.properties.tessellate)
        Assert.assertEquals(true, lines.line2.properties.tessellate)
        Assert.assertEquals(true, lines.line3.properties.tessellate)
        Assert.assertEquals(false, lines.line4.properties.tessellate)
        Assert.assertEquals(false, lines.line5.properties.tessellate)
        Assert.assertEquals(defaultTessellate, lines.line6.properties.tessellate)
    }

    @Test
    public fun testVisibilityLines() {
        val lines = getTestLines()
        val defaultVisibility = true // taken from KmlComposableProperties Companion object DEFAULT_VISIBILITY

        Assert.assertEquals(true, lines.line1.isActive.value)
        Assert.assertEquals(true, lines.line2.isActive.value)
        Assert.assertEquals(false, lines.line3.isActive.value)
        Assert.assertEquals(false, lines.line4.isActive.value)
        Assert.assertEquals(false, lines.line5.isActive.value)
        Assert.assertEquals(defaultVisibility, lines.line6.isActive.value)
    }

    @Test
    public fun testWidthLines() {
        val lines = getTestLines()
        val defaultWith = 1f // taken from PolylineManager Companion object DEFAULT_WIDTH

        Assert.assertEquals(1f, lines.line1.properties.width)
        Assert.assertEquals(15f, lines.line2.properties.width)
        Assert.assertEquals(20f, lines.line3.properties.width)
        Assert.assertEquals(25f, lines.line4.properties.width)
        Assert.assertEquals(100f, lines.line5.properties.width)
        Assert.assertEquals(defaultWith, lines.line6.properties.width)
    }

    @Test
    public fun testDrawOrderLinesZIndex() {
        val lines = getTestLines()
        val defaultDrawOrder = 0f // taken from IKmlComposableProperties Companion object DEFAULT_DRAW_ORDER

        Assert.assertEquals(1f, lines.line1.properties.drawOrder)
        Assert.assertEquals(2f, lines.line2.properties.drawOrder)
        Assert.assertEquals(3f, lines.line3.properties.drawOrder)
        Assert.assertEquals(4f, lines.line4.properties.drawOrder)
        Assert.assertEquals(5f, lines.line5.properties.drawOrder)
        Assert.assertEquals(defaultDrawOrder, lines.line6.properties.drawOrder)
    }

    @Test
    public fun testEndCapSetter() {
        val line = getTestLines().line1

        line.setEndCap(RoundCap())

        Assert.assertEquals(RoundCap::class.java, line.properties.endCap::class.java)
        Assert.assertNotEquals(ButtCap::class.java, line.properties.endCap::class.java)
        Assert.assertNotEquals(SquareCap::class.java, line.properties.endCap::class.java)
    }

    @Test
    public fun testStartCapSetter() {
        val line = getTestLines().line1
        line.setStartCap(SquareCap())

        Assert.assertEquals(SquareCap::class.java, line.properties.startCap::class.java)
        Assert.assertNotEquals(ButtCap::class.java, line.properties.startCap::class.java)
        Assert.assertNotEquals(RoundCap::class.java, line.properties.startCap::class.java)
    }

    @Test
    public fun testDefaultJointTypeLine() {
        val lines = getTestLines()
        val defaultJointType = JointType.DEFAULT // taken from PolylineManager Companion object DEFAULT_JOINT_TYPE

        Assert.assertEquals(defaultJointType, lines.line1.properties.jointType)
        Assert.assertEquals(defaultJointType, lines.line2.properties.jointType)
        Assert.assertEquals(defaultJointType, lines.line3.properties.jointType)
        Assert.assertEquals(defaultJointType, lines.line4.properties.jointType)
        Assert.assertEquals(defaultJointType, lines.line5.properties.jointType)
        Assert.assertEquals(defaultJointType, lines.line6.properties.jointType)
    }

    @Test
    public fun testJointTypeSetter() {
        val line = getTestLines().line1
        val expectedType = JointType.ROUND
        line.setJointType(expectedType)

        Assert.assertEquals(expectedType, line.properties.jointType)
    }

    @Test
    public fun testPatternSetter() {
        val line = getTestLines().line1
        val expectedPattern = mutableListOf(
            Gap(10f),
            Dash(5f),
            Gap(15f),
            Dot()
        )
        line.setPattern(expectedPattern)

        Assert.assertEquals(expectedPattern, line.properties.pattern)
    }
}