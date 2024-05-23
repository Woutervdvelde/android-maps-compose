import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * These tests are focussed on extracting the structure from a KML file.
 *
 * KML Structure ContainersNesting.kml
 * - Document: Root Document
 *   |- Folder: Folder 1
 *   |  |- Folder: Folder 2
 *   |  |  |- Folder: Folder 3
 *   |  |  |  |- Placemark: Folder 3 placemark
 *   |  |  |- Placemark: Folder 2 placemark
 *   |  |- Placemark: Folder 1 placemark
 *   |- Folder: Folder 4
 *   |  |- Document: Document 1
 *   |  |  |- Folder: Folder 5
 *   |  |  |- Placemark: Document 1 placemark
 */

public class KmlParserContainerTest {
    private lateinit var parser: KmlParser

    @Before
    public fun setUp(): Unit = runBlocking {
        val containerNestingKml =
            javaClass.classLoader?.getResourceAsStream("ContainersNesting.kml")
                ?: throw AssertionError("Could not load sample KML file")
        val parsedKmlData = MapFileParser.parseStream(containerNestingKml)
        parsedKmlData.parser?.applyStyles(parsedKmlData.media)
        parser = parsedKmlData.parser!!
    }

    @Test
    public fun testRootContainerNameMatches() {
        Assert.assertEquals("Root Document", parser.container.getName())
    }

    @Test
    public fun testRootContainerNameIsNotChildName() {
        Assert.assertNotEquals(
            parser.container.getName(),
            parser.container.getContainers().first().getName()
        )
    }

    @Test
    public fun testParsedContainersOrder() {
        Assert.assertEquals("Folder 1", parser.container.getContainers().first().getName())
        Assert.assertEquals(
            "Folder 2",
            parser.container.getContainers().first().getContainers().first().getName()
        )
        Assert.assertEquals(
            "Folder 3",
            parser.container.getContainers().first().getContainers().first().getContainers().first()
                .getName()
        )
        Assert.assertEquals("Folder 4", parser.container.getContainers()[1].getName())
        Assert.assertEquals(
            "Folder 5",
            parser.container.getContainers()[1].getContainers().first().getContainers().first()
                .getName()
        )
    }

    @Test
    public fun testFolderContainingDocument() {
        Assert.assertEquals(
            "Document 1",
            parser.container.getContainers()[1].getContainers().first().getName()
        )
    }

    @Test
    public fun testMarkerInCorrectContainer() {
        Assert.assertEquals(
            "Folder 3 placemark",
            parser.container
                .getContainers().first()
                .getContainers().first()
                .getContainers().first()
                .getMarkers().first()
                .getProperties().name
        )

        Assert.assertEquals(
            "Document 1 placemark",
            parser.container
                .getContainers()[1]
                .getContainers().first()
                .getMarkers().first()
                .getProperties().name
        )
    }

    @Test
    public fun testEmptyMarkerManagerInContainer() {
        Assert.assertEquals(
            0,
            parser.container.getMarkers().count()
        )

        Assert.assertEquals(
            0,
            parser.container
                .getContainers()[1]
                .getMarkers().count()
        )
    }
}