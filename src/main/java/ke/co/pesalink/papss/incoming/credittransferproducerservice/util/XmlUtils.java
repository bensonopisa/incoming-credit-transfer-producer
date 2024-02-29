package ke.co.pesalink.papss.incoming.credittransferproducerservice.util;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.exceptions.XmlParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class XmlUtils {
    private final DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;

    private final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    public XmlUtils() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    public Object parseXml(String xml) throws XmlParseException  {

        Document document;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));

            document = documentBuilder.parse(inputSource);
            logger.debug("Loaded document {}", document);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XmlParseException("Error encountered when parsing xml", e.getCause());
        }

        return document;
    }
}
