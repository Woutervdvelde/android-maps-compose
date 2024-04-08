
import com.google.maps.android.compose.kml.parser.KmlParser
import com.google.maps.android.compose.kml.parser.MapFileParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
public class KmlParserContainerTest {
    private lateinit var parser: KmlParser

    @Before
    public fun setUp(): Unit = runBlocking {
        val containerNestingKml = javaClass.classLoader?.getResourceAsStream("ContainersNesting.kml")
            ?: throw AssertionError("Could not load sample KML file")
        val parsedKmlData = MapFileParser.parseStream(containerNestingKml)
        parsedKmlData.parser?.applyStyles(parsedKmlData.media)
        parser = parsedKmlData.parser!!
    }

    @Test
    public fun testRootContainerNameMatches() {
        Assert.assertEquals(parser.container.getName(), "Root Document")
    }

    @Test
    public fun testRootContainerNameIsNotChilds() {
        Assert.assertNotEquals(parser.container.getName(), parser.container.getContainers().first().getName())
    }
}