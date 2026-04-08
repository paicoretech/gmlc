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
    "rac"
})
@XmlRootElement(name = "rai")
public class Rai {

    @XmlElement(required = true)
    protected String mcc;
    @XmlElement(required = true)
    protected String mnc;
    @XmlElement(required = true)
    protected String rac;

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
     * Gets the value of the rac property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRac() {
        return rac;
    }

    /**
     * Sets the value of the rac property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRac(String value) {
        this.rac = value;
    }
}
