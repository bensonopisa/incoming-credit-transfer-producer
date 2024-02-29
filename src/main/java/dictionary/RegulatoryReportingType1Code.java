
package dictionary;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegulatoryReportingType1Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="RegulatoryReportingType1Code"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CRED"/&gt;
 *     &lt;enumeration value="DEBT"/&gt;
 *     &lt;enumeration value="BOTH"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "RegulatoryReportingType1Code", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07")
@XmlEnum
public enum RegulatoryReportingType1Code {

    CRED,
    DEBT,
    BOTH;

    public String value() {
        return name();
    }

    public static RegulatoryReportingType1Code fromValue(String v) {
        return valueOf(v);
    }

}
