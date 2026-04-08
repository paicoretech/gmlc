package org.mobicents.gmlc.slee.mlp.v3_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "msids",
    "pos",
    "velocityEstimate",
    "rai",
    "cgi",
    "sai",
    "ucgi",
    "tai",
    "ecgi",
    "nrTai",
    "nrCgi",
    "neid",
    "servingNode"
})
@XmlRootElement(name = "extension_params")
public class ExtensionParams {

    protected MsisdsExt msids;
    protected List<Pos> pos;
    @XmlElement(name = "velocity_estimate")
    protected VelocityEstimate velocityEstimate;
    @XmlElement(name = "rai")
    protected Rai rai;
    protected Cgi cgi;
    protected Sai sai;
    @XmlElement(name = "ucgi")
    protected UtranCgi ucgi;
    @XmlElement(name = "tai")
    protected Tai tai;
    @XmlElement(name = "ecgi")
    protected ECgi ecgi;
    @XmlElement(name = "nr_tai")
    protected NrTai nrTai;
    @XmlElement(name = "nr_cgi")
    protected NrCgi nrCgi;
    protected Neid neid;
    @XmlElement(name = "serving_node")
    protected ServingNode servingNode;

    /**
     * Gets the value of the msids property.
     *
     * @return
     *     possible object is
     *     {@link List<Msid> }
     *
     */
    public MsisdsExt getMsidsExt() {
        return msids;
    }

    /**
     * Sets the value of the msids property.
     *
     * @param value
     *     allowed object is
     *     {@link Msids }
     *
     */
    public void setMsidsExt(MsisdsExt value) {
        this.msids = value;
    }

    /**
     * Gets the value of the pos property.
     * This property is a customization for MAP PSL and Diameter SLg PLR
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pos property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPos().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pos }
     *
     *
     */
    public List<Pos> getPos() {
        if (pos == null) {
            pos = new ArrayList<>();
        }
        return this.pos;
    }

    /**
     * Gets the value of the velocityEstimate property.
     *
     * @return
     *     possible object is
     *     {@link Rai }
     *
     */
    public VelocityEstimate getVelocityEstimate() {
        return velocityEstimate;
    }

    /**
     * Sets the value of the velocityEstimate property.
     *
     * @param value
     *     allowed object is
     *     {@link Rai }
     *
     */
    public void setVelocityEstimate(VelocityEstimate value) {
        this.velocityEstimate = value;
    }

    /**
     * Gets the value of the rai property.
     *
     * @return
     *     possible object is
     *     {@link Rai }
     *
     */
    public Rai getRai() {
        return rai;
    }

    /**
     * Sets the value of the rai property.
     *
     * @param value
     *     allowed object is
     *     {@link Rai }
     *
     */
    public void setRai(Rai value) {
        this.rai = value;
    }

    /**
     * Gets the value of the cgi property.
     *
     * @return
     *     possible object is
     *     {@link Cgi }
     *
     */
    public Cgi getCgi() {
        return cgi;
    }

    /**
     * Sets the value of the cgi property.
     *
     * @param value
     *     allowed object is
     *     {@link Cgi }
     *
     */
    public void setCgi(Cgi value) {
        this.cgi = value;
    }

    /**
     * Gets the value of the sai property.
     *
     * @return
     *     possible object is
     *     {@link Sai }
     *
     */
    public Sai getSai() {
        return sai;
    }

    /**
     * Sets the value of the sai property.
     *
     * @param value
     *     allowed object is
     *     {@link Sai }
     *
     */
    public void setSai(Sai value) {
        this.sai = value;
    }

    /**
     * Gets the value of the ucgi property.
     *
     * @return
     *     possible object is
     *     {@link UtranCgi }
     *
     */
    public UtranCgi getUcgi() {
        return ucgi;
    }

    /**
     * Sets the value of the ucgi property.
     *
     * @param value
     *     allowed object is
     *     {@link UtranCgi }
     *
     */
    public void setUcgi(UtranCgi value) {
        this.ucgi = value;
    }

    /**
     * Gets the value of the tai property.
     *
     * @return
     *     possible object is
     *     {@link Tai }
     *
     */
    public Tai getTai() {
        return tai;
    }

    /**
     * Sets the value of the tai property.
     *
     * @param value
     *     allowed object is
     *     {@link Tai }
     *
     */
    public void setTai(Tai value) {
        this.tai = value;
    }

    public ECgi getEcgi() {
        return ecgi;
    }

    public void setEcgi(ECgi ecgi) {
        this.ecgi = ecgi;
    }

    /**
     * Gets the value of the nrTai property.
     *
     * @return
     *     possible object is
     *     {@link NrTai }
     *
     */
    public NrTai getNrTai() {
        return nrTai;
    }

    /**
     * Sets the value of the nrTai property.
     *
     * @param value
     *     allowed object is
     *     {@link NrTai }
     *
     */
    public void setNrTai(NrTai value) {
        this.nrTai = value;
    }

    /**
     * Gets the value of the nrCgi property.
     *
     * @return
     *     possible object is
     *     {@link NrCgi }
     *
     */
    public NrCgi getNrCgi() {
        return nrCgi;
    }

    /**
     * Sets the value of the nrCgi property.
     *
     * @param value
     *     allowed object is
     *     {@link NrCgi }
     *
     */
    public void setNrCgi(NrCgi value) {
        this.nrCgi = value;
    }

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
     * Gets the value of the servingNode property.
     *
     * @return
     *     possible object is
     *     {@link ServingNode }
     *
     */
    public ServingNode getServingNode() {
        return servingNode;
    }

    /**
     * Sets the value of the servingNode property.
     *
     * @param value
     *     allowed object is
     *     {@link ServingNode }
     *
     */
    public void setServingNode(ServingNode value) {
        this.servingNode = value;
    }
}
