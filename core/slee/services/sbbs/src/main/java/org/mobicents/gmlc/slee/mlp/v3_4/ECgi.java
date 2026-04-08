package org.mobicents.gmlc.slee.mlp.v3_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mcc",
    "mnc",
    "eci",
    "enbid",
    "ci"
})
@XmlRootElement(name = "ecgi")
public class ECgi {

    @XmlElement(required = true)
    protected String mcc;
    @XmlElement(required = true)
    protected String mnc;
    protected String enbid;
    @XmlElement(required = true)
    protected String eci;
    protected String ci;

    /**
     * Gets the value of the mcc property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMcc() {
        return mcc;
    }

    /**
     * Sets the value of the mcc property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMcc(String value) {
        this.mcc = value;
    }

    /**
     * Gets the value of the mnc property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMnc() {
        return mnc;
    }

    /**
     * Sets the value of the mnc property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMnc(String value) {
        this.mnc = value;
    }

    /**
     * Gets the value of the eci property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEci() {
        return eci;
    }

    /**
     * Sets the value of the eci property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEci(String value) {
        this.eci = value;
    }

    /**
     * Gets the value of the enbid property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnbid() {
        return enbid;
    }

    /**
     * Sets the value of the enbid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnbid(String value) {
        this.enbid = value;
    }

    /**
     * Gets the value of the ci property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCi() {
        return ci;
    }

    /**
     * Sets the value of the enbid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCi(String value) {
        this.ci = value;
    }
}
