package ke.co.pesalink.papss.incoming.credittransferproducerservice.util;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.exceptions.XmlParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class XmlUtils {
    private final DocumentBuilderFactory documentBuilderFactory;


    private final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    public XmlUtils() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }

    public Document parseXml(String xml) throws XmlParseException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xml));

        Document document = documentBuilder.parse(inputSource);

        logger.debug("Loaded document {}", document);
        return document;
    }

    private KeyStore loadKeyStore(String keyStoreName, String password) throws KeyStoreException, IOException {
        KeyStore.PasswordProtection keyStorePassword = new KeyStore.PasswordProtection(password.toCharArray());
        File keyStoreFile = new ClassPathResource(keyStoreName).getFile();

        if (!keyStoreFile.exists()) {
            throw new KeyStoreException("Failed to locate keystore file at path "+ keyStoreFile.getCanonicalPath());
        }
        KeyStore.Builder keyStoreBuilder = KeyStore.Builder.newInstance("pkcs12", null, keyStoreFile , keyStorePassword);

        return keyStoreBuilder.getKeyStore();
    }

    public boolean validateXMLSignature(String body) throws XmlParseException, MarshalException, XMLSignatureException, KeyStoreException, IOException {

        final KeyStore keyStore = loadKeyStore("client.p12", "secure");

        Document document;
        try {
            document = this.parseXml(body);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new XmlParseException("Failed to parse xml document. Reason ::: " + ex.getLocalizedMessage());
        }

        NodeList nl = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new XmlParseException("Cannot find Signature element within the response");
        }

        // create a dom validation context with a custom key value selector
        // passing in the first element inside the node list signature body
        DOMValidateContext context = new DOMValidateContext(new KeyValueSelector(keyStore), nl.item(0));

        context.setProperty("org.jcp.xml.dsig.secureValidation", Boolean.TRUE);

        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

        XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(context);

        boolean coreValidation = xmlSignature.validate(context);

        logger.debug("was signature validation successful: {}" , coreValidation);

        return coreValidation;
    }
}
