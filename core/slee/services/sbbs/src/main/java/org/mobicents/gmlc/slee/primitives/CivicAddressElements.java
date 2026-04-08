package org.mobicents.gmlc.slee.primitives;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@XmlRootElement(name = "civicAddress")
public class CivicAddressElements {

    /**
     * <xs:element name="civicAddress" type="ca:civicAddress"/>
     *      <xs:complexType name="civicAddress">
     *        <xs:sequence>
     *          <xs:element name="country" type="ca:iso3166a2" minOccurs="0"/>
     *          <xs:element name="A1" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="A2" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="A3" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="A4" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="A5" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="A6" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="PRM" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="PRD" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="RD" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="STS" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="POD" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="POM" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="RDSEC" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="RDBR" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="RDSUBBR" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="HNO" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="HNS" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="LMK" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="LOC" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="FLR" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="NAM" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="PC" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="BLD" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="UNIT" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="ROOM" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="SEAT" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="PLC" type="xs:token" minOccurs="0"/>
     *          <xs:element name="PCN" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="POBOX" type="ca:caType" minOccurs="0"/>
     *          <xs:element name="ADDCODE" type="ca:caType" minOccurs="0"/>
     *          <xs:any namespace="##other" processContents="lax"
     *                  minOccurs="0" maxOccurs="unbounded"/>
     *        </xs:sequence>
     *        <xs:anyAttribute namespace="##any" processContents="lax"/>
     *      </xs:complexType>
     *    </xs:schema>
     *  Civic Address Extensions
     *  XML Schema
     *  <?xml version="1.0"?>
     *  <xs:schema
     *    targetNamespace="urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr:ext"
     *    xmlns:ca="urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr"
     *    xmlns:xs="<a href="http://www.w3.org/2001/XMLSchema">...</a>"
     *    xmlns:cae="urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr:ext"
     *    xmlns:xml="http://www.w3.org/XML/1998/namespace"
     *    elementFormDefault="qualified" attributeFormDefault="unqualified">
     *    <xs:import namespace="urn:ietf:params:xml:pidf:geopriv10:civicAddr"/>
     *    <!-- Post Number -->
     *    <xs:element name="PN" type="ca:caType"/>
     *    <!-- Milepost -->
     *    <xs:element name="MP" type="ca:caType"/>
     *    <!-- Street Type Prefix -->
     *    <xs:element name="STP" type="ca:caType"/>
     *    <!-- House Number Prefix -->
     *    <xs:element name="HNP" type="ca:caType"/>
     *  </xs:schema>
     *
     */

    private String country, a1, a2, a3, a4, a5, a6;
    private String prm, prd, rd, sts, pod, pom, rdsec, rdbr, rdsubbr, hno, hns, lmk, loc, flr, nam, pc, bld, unit, room, seat, plc, pcn, pobox, addcode;
    private String pn, mp, stp, hnp;

    public CivicAddressElements() {
    }

    public String getCountry() {
        return country;
    }

    @XmlElement (name = "country")
    public void setCountry(String country) {
        this.country = country;
    }

    public String getA1() {
        return a1;
    }

    @XmlElement (name = "A1")
    public void setA1(String a1) {
        this.a1 = a1;
    }

    public String getA2() {
        return a2;
    }

    @XmlElement (name = "A2")
    public void setA2(String a2) {
        this.a2 = a2;
    }

    public String getA3() {
        return a3;
    }

    @XmlElement (name = "A3")
    public void setA3(String a3) {
        this.a3 = a3;
    }

    public String getA4() {
        return a4;
    }

    @XmlElement (name = "A4")
    public void setA4(String a4) {
        this.a4 = a4;
    }

    public String getA5() {
        return a5;
    }

    @XmlElement (name = "A5")
    public void setA5(String a5) {
        this.a5 = a5;
    }

    public String getA6() {
        return a6;
    }

    @XmlElement (name = "A6")
    public void setA6(String a6) {
        this.a6 = a6;
    }

    public String getPrm() {
        return prm;
    }

    @XmlElement (name = "PRM")
    public void setPrm(String prm) {
        this.prm = prm;
    }

    public String getPrd() {
        return prd;
    }

    @XmlElement (name = "PRD")
    public void setPrd(String prd) {
        this.prd = prd;
    }

    public String getRd() {
        return rd;
    }

    @XmlElement (name = "RD")
    public void setRd(String rd) {
        this.rd = rd;
    }

    public String getSts() {
        return sts;
    }

    @XmlElement (name = "STS")
    public void setSts(String sts) {
        this.sts = sts;
    }

    public String getPod() {
        return pod;
    }

    @XmlElement (name = "POD")
    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getPom() {
        return pom;
    }

    @XmlElement (name = "POM")
    public void setPom(String pom) {
        this.pom = pom;
    }

    public String getRdsec() {
        return rdsec;
    }

    @XmlElement (name = "RDSEC")
    public void setRdsec(String rdsec) {
        this.rdsec = rdsec;
    }

    public String getRdbr() {
        return rdbr;
    }

    @XmlElement (name = "RDBR")
    public void setRdbr(String rdbr) {
        this.rdbr = rdbr;
    }

    public String getRdsubbr() {
        return rdsubbr;
    }

    @XmlElement (name = "RDSUBBR")
    public void setRdsubbr(String rdsubbr) {
        this.rdsubbr = rdsubbr;
    }

    public String getHno() {
        return hno;
    }

    @XmlElement (name = "HNO")
    public void setHno(String hno) {
        this.hno = hno;
    }

    public String getHns() {
        return hns;
    }

    @XmlElement (name = "HNS")
    public void setHns(String hns) {
        this.hns = hns;
    }

    public String getLmk() {
        return lmk;
    }

    @XmlElement (name = "LMK")
    public void setLmk(String lmk) {
        this.lmk = lmk;
    }

    public String getLoc() {
        return loc;
    }

    @XmlElement (name = "LOC")
    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getFlr() {
        return flr;
    }

    @XmlElement (name = "FLR")
    public void setFlr(String flr) {
        this.flr = flr;
    }

    public String getNam() {
        return nam;
    }

    @XmlElement (name = "NAM")
    public void setNam(String nam) {
        this.nam = nam;
    }

    public String getPc() {
        return pc;
    }

    @XmlElement (name = "PC")
    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getBld() {
        return bld;
    }

    @XmlElement (name = "BLD")
    public void setBld(String bld) {
        this.bld = bld;
    }

    public String getUnit() {
        return unit;
    }

    @XmlElement (name = "UNIT")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRoom() {
        return room;
    }

    @XmlElement (name = "ROOM")
    public void setRoom(String room) {
        this.room = room;
    }

    public String getSeat() {
        return seat;
    }

    @XmlElement (name = "SEAT")
    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getPlc() {
        return plc;
    }

    @XmlElement (name = "PLC")
    public void setPlc(String plc) {
        this.plc = plc;
    }

    public String getPcn() {
        return pcn;
    }

    @XmlElement (name = "PCN")
    public void setPcn(String pcn) {
        this.pcn = pcn;
    }

    public String getPobox() {
        return pobox;
    }

    @XmlElement (name = "POBOX")
    public void setPobox(String pobox) {
        this.pobox = pobox;
    }

    public String getAddcode() {
        return addcode;
    }

    @XmlElement (name = "ADDCODE")
    public void setAddcode(String addcode) {
        this.addcode = addcode;
    }

    public String getPn() {
        return pn;
    }

    @XmlElement (name = "PN", namespace="cae")
    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getMp() {
        return mp;
    }

    @XmlElement (name = "MP", namespace="cae")
    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getStp() {
        return stp;
    }

    @XmlElement (name = "STP", namespace="cae")
    public void setStp(String stp) {
        this.stp = stp;
    }

    public String getHnp() {
        return hnp;
    }

    @XmlElement (name = "HNP", namespace="cae")
    public void setHnp(String hnp) {
        this.hnp = hnp;
    }

    @Override
    public String toString() {
        return "UtranCivicAddress{" +
                "country='" + country + '\'' +
                ", a1='" + a1 + '\'' +
                ", a2='" + a2 + '\'' +
                ", a3='" + a3 + '\'' +
                ", a4='" + a4 + '\'' +
                ", a5='" + a5 + '\'' +
                ", a6='" + a6 + '\'' +
                ", prm='" + prm + '\'' +
                ", prd='" + prd + '\'' +
                ", rd='" + rd + '\'' +
                ", sts='" + sts + '\'' +
                ", pod='" + pod + '\'' +
                ", pom='" + pom + '\'' +
                ", rdsec='" + rdsec + '\'' +
                ", rdbr='" + rdbr + '\'' +
                ", rdsubbr='" + rdsubbr + '\'' +
                ", hno='" + hno + '\'' +
                ", hns='" + hns + '\'' +
                ", lmk='" + lmk + '\'' +
                ", loc='" + loc + '\'' +
                ", flr='" + flr + '\'' +
                ", nam='" + nam + '\'' +
                ", pc='" + pc + '\'' +
                ", bld='" + bld + '\'' +
                ", unit='" + unit + '\'' +
                ", room='" + room + '\'' +
                ", seat='" + seat + '\'' +
                ", plc='" + plc + '\'' +
                ", pcn='" + pcn + '\'' +
                ", pobox='" + pobox + '\'' +
                ", addcode='" + addcode + '\'' +
                ", pn='" + pn + '\'' +
                ", mp='" + mp + '\'' +
                ", stp='" + stp + '\'' +
                ", hnp='" + hnp + '\'' +
                '}';
    }
}
