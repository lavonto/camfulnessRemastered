package fi.hamk.calmfulnessV2.helpers;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Class for creating a list of LatLng positions from a .gpx file
 */
public class GpxHandler {
    public static List<LatLng> decodeGPX(final InputStream stream) throws ParserConfigurationException, IOException, SAXException {

        final List<LatLng> latLngs = new ArrayList<>();

        // Get Element object
        final Element rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream).getDocumentElement();

        // Find specific element using Element object (tag) and form nodelist
        final NodeList nodelist = rootElement.getElementsByTagName("trkpt");

        // Go through entries in nodelist, extract latitude and longitude, form new LatLng object and add it to latLngs
        for (int i = 0; i < nodelist.getLength(); i++) {
            final NamedNodeMap attributes = nodelist.item(i).getAttributes();
            final LatLng latLng = new LatLng(
                    Double.parseDouble(attributes.getNamedItem("lat").getTextContent()),
                    Double.parseDouble(attributes.getNamedItem("lon").getTextContent()));
            latLngs.add(latLng);
        }
        // Return latLngs containing LatLng objects
        return latLngs;
    }
}
