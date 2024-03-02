package ke.co.pesalink.papss.incoming.credittransferproducerservice.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

public class KeyValueSelector extends KeySelector {
    private final KeyStore keyStore;
    private final Logger logger = LoggerFactory.getLogger(KeyValueSelector.class);

    public KeyValueSelector(KeyStore keyStore){
        if (keyStore == null) {
            throw new NullPointerException("Keystore supplied is null");
        }
        this.keyStore = keyStore;
    }
    @Override
    public KeySelectorResult  select(KeyInfo keyInfo, KeySelector.Purpose purpose,AlgorithmMethod method, XMLCryptoContext context)
            throws KeySelectorException {

        if (keyInfo == null) {
            throw new KeySelectorException("Null KeyInfo object!");
        }

        SignatureMethod sm = (SignatureMethod) method;

        List<XMLStructure> list = keyInfo.getContent();


        for (XMLStructure xmlStructure : list) {
            String sub = null;
            X509IssuerSerial issuerSerial = null;

            if (xmlStructure instanceof X509Data x509Data) {
                Iterator<?> iterator = x509Data.getContent().iterator();

                while(iterator.hasNext()) {
                    if (iterator.next() instanceof String subjectName) {
                        sub = subjectName;
                    }
                    if (iterator.next() instanceof X509IssuerSerial issuer) {
                        issuerSerial = issuer;
                    }
                }

                try {
                    Iterator<String> aliases = this.keyStore.aliases().asIterator();
                    X509Certificate validCert = null;

                    while(aliases.hasNext()) {
                        X509Certificate cert = (X509Certificate) this.keyStore.getCertificate(aliases.next());

                        if (!algEqual(cert.getSigAlgName(),sm.getAlgorithm())){
                            logger.warn("Certificate algorithm and the signature algorithm dont match");
                            throw new KeySelectorException("Algorithm mismatch between certificate and signature algorithm");
                        }

                        if (issuerSerial != null && !serialEqual(cert.getSerialNumber(),issuerSerial.getSerialNumber())){
                            logger.warn("Certificate and signature serial do not match");
                            throw new KeySelectorException("Serial number mismatch between certificate and signature serial numbers");
                        }

                        if (issuerSerial != null && compare(cert.getIssuerX500Principal(), new X500Principal(issuerSerial.getIssuerName()))){
                            logger.warn("Certificate and signature issuer names do not match");
                            throw new KeySelectorException("Issuer name mismatch between certificate and signature issuer name");
                        }


                        if (sub != null && compare(cert.getSubjectX500Principal(),new X500Principal(sub))){
                            logger.warn("Certificate and signature subject names do not match");
                            throw new KeySelectorException("Subject name mismatch between certificate and signature subject name");
                        }


                        validCert = cert;
                    }
                    if (validCert != null) {
                        return new SimpleKeySelectorResult(validCert.getPublicKey());
                    }
                }catch(KeyStoreException kse) {
                    throw new KeySelectorException(kse.getMessage());
                }
            }
        }
        throw new KeySelectorException("No KeyValue element found!");
    }

    // compare the two algorithms
    private boolean algEqual(String alg1, String alg2) {
        if (alg1 == null || alg2 == null) {
            return false;
        }
        return StringUtils.equals(alg1,alg2);
    }

    // compare the two serial numbers
    private boolean serialEqual(BigInteger serial1, BigInteger serial2) {
       if (serial1 == null || serial2 == null ){
           return false;
       }

       return serial1.compareTo(serial2) == 0;
    }

    private boolean compare(X500Principal x500Principal1, X500Principal x500Principal2) {
        if (x500Principal1 == null || x500Principal2 == null) {
            return false;
        }

        return x500Principal1.equals(x500Principal2);
    }
}
