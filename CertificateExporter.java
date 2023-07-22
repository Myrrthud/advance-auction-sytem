import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class CertificateExporter {
    private static final String KEYSTORE_PATH = "C:\\Users\\user\\Documents\\RMI Advanced Auction System\\keystore.jks";
    private static final String KEYSTORE_PASSWORD = "Group2";
    private static final String CERTIFICATE_ALIAS = "certificate";
    private static final String EXPORTED_CERT_PATH = "C:\\Users\\user\\Documents\\RMI Advanced Auction System\\exported_certificate.cer";

    public static void main(String[] args) {
        try {
            // Load the keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(KEYSTORE_PATH);
            char[] password = KEYSTORE_PASSWORD.toCharArray();
            keyStore.load(fis, password);
            fis.close();

            // Export the certificate from the keystore
            Certificate certificate = keyStore.getCertificate(CERTIFICATE_ALIAS);
            writeCertificateToFile(certificate, EXPORTED_CERT_PATH);

            System.out.println("Certificate exported successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeCertificateToFile(Certificate certificate, String filePath) throws IOException {
        try (OutputStream os = new FileOutputStream(filePath)) {
            os.write(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
    }
}
