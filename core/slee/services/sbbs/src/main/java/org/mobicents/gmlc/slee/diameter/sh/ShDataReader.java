package org.mobicents.gmlc.slee.diameter.sh;

import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.io.StringReader;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShDataReader {

    private static final Logger logger = Logger.getLogger(ShDataReader.class.getName());

    private PublicIds shPublicIdentifiers = new PublicIds();
    private CSLocationInformation shCSLocationInfo = new CSLocationInformation();
    private PSLocationInformation shPSLocationInfo = new PSLocationInformation();
    private Extension shEPSLocationInfo = new Extension();
    private Extension sh5GSLocationInfo = new Extension();

    public ShDataReader() {
    }

    public PublicIds getShPublicIdentifiers() {
        return shPublicIdentifiers;
    }

    public void setShPublicIdentifiers(PublicIds shPublicIdentifiers) {
        this.shPublicIdentifiers = shPublicIdentifiers;
    }

    public CSLocationInformation getShCSLocationInfo() {
        return shCSLocationInfo;
    }

    public void setShCSLocationInfo(CSLocationInformation shCSLocationInfo) {
        this.shCSLocationInfo = shCSLocationInfo;
    }

    public PSLocationInformation getShPSLocationInfo() {
        return shPSLocationInfo;
    }

    public void setShPSLocationInfo(PSLocationInformation shPSLocationInfo) {
        this.shPSLocationInfo = shPSLocationInfo;
    }

    public Extension getShEPSLocationInfo() {
        return shEPSLocationInfo;
    }

    public void setShEPSLocationInfo(Extension shEPSLocationInfo) {
        this.shEPSLocationInfo = shEPSLocationInfo;
    }

    public Extension getSh5GSLocationInfo() {
        return sh5GSLocationInfo;
    }

    public void setSh5GSLocationInfo(Extension sh5GSLocationInfo) {
        this.sh5GSLocationInfo = sh5GSLocationInfo;
    }

    public void ShXMLReader(String shData) {

        try {

            JAXBContext jaxbPublicIdentifiersContext = JAXBContext.newInstance(PublicIdentifiers.class);
            Unmarshaller jaxbUnmarshallerPublicIdentifiers = jaxbPublicIdentifiersContext.createUnmarshaller();

            JAXBContext jaxbCSLocationInfoContext = JAXBContext.newInstance(CircuitSwitchedLocationInformation.class);
            Unmarshaller jaxbUnmarshallerCSLocationInfo = jaxbCSLocationInfoContext.createUnmarshaller();

            JAXBContext jaxbPSLocationInfoContext = JAXBContext.newInstance(PacketSwitchedLocationInformation.class);
            Unmarshaller jaxbUnmarshallerPSLocationInfo = jaxbPSLocationInfoContext.createUnmarshaller();

            JAXBContext jaxbEPSLocationInfoContext = JAXBContext.newInstance(Extension.class);
            Unmarshaller jaxbUnmarshallerEPSLocationInfo = jaxbEPSLocationInfoContext.createUnmarshaller();

            JAXBContext jaxv5GSLocationInfoContext = JAXBContext.newInstance(Extension.class);
            Unmarshaller jaxbUnmarshaller5GSLocationInfo = jaxv5GSLocationInfoContext.createUnmarshaller();

            StringReader xmlShDataPublicIdentifiers = new StringReader(shData);
            PublicIdentifiers shPublicIdentifiers = (PublicIdentifiers) jaxbUnmarshallerPublicIdentifiers.unmarshal(xmlShDataPublicIdentifiers);
            this.shPublicIdentifiers = shPublicIdentifiers.getPublicIdentifiers();

            StringReader xmlShDataCSLocationInfo = new StringReader(shData);
            CircuitSwitchedLocationInformation shDataCsLocation = (CircuitSwitchedLocationInformation) jaxbUnmarshallerCSLocationInfo.unmarshal(xmlShDataCSLocationInfo);
            this.shCSLocationInfo = shDataCsLocation.getCsLocationInformation();

            StringReader xmlShDataPSLocationInfo = new StringReader(shData);
            PacketSwitchedLocationInformation shDataPsLocation = (PacketSwitchedLocationInformation) jaxbUnmarshallerPSLocationInfo.unmarshal(xmlShDataPSLocationInfo);
            this.shPSLocationInfo = shDataPsLocation.getPsLocationInformation();

            StringReader xmlShDataEPSLocationInfo = new StringReader(shData);
            Extension shDataEpsLocation = (Extension) jaxbUnmarshallerEPSLocationInfo.unmarshal(xmlShDataEPSLocationInfo);
            this.shEPSLocationInfo = shDataEpsLocation.getExtension();

            StringReader xmlShData5GSLocationInfo = new StringReader(shData);
            Extension shData5gsLocation = (Extension) jaxbUnmarshaller5GSLocationInfo.unmarshal(xmlShData5GSLocationInfo);
            this.sh5GSLocationInfo = shData5gsLocation.getExtension();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
