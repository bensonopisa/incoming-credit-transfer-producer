package ke.co.pesalink.papss.incoming.credittransferproducerservice.exceptions;

public class XmlParseException extends  RuntimeException {
    public XmlParseException(String message) {
        super(message);
    }

    public XmlParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
