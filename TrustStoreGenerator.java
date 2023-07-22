import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class TrustStoreGenerator {
    private static final String KEYSTORE_PATH = "C:\\Users\\user\\Documents\\RMI Advanced Auction System\\keystore.jks";
    private static final String KEYSTORE_PASSWORD = "Group2";
    private static final String TRUSTSTORE_PATH = "C:\\Users\\user\\Documents\\RMI Advanced Auction System\\truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "Group2";
    private static final String CERTIFICATE_PATH = "C:\\Users\\user\\Documents\\RMI Advanced Auction System\\exported_certificate.cer";

    public static void main(String[] args) {
        try {
            // Load the keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(KEYSTORE_PATH);
            char[] password = KEYSTORE_PASSWORD.toCharArray();
            keyStore.load(fis, password);
            fis.close();

            // Load the certificate from the exported file
            FileInputStream certFis = new FileInputStream(CERTIFICATE_PATH);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate certificate = cf.generateCertificate(certFis);
            certFis.close();

            // Create a new truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, null);

            // Add the certificate to the truststore
            trustStore.setCertificateEntry("alias", certificate);

            // Save the truststore to a file
            FileOutputStream fos = new FileOutputStream(TRUSTSTORE_PATH);
            char[] truststorePassword = TRUSTSTORE_PASSWORD.toCharArray();
            trustStore.store(fos, truststorePassword);
            fos.close();

            System.out.println("Truststore generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}