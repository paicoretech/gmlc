package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShCellGlobalId {

  private static final Logger logger = Logger.getLogger(ShCellGlobalId.class.getName());

  private static final String MCC = "mcc";
  private static final String MNC = "mnc";
  private static final String LAC = "lac";
  private static final String CELL_ID = "ci";

  private String cellGlobalIdStr;
  private CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength;

  public ShCellGlobalId(String cellGlobalIdStr, CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
    this.cellGlobalIdStr = cellGlobalIdStr;
    this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
  }

  public ShCellGlobalId(String cellGlobalIdStr) {
    this.cellGlobalIdStr = cellGlobalIdStr;
  }

  public ShCellGlobalId(CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
    this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
  }

  public ShCellGlobalId() {
  }

  public CellGlobalIdOrServiceAreaIdFixedLength getCellGlobalIdOrServiceAreaIdFixedLength() {
    return cellGlobalIdOrServiceAreaIdFixedLength;
  }

  public void setCellGlobalIdOrServiceAreaIdFixedLength(CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
    this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
  }

  public String getCellGlobalIdStr() {
    return cellGlobalIdStr;
  }

  public void setCellGlobalIdStr(String cellGlobalIdStr) {
    if (cellGlobalIdStr != null) {
      byte[] cgiBytes = getCellGlobalIdOrServiceAreaIdFixedLengthBytes(cellGlobalIdStr);
      this.cellGlobalIdOrServiceAreaIdFixedLength = decodeCellGlobalIdOrServiceAreaIdFixedLengthBytes(cgiBytes);
      this.cellGlobalIdStr = this.cellGlobalIdOrServiceAreaIdFixedLength.toString();
    }
  }

  public byte[] getCellGlobalIdOrServiceAreaIdFixedLengthBytes(String cgiInfo) {
    return Base64.getDecoder().decode(cgiInfo);
  }

  public CellGlobalIdOrServiceAreaIdFixedLength decodeCellGlobalIdOrServiceAreaIdFixedLengthBytes(byte[] cgiBytes) {
    return new CellGlobalIdOrServiceAreaIdFixedLengthImpl(cgiBytes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("CGI");
    sb.append(" [");

    if (cellGlobalIdOrServiceAreaIdFixedLength != null) {

      try {
        sb.append(MCC+"=");
        sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getMCC());

        sb.append(", "+MNC+"=");
        sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getMNC());

        sb.append(", "+LAC+"=");
        sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getLac());

        sb.append(", "+CELL_ID+"=");
        sb.append(this.cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode());

      } catch (MAPException e) {
        logger.error(e.getMessage());
      }

    }

    sb.append("]");

    return sb.toString();
  }

}
