package org.mobicents.gmlc.slee.primitives;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.map.primitives.TbcdString;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class TrackingAreaId5GSImpl extends OctetStringBase implements TrackingAreaId5GS {

    private static final Logger logger = Logger.getLogger(TrackingAreaId5GSImpl.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";
    private static final String TAC = "tac";

    private static final String DATA = "data";
    private static final String DEFAULT_VALUE = null;
    private static final int DEFAULT_INT_VALUE = 0;

    public TrackingAreaId5GSImpl() { super(6,6,"5GSTrackingAreaId");}

    public TrackingAreaId5GSImpl(byte[] data) {super(6,6,"5GSTrackingAreaId", data);}

    public byte[] getData() {
        return data;
    }

    public void setData(int mcc, int mnc, int tac) throws MAPException {
        if (mcc < 1 || mcc > 999)
            throw new MAPException("Bad MCC value");
        if (mnc < 0 || mnc > 999)
            throw new MAPException("Bad MNC value");

        this.data = new byte[6];

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (mcc < 100)
            sb.append("0");
        if (mcc < 10)
            sb.append("0");
        sb.append(mcc);

        if (mnc < 100) {
            if (mnc < 10)
                sb2.append("0");
            sb2.append(mnc);
        } else {
            sb.append(mnc % 10);
            sb2.append(mnc / 10);
        }

        AsnOutputStream asnOs = new AsnOutputStream();
        TbcdString.encodeString(asnOs, sb.toString());
        System.arraycopy(asnOs.toByteArray(), 0, this.data, 0, 2);

        asnOs = new AsnOutputStream();
        TbcdString.encodeString(asnOs, sb2.toString());
        System.arraycopy(asnOs.toByteArray(), 0, this.data, 2, 1);

        data[3] = (byte) ((tac >> 16) & 0x0F);
        data[4] = (byte) ((tac >> 8) & 0xFF);
        data[5] = (byte) (tac & 0xFF);
    }

    public int getMCC() throws MAPException {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 6)
            throw new MAPException("Data length must equal 6");

        AsnInputStream ansIS = new AsnInputStream(data);
        String res;
        try {
            res = TbcdString.decodeString(ansIS, 3);
        } catch (IOException e) {
            throw new MAPException("IOException when decoding 5GSTrackingAreaId: " + e.getMessage(), e);
        } catch (MAPParsingComponentException e) {
            throw new MAPException("MAPParsingComponentException when decoding 5GSTrackingAreaId: " + e.getMessage(), e);
        }

        if (res.length() < 5 || res.length() > 6)
            throw new MAPException("Decoded TbcdString must equal 5 or 6");

        String sMcc = res.substring(0, 3);

        return Integer.parseInt(sMcc);
    }

    public int getMNC() throws MAPException {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 6)
            throw new MAPException("Data length must equal 6");

        AsnInputStream ansIS = new AsnInputStream(data);
        String res;
        try {
            res = TbcdString.decodeString(ansIS, 3);
        } catch (IOException e) {
            throw new MAPException("IOException when decoding 5GSTrackingAreaId: " + e.getMessage(), e);
        } catch (MAPParsingComponentException e) {
            throw new MAPException("MAPParsingComponentException when decoding 5GSTrackingAreaId: " + e.getMessage(), e);
        }

        if (res.length() < 5 || res.length() > 6)
            throw new MAPException("Decoded TbcdString must equal 5 or 6");

        String sMnc;
        if (res.length() == 5) {
            sMnc = res.substring(3);
        } else {
            sMnc = res.substring(4) + res.charAt(3);
        }

        return Integer.parseInt(sMnc);
    }

    public int get5GSTAC() throws MAPException {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 6)
            throw new MAPException("Data length must equal 6");

        return ((data[3] & 0x0F) << 16) + ((data[4] & 0xFF) << 8) + (data[5] & 0xFF);
    }

    @Override
    public String toString() {

        int mcc = 0;
        int mnc = 0;
        int tac = 0;
        boolean correctData = false;

        try {
            mcc = this.getMCC();
            mnc = this.getMNC();
            tac = this.get5GSTAC();
            correctData = true;
        } catch (MAPException e) {
            logger.error(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this._PrimitiveName);
        sb.append(" [");
        if (correctData) {
            sb.append(MCC+"=");
            sb.append(mcc);
            sb.append(", "+MNC+"=");
            sb.append(mnc);
            sb.append(", "+TAC+"=");
            sb.append(tac);
        } else {
            sb.append("Data=");
            sb.append(this.printDataArr());
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<TrackingAreaId5GSImpl> TA_ID_5GS_XML = new XMLFormat<>(TrackingAreaId5GSImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, TrackingAreaId5GSImpl taId) throws XMLStreamException {
            int mcc = xml.getAttribute(MCC, DEFAULT_INT_VALUE);
            int mnc = xml.getAttribute(MNC, DEFAULT_INT_VALUE);
            int tac = xml.getAttribute(TAC, DEFAULT_INT_VALUE);

            try {
                taId.setData(mcc, mnc, tac);
            } catch (MAPException e) {
                throw new XMLStreamException("MAPException when deserializing TrackingAreaId5GSImpl", e);
            }
        }

        @Override
        public void write(TrackingAreaId5GSImpl taId, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            try {
                xml.setAttribute(MCC, taId.getMCC());
                xml.setAttribute(MNC, taId.getMNC());
                xml.setAttribute(TAC, taId.get5GSTAC());
            } catch (MAPException e) {
                throw new XMLStreamException("MAPException when serializing TrackingAreaId5GSImpl", e);
            }
        }
    };
}
