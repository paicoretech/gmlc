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
    "horizontalSpeed",
    "bearing",
    "verticalSpeed",
    "uncertaintyHorizontalSpeed",
    "uncertaintyVerticalSpeed",
    "velocityType"
})
@XmlRootElement(name = "velocity_estimate")
public class VelocityEstimate {

    @XmlElement(name = "horizontal_speed")
    protected String horizontalSpeed;
    @XmlElement(name = "bearing")
    protected String bearing;
    @XmlElement(name = "vertical_speed")
    protected String verticalSpeed;
    @XmlElement(name = "uncertainty_horizontal_speed")
    protected String uncertaintyHorizontalSpeed;
    @XmlElement(name = "uncertainty_vertical_speed")
    protected String uncertaintyVerticalSpeed;
    @XmlElement(name = "velocity_type")
    protected String velocityType;

    /**
     * Gets the value of the horizontalSpeed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHorizontalSpeed() {
        return horizontalSpeed;
    }

    /**
     * Sets the value of the horizontalSpeed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHorizontalSpeed(String value) {
        this.horizontalSpeed = value;
    }

    /**
     * Gets the value of the bearing property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBearing() {
        return bearing;
    }

    /**
     * Sets the value of the bearing property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBearing(String value) {
        this.bearing = value;
    }

    /**
     * Gets the value of the verticalSpeed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVerticalSpeed() {
        return verticalSpeed;
    }

    /**
     * Sets the value of the verticalSpeed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerticalSpeed(String value) {
        this.verticalSpeed = value;
    }

    /**
     * Gets the value of the uncertaintyHorizontalSpeed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUncertaintyHorizontalSpeed() {
        return uncertaintyHorizontalSpeed;
    }

    /**
     * Sets the value of the uncertaintyHorizontalSpeed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUncertaintyHorizontalSpeed(String value) {
        this.uncertaintyHorizontalSpeed = value;
    }

    /**
     * Gets the value of the uncertaintyVerticalSpeed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUncertaintyVerticalSpeed() {
        return uncertaintyVerticalSpeed;
    }

    /**
     * Sets the value of the uncertaintyVerticalSpeed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUncertaintyVerticalSpeed(String value) {
        this.uncertaintyVerticalSpeed = value;
    }

    /**
     * Gets the value of the velocityType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVelocityType() {
        return velocityType;
    }

    /**
     * Sets the value of the velocityType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVelocityType(String value) {
        this.velocityType = value;
    }
}