import java.io.*;
import java.nio.charset.StandardCharsets;

public class KeyStoreGenerator {
    private static final String OUTPUT_DIRECTORY = "C:\\Users\\user\\Documents\\RMI Advanced Auction System\\";

    public static void main(String[] args) {
        try {
            // Generate private key using OpenSSL
            String privateKeyCommand = "openssl genpkey -algorithm RSA -out " + OUTPUT_DIRECTORY + "private_key.pem";
            executeCommand(privateKeyCommand);

        //    // Generate public key and self-signed certificate using OpenSSL
            String publicKeyCommand = "openssl x509 -req -in " + OUTPUT_DIRECTORY + "certificate.csr -signkey " + OUTPUT_DIRECTORY + "private_key.pem -out " + OUTPUT_DIRECTORY + "certificate.pem";
            executeCommand(publicKeyCommand);

            // Generate self-signed certificate using OpenSSL
            String certCommand = "openssl x509 -req -in " + OUTPUT_DIRECTORY + "certificate.csr -signkey " + OUTPUT_DIRECTORY + "private_key.pem -out " + OUTPUT_DIRECTORY + "certificate.crt";
            executeCommand(certCommand);

            // Convert private key and certificate to PKCS12 format using OpenSSL
            String pkcs12Command = "openssl pkcs12 -export -in " + OUTPUT_DIRECTORY + "certificate.pem -inkey " + OUTPUT_DIRECTORY + "private_key.pem -out " + OUTPUT_DIRECTORY + "keystore.p12 -name alias -password Group2";
            executeCommand(pkcs12Command);

            // Convert PKCS12 keystore to JKS format using keytool
            String jksCommand = "keytool -importkeystore -srckeystore " + OUTPUT_DIRECTORY + "keystore.p12 -srcstoretype PKCS12 -srcstorepass Group2 -destkeystore " + OUTPUT_DIRECTORY + "keystore.jks -deststoretype JKS -deststorepass Group2 -alias certificate";
            executeCommand(jksCommand);
            

            // Delete temporary files
            // deleteFile(OUTPUT_DIRECTORY + "private_key.pem");
            // deleteFile(OUTPUT_DIRECTORY + "certificate.pem");
            // deleteFile(OUTPUT_DIRECTORY + "keystore.p12");

            System.out.println("Keystore and certificate generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        // Print any command output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();

        // Print any error output
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            System.err.println(errorLine);
        }
        errorReader.close();
    }

    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
