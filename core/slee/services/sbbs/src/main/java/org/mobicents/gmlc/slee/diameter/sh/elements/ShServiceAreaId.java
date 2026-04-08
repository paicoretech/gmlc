package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShServiceAreaId {

    private static final Logger logger = Logger.getLogger(ShServiceAreaId.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";
    private static final String LAC = "lac";
    private static final String SAC = "sac";

    private String serviceAreaIdStr;
    private CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength;

    public ShServiceAreaId(String serviceAreaIdStr, CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
        this.serviceAreaIdStr = serviceAreaIdStr;
        this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
    }

    public ShServiceAreaId(String serviceAreaIdStr) {
        this.serviceAreaIdStr = serviceAreaIdStr;
    }

    public ShServiceAreaId(CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
        this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
    }

    public ShServiceAreaId() {
    }

    public String getServiceAreaIdStr() {
        return serviceAreaIdStr;
    }

    public void setServiceAreaIdStr(String serviceAreaIdStr) {
        if (serviceAreaIdStr != null) {
            byte[] saiBytes = getCellGlobalIdOrServiceAreaIdFixedLengthBytes(serviceAreaIdStr);
            this.cellGlobalIdOrServiceAreaIdFixedLength = decodeCellGlobalIdOrServiceAreaIdFixedLengthBytes(saiBytes);
            this.serviceAreaIdStr = this.cellGlobalIdOrServiceAreaIdFixedLength.toString();
        }
    }

    public CellGlobalIdOrServiceAreaIdFixedLength getCellGlobalIdOrServiceAreaIdFixedLength() {
        return cellGlobalIdOrServiceAreaIdFixedLength;
    }

    public void setCellGlobalIdOrServiceAreaIdFixedLength(CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
        this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
    }

    public byte[] getCellGlobalIdOrServiceAreaIdFixedLengthBytes(String saiInfo) {
        return Base64.getDecoder().decode(saiInfo);
    }

    public CellGlobalIdOrServiceAreaIdFixedLength decodeCellGlobalIdOrServiceAreaIdFixedLengthBytes(byte[] cgiBytes) {
        return new CellGlobalIdOrServiceAreaIdFixedLengthImpl(cgiBytes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SAI");
        sb.append(" [");

        if (cellGlobalIdOrServiceAreaIdFixedLength != null) {

            try {
                sb.append(MCC+"=");
                sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getMCC());

                sb.append(", "+MNC+"=");
                sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getMNC());

                sb.append(", "+LAC+"=");
                sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getLac());

                sb.append(", "+SAC+"=");
                sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode());

            } catch (MAPException e) {
                logger.error(e.getMessage());
            }

        }

        sb.append("]");

        return sb.toString();
    }

}
