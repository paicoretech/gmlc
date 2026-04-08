package org.mobicents.gmlc.slee.primitives;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import java.io.StringReader;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 */
public class CivicAddressXmlReader {

    private static final Logger logger = Logger.getLogger(CivicAddressXmlReader.class.getName());

    private CivicAddressElements civicAddress = new CivicAddressElements();

    public CivicAddressXmlReader() {
    }

    public CivicAddressElements getCivicAddressElements() {
        return civicAddress;
    }

    public void civicAddressXMLReader(String civicAddressXmlData) {
        try {

            JAXBContext jaxbContextCivicAddress = JAXBContext.newInstance(CivicAddressElements.class);
            Unmarshaller jaxbUnmarshallerCivicAddress = jaxbContextCivicAddress.createUnmarshaller();
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            NamespaceFilter namespaceFilter = new NamespaceFilter();
            namespaceFilter.setParent(xmlReader);
            StringReader xmlCivicAddress = new StringReader(civicAddressXmlData);
            InputSource inputSource = new InputSource(xmlCivicAddress);
            SAXSource source = new SAXSource(namespaceFilter, inputSource);
            this.civicAddress = (CivicAddressElements) jaxbUnmarshallerCivicAddress.unmarshal(source);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
