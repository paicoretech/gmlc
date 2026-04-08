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
public class NRCellGlobalIdImpl extends OctetStringBase implements NRCellGlobalId {

    private static final Logger logger = Logger.getLogger(NRCellGlobalIdImpl.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";
    private static final String NCI = "nci";

    private static final String DATA = "data";
    private static final int DEFAULT_INT_VALUE = 0;
    private static final long DEFAULT_LONG_VALUE = 0;

    private static final String DEFAULT_VALUE = null;

    public NRCellGlobalIdImpl() {
        super(8, 8, "NRCellGlobalId");
    }

    public NRCellGlobalIdImpl(byte[] data) {
        super(8, 8, "NRCellGlobalId", data);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(int mcc, int mnc, long nci) throws MAPException {
        if (mcc < 1 || mcc > 999)
            throw new MAPException("Bad MCC value");
        if (mnc < 0 || mnc > 999)
            throw new MAPException("Bad MNC value");

        this.data = new byte[8];

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

        data[3] = (byte) ((nci >> 32) & 0x0F);
        data[4] = (byte) ((nci >> 24) & 0xFF);
        data[5] = (byte) ((nci >> 16) & 0xFF);
        data[6] = (byte) ((nci >> 8) & 0xFF);
        data[7] = (byte) (nci & 0xFF);
    }

    public int getMCC() throws Exception {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 8)
            throw new MAPException("Data length must equal 8");

        AsnInputStream ansIS = new AsnInputStream(data);
        String res;
        try {
            res = TbcdString.decodeString(ansIS, 3);
        } catch (IOException e) {
            throw new IOException("IOException when decoding NRCellGlobalId: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Parsing Component Exception when decoding NRCellGlobalId: " + e.getMessage(), e);
        }

        if (res.length() < 5 || res.length() > 6)
            throw new MAPException("Decoded TbcdString must equal 5 or 6");

        String sMcc = res.substring(0, 3);

        return Integer.parseInt(sMcc);
    }

    public int getMNC() throws Exception {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 8)
            throw new MAPException("Data length must equal 8");

        AsnInputStream ansIS = new AsnInputStream(data);
        String res;
        try {
            res = TbcdString.decodeString(ansIS, 3);
        } catch (IOException e) {
            throw new IOException("IOException when decoding NRCellGlobalId: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Parsing Component Exception when decoding NRCellGlobalId: " + e.getMessage(), e);
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

    public long getNCI() throws MAPException {

        if (data == null)
            throw new MAPException("Data must not be empty");
        if (data.length != 8)
            throw new MAPException("Data length must equal 8");

        return ((long) (data[3] & 0x0F) << 32) + ((long) (data[4] & 0xFF) << 24) + ((data[5] & 0xFF) << 16) + ((data[6] & 0xFF) << 8) + (data[7] & 0xFF);
    }

    @Override
    public String toString() {

        int mcc = 0;
        int mnc = 0;
        long nci = 0;
        boolean correctData = false;

        try {
            try {
                mcc = this.getMCC();
                mnc = this.getMNC();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            nci = this.getNCI();
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
            sb.append(", "+NCI+"=");
            sb.append(nci);
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
    protected static final XMLFormat<NRCellGlobalIdImpl> NR_CELL_GLOBAL_ID_XML = new XMLFormat<>(NRCellGlobalIdImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, NRCellGlobalIdImpl nrCgi) throws XMLStreamException {
            int mcc = xml.getAttribute(MCC, DEFAULT_INT_VALUE);
            int mnc = xml.getAttribute(MNC, DEFAULT_INT_VALUE);
            int nci = xml.getAttribute(NCI, DEFAULT_INT_VALUE);

            try {
                nrCgi.setData(mcc, mnc, nci);
            } catch (MAPException e) {
                throw new XMLStreamException("MAPException when deserializing NRCellGlobalIdImpl", e);
            }
        }

        @Override
        public void write(NRCellGlobalIdImpl nrCgi, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            try {
                xml.setAttribute(MCC, nrCgi.getMCC());
                xml.setAttribute(MNC, nrCgi.getMNC());
                xml.setAttribute(NCI, nrCgi.getNCI());
            } catch (MAPException e) {
                throw new XMLStreamException("MAPException when serializing NRCellGlobalIdImpl", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}
