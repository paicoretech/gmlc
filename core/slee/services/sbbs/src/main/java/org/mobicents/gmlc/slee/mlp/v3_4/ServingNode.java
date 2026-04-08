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
    "neid",
    "diameterIdentity",
    "sgsnId",
    "amfAddress"
})
@XmlRootElement(name = "serving_node")
public class ServingNode {

    protected Neid neid;
    @XmlElement(name = "diameter_identity")
    protected DiameterIdentity diameterIdentity;
    @XmlElement(name = "sgsnid")
    protected Sgsnid sgsnId;
    @XmlElement(name = "amf_address")
    protected String amfAddress;

    /**
     * Gets the value of the neid property.
     *
     * @return
     *     possible object is
     *     {@link Neid }
     *
     */
    public Neid getNeid() {
        return neid;
    }

    /**
     * Sets the value of the neid property.
     *
     * @param value
     *     allowed object is
     *     {@link Neid }
     *
     */
    public void setNeid(Neid value) {
        this.neid = value;
    }

    /**
     * Gets the value of the diameterIdentity property.
     *
     * @return
     *     possible object is
     *     {@link DiameterIdentity }
     *
     */
    public DiameterIdentity getDiameterIdentity() {
        return diameterIdentity;
    }

    /**
     * Sets the value of the diameterIdentity property.
     *
     * @param value
     *     allowed object is
     *     {@link DiameterIdentity }
     *
     */
    public void setDiameterIdentity(DiameterIdentity value) {
        this.diameterIdentity = value;
    }

    /**
     * Gets the value of the sgsnId property.
     *
     * @return
     *     possible object is
     *     {@link Sgsnid }
     *
     */
    public Sgsnid getSgsnId() {
        return sgsnId;
    }

    /**
     * Sets the value of the sgsnId property.
     *
     * @param value
     *     allowed object is
     *     {@link Sgsnid }
     *
     */
    public void setSgsnId(Sgsnid value) {
        this.sgsnId = value;
    }

    /**
     * Gets the value of the amfAddress property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAmfAddress() {
        return amfAddress;
    }

    /**
     * Sets the value of the amfAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAmfAddress(String value) {
        this.amfAddress = value;
    }
}
