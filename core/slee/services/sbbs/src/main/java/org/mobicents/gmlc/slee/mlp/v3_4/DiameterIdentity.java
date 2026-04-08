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
    "sgsnName",
    "sgsnRealm",
    "mmeName",
    "mmeRealm"
})
@XmlRootElement(name = "diameter_identity")
public class DiameterIdentity {

    @XmlElement(name = "sgsn_name")
    protected String sgsnName;
    @XmlElement(name = "sgsn_realm")
    protected String sgsnRealm;
    @XmlElement(name = "mme_name")
    protected String mmeName;
    @XmlElement(name = "mme_realm")
    protected String mmeRealm;

    /**
     * Gets the value of the sgsnName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSgsnName() {
        return sgsnName;
    }

    /**
     * Sets the value of the sgsnName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSgsnName(String value) {
        this.sgsnName = value;
    }

    /**
     * Gets the value of the sgsnRealm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSgsnRealm() {
        return sgsnRealm;
    }

    /**
     * Sets the value of the sgsnRealm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSgsnRealm(String value) {
        this.sgsnRealm = value;
    }

    /**
     * Gets the value of the mmeName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMmeName() {
        return mmeName;
    }

    /**
     * Sets the value of the mmeName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMmeName(String value) {
        this.mmeName = value;
    }

    /**
     * Gets the value of the mmeRealm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMmeRealm() {
        return mmeRealm;
    }

    /**
     * Sets the value of the mmeRealm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMmeRealm(String value) {
        this.mmeRealm = value;
    }
}
