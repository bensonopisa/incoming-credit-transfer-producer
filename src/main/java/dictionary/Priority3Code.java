
package dictionary;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Priority3Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="Priority3Code"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="URGT"/&gt;
 *     &lt;enumeration value="HIGH"/&gt;
 *     &lt;enumeration value="NORM"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Priority3Code", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07")
@XmlEnum
public enum Priority3Code {

    URGT,
    HIGH,
    NORM;

    public String value() {
        return name();
    }

    public static Priority3Code fromValue(String v) {
        return valueOf(v);
    }

}
