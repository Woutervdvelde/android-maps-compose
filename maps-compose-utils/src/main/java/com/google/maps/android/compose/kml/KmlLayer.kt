package com.google.maps.android.compose.kml

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.kml.parser.KmlParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

@Composable
@GoogleMapComposable
public fun KmlLayer(
    stream: InputStream
) {
    val bis = BufferedInputStream(stream)
    bis.mark(1024)
    val zip = ZipInputStream(bis)
    var parser: KmlParser? = null

    try {
        var entry = zip.nextEntry
        if (entry != null) { // is a KMZ zip file
            val images = HashMap<String, Bitmap>()
            while (entry != null) {
                if (parser == null && entry.name.lowercase().endsWith(".kml")) {
                    parser = parseKml(zip)
                } else {
                    val bitmap = BitmapFactory.decodeStream(zip)
                    if (bitmap != null) {
                        images[entry.name] = bitmap
                    } else {
                        Log.w("KmlLayer", "Unsupported KMZ contents file type: ${entry.name}")
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
        stream.close()
        bis.close()
        zip.close()
    }

    parser?.container?.getContainers()?.forEach {container ->
        container.getMarkers().forEach { marker ->
            run {
                Marker(
                    state = MarkerState(marker.getPosition())
                )
            }
        }
        container.getContainers().forEach { container ->
            container.getMarkers().forEach { marker ->
                run {
                    Marker(
                        state = MarkerState(marker.getPosition())
                    )
                }
            }
            container.getContainers().forEach { container ->
                container.getMarkers().forEach { marker ->
                    run {
                        Marker(
                            state = MarkerState(marker.getPosition())
                        )
                    }
                }
            }
        }
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