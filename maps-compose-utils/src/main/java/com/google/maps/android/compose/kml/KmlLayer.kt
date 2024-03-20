package com.google.maps.android.compose.kml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.kml.manager.ContainerManager
import com.google.maps.android.compose.kml.parser.KmlParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

@Composable
@GoogleMapComposable
public fun KmlLayer(
    stream: InputStream,
    context: Context,
    onParsed: (ContainerManager?) -> Unit
) {
    val images = HashMap<String, Bitmap>()
    var parser: KmlParser? = null
    parseStream(stream) { parsedParser, parsedImages ->
        parser = parsedParser
        images.putAll(parsedImages)
    }

    parser?.container?.Render()

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            parser?.applyStyles(images, context)
        }.invokeOnCompletion {
            onParsed(parser?.container)
        }
    }
}

private fun parseStream(stream: InputStream, onParsed: (KmlParser?, Map<String, Bitmap>) -> Unit) {
    val images = HashMap<String, Bitmap>()
    var parser: KmlParser? = null

    stream.use { inputStream ->
        val bis = BufferedInputStream(inputStream)
        bis.mark(1024)
        val zip = ZipInputStream(bis)

        try {
            var entry = zip.nextEntry
            if (entry != null) { // is a KMZ zip file
                while (entry != null) {
                    if (parser == null && entry.name.lowercase().endsWith(".kml")) {
                        parser = parseKml(zip)
                    } else {
                        val bitmap = BitmapFactory.decodeStream(zip)
                        if (bitmap != null) {
                            images[entry.name] = bitmap
                        } else {
                            Log.w(
                                "KmlLayer",
                                "Unsupported KMZ contents file type: ${entry.name}"
                            )
                        }
                    }
                    entry = zip.nextEntry
                }

                if (parser == null) {
                    throw IllegalArgumentException("KML not found in InputStream")
                }
            } else {
                bis.reset()
                parser = parseKml(bis)
            }
        } finally {
            bis.close()
            zip.close()
        }

        onParsed(parser, images)
    }
}

private fun parseKml(stream: InputStream): KmlParser {
    val xmlPullParser = createXmlParser(stream)
    val parser = KmlParser(xmlPullParser)
    parser.parseKml()
    return parser
}

private fun createXmlParser(stream: InputStream): XmlPullParser {
    val factory = XmlPullParserFactory.newInstance()
    factory.isNamespaceAware = true
    val parser = factory.newPullParser()
    parser.setInput(stream, null)
    return parser;
}