package org.mobicents.gmlc.slee.primitives;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.map.primitives.TbcdString;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class UTRANCellIdImpl extends OctetStringBase implements UTRANCellId {

    private static final Logger logger = Logger.getLogger(UTRANCellIdImpl.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";
    private static final String UCI = "utranCellId";

    private static final int DEFAULT_INT_VALUE = 0;
    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public UTRANCellIdImpl() {
        super(7, 7, "UtranCellId");
    }

    public UTRANCellIdImpl(byte[] data) {
        super(7, 7, "UtranCellId", data);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(int mcc, int mnc, int uci) throws MAPException {
        if (mcc < 1 || mcc > 999)
            throw new MAPException("Bad MCC value");
        if (mnc < 0 || mnc > 999)
            throw new MAPException("Bad MNC value");

        this.data = new byte[7];

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

        this.data[3] = (byte) (uci >> 24 & 255);
        this.data[4] = (byte) (uci >> 16 & 255);
        this.data[5] = (byte) (uci >> 8 & 255);
        this.data[6] = (byte) (uci & 255);
    }

    public int getMCC() throws Exception {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 7)
            throw new MAPException("Data length must equal 7");

        AsnInputStream ansIS = new AsnInputStream(data);
        String res;
        try {
            res = TbcdString.decodeString(ansIS, 3);
        } catch (IOException e) {
            throw new IOException("IOException when decoding UtranCellId: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Parsing Component Exception when decoding UtranCellId: " + e.getMessage(), e);
        }

        if (res.length() < 5 || res.length() > 6)
            throw new MAPException("Decoded TbcdString must equal 5 or 6");

        String sMcc = res.substring(0, 3);

        return Integer.parseInt(sMcc);
    }

    public int getMNC() throws Exception {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 7)
            throw new MAPException("Data length must equal 7");

        AsnInputStream ansIS = new AsnInputStream(data);
        String res;
        try {
            res = TbcdString.decodeString(ansIS, 3);
        } catch (IOException e) {
            throw new IOException("IOException when decoding UtranCellId: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Parsing Component Exception when decoding UtranCellId: " + e.getMessage(), e);
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

    public int getUci() throws MAPException {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 7)
            throw new MAPException("Data length must equal 7");

        return ((this.data[3] & 255) << 24) + ((this.data[4] & 255) << 16) + ((this.data[5] & 255) << 8) + (this.data[6] & 255);
    }

    @Override
    public String toString() {

        int mcc = 0;
        int mnc = 0;
        long uci = 0;
        boolean correctData = false;

        try {
            try {
                mcc = this.getMCC();
                mnc = this.getMNC();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            uci = this.getUci();
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
            sb.append(", "+UCI+"=");
            sb.append(uci);
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
    protected static final XMLFormat<UTRANCellIdImpl> UTRAN_CELL_ID_XML_FORMAT = new XMLFormat<>(UTRANCellIdImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, UTRANCellIdImpl utranCellId) throws XMLStreamException {
            int mcc = xml.getAttribute(MCC, DEFAULT_INT_VALUE);
            int mnc = xml.getAttribute(MNC, DEFAULT_INT_VALUE);
            int uci = xml.getAttribute(UCI, DEFAULT_INT_VALUE);

            try {
                utranCellId.setData(mcc, mnc, uci);
            } catch (MAPException e) {
                throw new XMLStreamException("MAPException when deserializing UTRANCellIdImpl", e);
            }
        }

        @Override
        public void write(UTRANCellIdImpl utranCellId, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            try {
                xml.setAttribute(MCC, utranCellId.getMCC());
                xml.setAttribute(MNC, utranCellId.getMNC());
                xml.setAttribute(UCI, utranCellId.getUci());
            } catch (MAPException e) {
                throw new XMLStreamException("MAPException when serializing UTRANCellIdImpl", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
