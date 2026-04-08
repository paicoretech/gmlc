package org.mobicents.gmlc.slee.mlp.v3_4;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "msidList"
})
@XmlRootElement(name = "msids")
public class MsisdsExt {

    @XmlElementRefs({
        @XmlElementRef(name = "msid", type = Msid.class)
    })
    protected List<Msid> msidList;

    /**
     * Gets the value of the msidList property.
     *
     * @return
     *     possible object is
     *     {@link List<Msid> }
     *
     */
    public List<Msid> getMsidList() {
        return msidList;
    }

    /**
     * Sets the value of the msidList property.
     *
     * @param value
     *     allowed object is
     *     {@link Msids }
     *
     */
    public void setMsidList(List<Msid> value) {
        this.msidList = value;
    }
}
