# GMLC – Gateway Mobile Location Center (Open Source Version)
============

##  Introduction to PAiCore Technologies GMLC

The Gateway Mobile Location Centre empowers telecom operators to offer efficient and highly accurate **Location-Based Services (LBS)** for mobile subscribers roaming across either legacy GSM or UMTS/HSPA+ networks, or Next Generation Networks like LTE/LTE-Advanced/LTE-Advanced Pro and 5G NR, without relying on smartphone apps or an internet connection. 

The GMLC is the first node an external LBS client accesses in a mobile core network. The GMLC may request routing and/or location information from the HLR/HSS (Home Location Register/Home Subscriber Server) or MSC/VLR (Mobile Switching Centre/Visitor Location Register) or SGSN (Serving GPRS Support Node) or MME (Mobility Management Entity). Furthermore, in conjunction with a **Serving Mobile Location Center (SMLC)** or **Evolved-SMLC**, it may receive mobile subscriber and/or IoT devices periodic or event based deferred location reports. The **GMLC** serves as the secure gateway between external applications and mobile network location data, while the **SMLC** and/or **E-SMLC** carry out user equipment location estimates using radio measurements from nearby base stations.

These capabilities are essential for a wide range of scenarios, including:
- Emergency location tracking
- Context-aware targeted messaging
- Network-based analytics
- Regulatory compliance and public safety services 
- Lawful interception

##  Installation

### Prerequisites

- Java 11 (OpenJDK recommended)
- Maven 3.x
- Linux-based environment
- (Optional) MySQL or other supported DB if needed

### Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/paicbd/gmlc.git
cd gmlc

# 2. Compile and build the project
mvn clean install
cd release-wildfly
ant

# 3. Run the application
cd $JBOSS_HOME/Extended-GMLC-6.0.1-SNAPSHOT/wildfly-24.0.1.Final
./standalone.sh
```

> Make sure the `JAVA_HOME` environment variable is properly set before building the project.



This open source release is provided as part of PAiCore Technologies' commitment to open innovation.  
Community contributions are welcome via pull requests and issue discussions.

For commercial support, enterprise-grade deployments or consulting services, contact us at:  
🌐 https://paicore.tech

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See the [LICENSE](LICENSE) file for more details.

