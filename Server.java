/*
DISTRIBUTED SYSTEMS GROUP TWO
 Stephen Sai Schandorf	11005248
Adelaide Anim-Annor	11004892
Nana Ama Bushel	11005089
Mildred Mensah	11361094
Daniel Obed Mensah	11004649
Andrew Bushel	11360976
Benjamin Agyeman	11005266
Richmond Sam Brown	1100492
 Server code for hosting the AuctionSystem
 */

import java.rmi.Naming;	// import naming classes to bind to rmiregistry
import java.rmi.RemoteException;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Server {

    public Server(int port) {
        
        try {

            // Load the certificate from the file
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream certFile = new FileInputStream("exported_certificate.cer");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certFile);
            certFile.close();

            // load the keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream keyStoreFile = new FileInputStream("keystore.p12");
            char[] password = "Group2".toCharArray();
            keyStore.load(keyStoreFile, password);
            keyStoreFile.close();

            // Set the system properties
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStore", "keystore.p12");
            System.setProperty("javax.net.ssl.keyStorePassword", "Group2");
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.trustStore", "truststore.p12");
            System.setProperty("javax.net.ssl.trustStorePassword", "Group2");


            AuctionSystem si = new AuctionSystem();
            AuctionSystemInterface s = (AuctionSystemInterface) UnicastRemoteObject.exportObject(si, 0);
            Naming.rebind("rmi://localhost:" + port + "/AuctionSystemService", s);
            System.out.println("Server is starting...");
        } catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        // create new server for auction system
        int p = 1099; // default port number

        if (args.length == 1) {
            p = Integer.parseInt(args[0]); // custom port number
        }
        new Server(p);
    }
}

