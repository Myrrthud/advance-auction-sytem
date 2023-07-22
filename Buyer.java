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
 1. Client provides the user interface for account login and interaction with the system
 2. Users are allowed to create new accounts or login with an existing account,
 then select from options to create and bid on auction items.
 */

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.InputMismatchException;

public class Buyer {
    private static final String TRUSTSTORE_PATH = "truststore.jks"; //path to truststore.jks
    private static final String TRUSTSTORE_PASSWORD = "Group2"; //truststore password

    public static void main(String[] args) {

        String reg_host = "localhost";
        int reg_port = 1099;

        if (args.length == 1) {
            reg_port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            reg_host = args[0];
            reg_port = Integer.parseInt(args[1]);
        }

        try {
            // Set the truststore for secure communication
            System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_PATH);
            System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);

            // Connect to the remote server
            AuctionSystemInterface server = (AuctionSystemInterface) Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/AuctionSystemService");

            // Generate a key pair for RSA encryption
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();



            // Generate a symmetric key for file encryption
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

        

            // Encrypt the secret key with the server's public key
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());

        

            // Generate a digital signature
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(encryptedKey);
            byte[] digitalSignature = signature.sign();

            server.authenticate(publicKey, encryptedKey, digitalSignature, privateKey);

            BuyerAccount b = new BuyerAccount();
            BuyerInterface account = (BuyerInterface) UnicastRemoteObject.exportObject(b, 0);
            displayAccountMenu(server, account);
        } // Catch the exceptions that may occur - rubbish URL, Remote exception, not bound exception
        catch (MalformedURLException murle) {
            murle.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayAccountMenu(AuctionSystemInterface s, BuyerInterface a) {
        while (true) {
            int choice = -1;
            System.out.println("//-------------------------//");
            System.out.println("[1] Create buyer account");
            System.out.println("[2] Login to buyer Interface");
            System.out.println("//-------------------------//");
            System.out.println("Choose a number: ");

            Scanner scan = new Scanner(System.in);
            choice = scan.nextInt();

            switch (choice) {
                case 1:
                    provideDetails(s, a);
                    break;
                case 2:
                    login(s, a);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            scan.close();
        }
    }

    public static void provideDetails(AuctionSystemInterface s, BuyerInterface a) {
        Scanner scan = new Scanner(System.in);
        System.out.println("//-------------------------//");
        System.out.println("Enter your Buyer login ID: ");
        String bid = scan.next();
        System.out.println("Enter your name: ");
        String n = scan.next();
        System.out.println("Enter your Mobile Number: ");
        String mn = scan.next();
        try {
            boolean created = s.addBuyer(bid, n, mn, a);
            if (created) {
                System.out.println("Your buyer details have been created. Please login using option [2].");
            } else {
                System.out.println("Buyer detail is taken. Please choose another name.");
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        } finally {
            scan.close();
        }
    }

    public static void login(AuctionSystemInterface s, BuyerInterface a) {
        boolean retry = true;
        while (retry) {
            Scanner scan = new Scanner(System.in);
            System.out.println("//-------------------------//");
            System.out.println("Enter your Buyer Login ID: ");
            String bid = scan.next();
            System.out.println("Enter your Mobile number: ");
            String mobileNum = scan.next();

            try {
                BuyerInterface acc = s.buyerLogin(bid, mobileNum, a);
                if (acc != null) {
                    a = acc;
                    System.out.println("Login successful. Welcome " + a.getName() + "!");
                    retry = false;
                    if (a.getAdmin()) {
                        displayAdminOptions(s, a);
                    } else {
                        displayOptions(s, a);
                    }
                } else {
                    System.out.println("Invalid Login ID or password. Please try again.");
                }
            } catch (RemoteException re) {
                re.printStackTrace();
            } finally {
                scan.close();
            }
        }
    }

    public static void displayAdminOptions(AuctionSystemInterface s, BuyerInterface a) {
        while (true) {
            int option = -1;
            System.out.println("//------Admin Options------//");
            System.out.println("[1] Save System State");
            System.out.println("[2] Restore System State");
            System.out.println("[3] Check server status");
            System.out.println("[0] Exit Auction System");
            System.out.println("//-------------------------//");
            System.out.print("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            option = scanner.nextInt();

            System.out.println("//-------------------------//");

            /* User Interface option */
            switch (option) {
                case 1:
                    try {
                        System.out.println(s.saveState());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        System.out.println(s.restoreState());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    checkStatus(s);
                    break;
                case 0:
                    System.out.println("Exited from Auction System.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Choose from 0 to 2.");
            }
        }
    }

    public static void displayOptions(AuctionSystemInterface s, BuyerInterface a) {
        while (true) {
            int option = -1;
            System.out.println("//-------------------------//");
            System.out.println("[1] View all auction item(s)");
            System.out.println("[2] Search for an item (by AuctionID)");
            System.out.println("[3] Bid for an item");
            System.out.println("[4] Check server status");
            System.out.println("[0] Exit Auction System");
            System.out.println("//-------------------------//");
            System.out.print("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            option = scanner.nextInt();

            System.out.println("//-------------------------//");

            /* User Interface option */
            switch (option) {
                case 1:
                    showAllAuctions(s);
                    break;
                case 2:
                    getAuction(s);
                    break;
                case 3:
                    bid(s, a);
                    break;
                case 4:
                    checkStatus(s);
                    break;
                case 0:
                    System.out.println("Exited from Auction System.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Choose from 0 to 4.");
            }
        }
    }

    // Show all auction items
    private static void showAllAuctions(AuctionSystemInterface s) {
        try {
            System.out.println(s.listAllItems());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Search for an auction item by auction id
    private static void getAuction(AuctionSystemInterface s) {
        try {
            System.out.print("Enter AuctionID: ");
            Scanner scan = new Scanner(System.in);
            long aid = scan.nextLong();
            System.out.println(s.getAuctionItem(aid));
            scan.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InputMismatchException ime) {
            ime.printStackTrace();
        }
    }

    /* Perform auction bidding */
    private static void bid(AuctionSystemInterface s, BuyerInterface a) {
        try {
            System.out.print("Enter AuctionID to bid: ");
            Scanner longScan = new Scanner(System.in);
            long aid = longScan.nextLong();

            System.out.print("Enter bid value: ");
            Scanner lineScan = new Scanner(System.in);
            double value = Double.parseDouble(lineScan.nextLine());

            System.out.println(s.setBid(a, aid, value));
            longScan.close(); lineScan.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void checkStatus(AuctionSystemInterface s) {
        try {
            System.out.println(s.checkServer());
        } catch (RemoteException e) {
            System.out.println("The server is offline. Please try again later or contact the administrator.");
        }
    }
}
