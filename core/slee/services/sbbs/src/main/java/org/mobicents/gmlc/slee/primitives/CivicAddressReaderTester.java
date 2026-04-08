package org.mobicents.gmlc.slee.primitives;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CivicAddressReaderTester {

    private static final Logger logger = Logger.getLogger(CivicAddressReaderTester.class.getName());

    public static void main(String[] args) {

        try {
            CivicAddressXmlReader civicAddressXmlReader = new CivicAddressXmlReader();
            File XMLfile = new File("core/slee/services/sbbs/src/main/java/org/mobicents/gmlc/slee/primitives/civicAddress5139_§5_Example.xml");
            String xmlFileToStr = FileUtils.readFileToString(XMLfile, "UTF-8");
            civicAddressXmlReader.civicAddressXMLReader(xmlFileToStr);
            CivicAddressElements civicAddressElements = civicAddressXmlReader.getCivicAddressElements();
            String addressElements = getAddressElements(civicAddressElements);
            logger.info("Test importing XML file taken from IETF RFC 5139 §5");
            logger.info(addressElements);

            String civicAddressString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<civicAddress>\n" +
                    "    <country>US</country>\n" +
                    "    <A1>New York</A1>\n" +
                    "    <A3>New York</A3>\n" +
                    "    <A4>Broadway</A4>\n" +
                    "    <HNO>123</HNO>\n" +
                    "    <LOC>Suite 75</LOC>\n" +
                    "    <PC>10027-0401</PC>\n" +
                    "</civicAddress>";
            civicAddressXmlReader = new CivicAddressXmlReader();
            civicAddressXmlReader.civicAddressXMLReader(civicAddressString);
            civicAddressElements = civicAddressXmlReader.getCivicAddressElements();
            addressElements = getAddressElements(civicAddressElements);
            logger.info("\nTest from String with no properties such as lang or namespace");
            logger.info(addressElements);


            String caRfc5139Example = "<civicAddress xml:lang=\"en-AU\"\n" +
                    "     xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr\">\n" +
                    "     <country>AU</country>\n" +
                    "     <A1>NSW</A1>\n" +
                    "     <A3>Wollongong</A3>\n" +
                    "     <A4>North Wollongong</A4>\n" +
                    "     <RD>Flinders</RD>\n" +
                    "     <STS>Street</STS>\n" +
                    "     <RDBR>Campbell Street</RDBR>\n" +
                    "     <LMK>Gilligan's Island</LMK>\n" +
                    "     <LOC>Corner</LOC>\n" +
                    "     <NAM>Video Rental Store</NAM>\n" +
                    "     <PC>2500</PC>\n" +
                    "     <ROOM>Westerns and Classics</ROOM>\n" +
                    "     <PLC>store</PLC>\n" +
                    "     <POBOX>Private Box 15</POBOX>\n" +
                    "   </civicAddress>";
            civicAddressXmlReader = new CivicAddressXmlReader();
            civicAddressXmlReader.civicAddressXMLReader(caRfc5139Example);
            civicAddressElements = civicAddressXmlReader.getCivicAddressElements();
            addressElements = getAddressElements(civicAddressElements);
            logger.info("\nTest from String taken from IETF RFC 5139 §5 Example");
            logger.info(addressElements);

            String caRfc6848_5_6_Fig7 = "<civicAddress xml:lang=\"en-US\"\n" +
                    "        xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr\"\n" +
                    "        xmlns:cae=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr:ext\">\n" +
                    "     <country>US</country>\n" +
                    "     <A1>CA</A1>\n" +
                    "     <A2>Sacramento</A2>\n" +
                    "     <RD>I5</RD>\n" +
                    "     <cae:MP>248</cae:MP>\n" +
                    "     <cae:PN>22-109-689</cae:PN>\n" +
                    "   </civicAddress>";
            civicAddressXmlReader = new CivicAddressXmlReader();
            civicAddressXmlReader.civicAddressXMLReader(caRfc6848_5_6_Fig7);
            civicAddressElements = civicAddressXmlReader.getCivicAddressElements();
            addressElements = getAddressElements(civicAddressElements);
            logger.info("\nTest from IETF RFC 6848 §5.6 Figure 7");
            logger.info(addressElements);

            String caRfc6848_5_6_Fig8 = "<civicAddress xml:lang=\"en-US\"\n" +
                    "        xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr\"\n" +
                    "        xmlns:cae=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr:ext\">\n" +
                    "     <country>US</country>\n" +
                    "     <A1>CA</A1>\n" +
                    "     <A2>Sacramento</A2>\n" +
                    "     <RD>Colorado</RD>\n" +
                    "     <HNO>223</HNO>\n" +
                    "     <cae:STP>Boulevard</cae:STP>\n" +
                    "     <cae:HNP>A</cae:HNP>\n" +
                    "   </civicAddress>";
            civicAddressXmlReader = new CivicAddressXmlReader();
            civicAddressXmlReader.civicAddressXMLReader(caRfc6848_5_6_Fig8);
            civicAddressElements = civicAddressXmlReader.getCivicAddressElements();
            addressElements = getAddressElements(civicAddressElements);
            logger.info("\nTest from IETF RFC 6848 §5.6 Figure 8");
            logger.info(addressElements);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String getAddressElements(CivicAddressElements civicAddressElements) {
        StringBuilder sb = new StringBuilder();
        sb.append("civicAddress: ");
        if (civicAddressElements != null) {
            if (civicAddressElements.getCountry() != null)
                sb.append("country=").append(civicAddressElements.getCountry());
            if (civicAddressElements.getA1() != null)
                sb.append(", ").append("A1=").append(civicAddressElements.getA1());
            if (civicAddressElements.getA2() != null)
                sb.append(", ").append("A2=").append(civicAddressElements.getA2());
            if (civicAddressElements.getA3() != null)
                sb.append(", ").append("A3=").append(civicAddressElements.getA3());
            if (civicAddressElements.getA4() != null)
                sb.append(", ").append("A4=").append(civicAddressElements.getA4());
            if (civicAddressElements.getA5() != null)
                sb.append(", ").append("A5=").append(civicAddressElements.getA5());
            if (civicAddressElements.getA6() != null)
                sb.append(", ").append("A6=").append(civicAddressElements.getA6());
            if (civicAddressElements.getPrm() != null)
                sb.append(", ").append("PRM=").append(civicAddressElements.getPrm());
            if (civicAddressElements.getPrd() != null)
                sb.append(", ").append("PRD=").append(civicAddressElements.getPrd());
            if (civicAddressElements.getRd() != null)
                sb.append(", ").append("RD=").append(civicAddressElements.getRd());
            if (civicAddressElements.getSts() != null)
                sb.append(", ").append("STS=").append(civicAddressElements.getSts());
            if (civicAddressElements.getPrd() != null)
                sb.append(", ").append("POD=").append(civicAddressElements.getPod());
            if (civicAddressElements.getPom() != null)
                sb.append(", ").append("POM=").append(civicAddressElements.getPom());
            if (civicAddressElements.getRdsec() != null)
                sb.append(", ").append("RDSEC=").append(civicAddressElements.getRdsec());
            if (civicAddressElements.getRdbr() != null)
                sb.append(", ").append("RDBR=").append(civicAddressElements.getRdbr());
            if (civicAddressElements.getRdsubbr() != null)
                sb.append(", ").append("RDSUBBR=").append(civicAddressElements.getRdsubbr());
            if (civicAddressElements.getHno() != null)
                sb.append(", ").append("HNO=").append(civicAddressElements.getHno());
            if (civicAddressElements.getHns() != null)
                sb.append(", ").append("HNS=").append(civicAddressElements.getHns());
            if (civicAddressElements.getLmk() != null)
                sb.append(", ").append("LMK=").append(civicAddressElements.getLmk());
            if (civicAddressElements.getLoc() != null)
                sb.append(", ").append("LOC=").append(civicAddressElements.getLoc());
            if (civicAddressElements.getFlr() != null)
                sb.append(", ").append("FLR=").append(civicAddressElements.getFlr());
            if (civicAddressElements.getNam() != null)
                sb.append(", ").append("NAM=").append(civicAddressElements.getNam());
            if (civicAddressElements.getPc() != null)
                sb.append(", ").append("PC=").append(civicAddressElements.getPc());
            if (civicAddressElements.getBld() != null)
                sb.append(", ").append("BLD=").append(civicAddressElements.getBld());
            if (civicAddressElements.getUnit() != null)
                sb.append(", ").append("UNIT=").append(civicAddressElements.getUnit());
            if (civicAddressElements.getRoom() != null)
                sb.append(", ").append("ROOM=").append(civicAddressElements.getRoom());
            if (civicAddressElements.getSeat() != null)
                sb.append(", ").append("SEAT=").append(civicAddressElements.getSeat());
            if (civicAddressElements.getPlc() != null)
                sb.append(", ").append("PLC=").append(civicAddressElements.getPlc());
            if (civicAddressElements.getPcn() != null)
                sb.append(", ").append("PCN=").append(civicAddressElements.getPcn());
            if (civicAddressElements.getPobox() != null)
                sb.append(", ").append("POBOX=").append(civicAddressElements.getPobox());
            if (civicAddressElements.getPn() != null)
                sb.append(", ").append("PN=").append(civicAddressElements.getPn());
            if (civicAddressElements.getMp() != null)
                sb.append(", ").append("MP=").append(civicAddressElements.getMp());
            if (civicAddressElements.getStp() != null)
                sb.append(", ").append("STP=").append(civicAddressElements.getStp());
            if (civicAddressElements.getHnp() != null)
                sb.append(", ").append("HNP=").append(civicAddressElements.getHnp());
        }
        return sb.toString();
    }
}
