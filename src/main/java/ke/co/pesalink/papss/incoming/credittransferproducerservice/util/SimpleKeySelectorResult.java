package ke.co.pesalink.papss.incoming.credittransferproducerservice.util;

import javax.xml.crypto.KeySelectorResult;
import java.security.Key;

public class SimpleKeySelectorResult implements KeySelectorResult {
    private final Key key;
    public SimpleKeySelectorResult(Key key) {
        this.key = key;
    }
    @Override
    public Key getKey() {
        return key;
    }
}
