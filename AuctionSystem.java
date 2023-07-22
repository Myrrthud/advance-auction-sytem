/*
 DISTRIBUTED SYSTEMS GROUP TWO
Stephen Sai Schandorf	11005248
Adelaide Anim-Annor	11004892
Nana Ama Bushel	11005089
Mildred Mensah	11361094
Daniel Obed Mensah	11004649
Andrew Bushel	11360976
Benjamin Agyeman	11005266
Richmond Sam Brown	11004928
 AuctionSystem implements the methods for the AuctionSystemInterface
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AuctionSystem implements AuctionSystemInterface {

    //private static final String FILE_DIRECTORY = "";
    private PrivateKey privateKey;

    private static final String AUCTION_FILE = "Auctions.bin";
    private static final String USER_FILE = "Users.bin";
    private static final String BUYER_FILE = "buyers.bin";
    private ConcurrentHashMap<Long, AuctionItem> auctionMap; // to store all auctions by auctionID
    private ConcurrentHashMap<String, SellerInterface> userMap; // to store all users currently registered in auction system
    private ConcurrentHashMap<String, BuyerInterface> buyerMap; // to store all buyers currently registered in auction system

    protected AuctionSystem() throws RemoteException {
        auctionMap = new ConcurrentHashMap<>();
        userMap = new ConcurrentHashMap<>();
        buyerMap = new ConcurrentHashMap<>();
        SellerAccount a = new SellerAccount();
        BuyerAccount b = new BuyerAccount();
        SellerInterface admin = (SellerInterface) UnicastRemoteObject.exportObject(a, 0);
        BuyerInterface buyer = (BuyerInterface) UnicastRemoteObject.exportObject(b, 0);

        admin.setLoginID("admin");
        admin.setName("administrator");
        admin.setPassword("admin");
        admin.setAdmin(true);
        userMap.put("admin", admin);
        buyerMap.put("admin", buyer);
    }
        // client authentication in server
      @Override
    public void authenticate(PublicKey publicKey, byte[] encryptedKey, byte[] signature, PrivateKey privateKey) throws RemoteException {
        try {
            // Verify digital signature
            Signature verifySignature = Signature.getInstance("SHA256withRSA");
            verifySignature.initVerify(publicKey);
            verifySignature.update(encryptedKey);
            boolean signatureValid = verifySignature.verify(signature);

            if (signatureValid) {
                // Decrypt the secret key with the private key
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedKey = cipher.doFinal(encryptedKey);

                // Use the decrypted secret key for further communication
                SecretKey secretKey = new SecretKeySpec(decryptedKey, "AES");
                // Perform additional authentication or authorization checks here
            } else {
                throw new RemoteException("Invalid digital signature. Client authentication failed.");
            }
        } catch (Exception e) {
            throw new RemoteException("Client authentication failed.", e);
        }
    }


    // Add a new User to server
    public boolean addUser(String userID, String name, String password, SellerInterface acc) throws RemoteException {
        boolean created = false;
        if (!userMap.containsKey(userID)) {
            acc.setLoginID(userID);
            acc.setName(name);
            acc.setPassword(password);
            userMap.put(userID, acc);
            created = true;
        } else {
            created = false;
        }

        return created;
    }

    // add buyer account details
    public boolean addBuyer(String buyerID, String name, String mobileNum, BuyerInterface acc) throws RemoteException {
        boolean created = false;
        if(!buyerMap.containsKey(name)){
            acc.setLoginID(buyerID);
            acc.setName(name);
            acc.setMobileNum(mobileNum);
            buyerMap.put(name,acc);
            created = true;
        }else {
            created = false;
        }
        return created;
    }

    //Buyer Login to server
    public BuyerInterface buyerLogin(String buyerID, String mobileNum, BuyerInterface acc) throws RemoteException {
        if(buyerMap.containsKey(buyerID) && buyerMap.get(buyerID).getMobileNum().equals(mobileNum)){
            acc = buyerMap.get(buyerID);
        }
        else{
            return null;
        }
        return acc;
    }

    // Login to server
    public SellerInterface userLogin(String userID, String password, SellerInterface acc) throws RemoteException {
        if (userMap.containsKey(userID) && userMap.get(userID).getPassword().equals(password)) {
            acc = userMap.get(userID);
        }
        else{
            return null;
        }
        return acc;
    }



    // Create an auction item, return id if created else if unsuccessful then return 0
    public String createAuctionItem(String name, double minValue, Date endTime, SellerInterface a) throws RemoteException {
        String result = "";
        long id = 0;
        if (endTime.before(new Date())) {
            result = "Please enter a valid end time (Any datetime after today/now).";
        } else if (auctionMap.isEmpty()) {
            id = 1;
            AuctionItem ai = new AuctionItem(id, name, minValue, endTime, a);
            auctionMap.put(id, ai);
            result = "Created auction successfully with auction ID: " + id;
        } else {
            id = auctionMap.size() + 1;
            AuctionItem ai = new AuctionItem(id, name, minValue, endTime, a);
            auctionMap.put(id, ai);
            result = "Created auction successfully with auction ID: " + id;
        }
        return result;
    }

    // List all auction items
    public String listAllItems() throws RemoteException {
        String result = "";
        if (auctionMap.isEmpty()) {
            result = "There are currently no auction items in the system.";
        } else {
            for (Entry<Long, AuctionItem> entry : auctionMap.entrySet()) {
                result = result + entry.getValue().getItemDetails();
            }
        }
        return result;
    }

    // Get an auction item
    public String getAuctionItem(long id) throws RemoteException {
        String result = "";
        if (auctionMap.isEmpty()) {
            result = "There are currently no auction items in the system.";
        } else if (auctionMap.get(id) == null) {
            result = "Could not find item with ID: " + id;
        } else {
            result = auctionMap.get(id).getItemDetails() + "\n";
        }
        return result;
    }

    // Bid for an auction item
    public String setBid(BuyerInterface bidder, long auctionID, double bidValue) throws RemoteException {
        String result = "";
        AuctionItem ai = auctionMap.get(auctionID);
        if (ai == null) {
            result = "Please enter a valid Auction ID.";
        } else if (bidValue <= ai.getMinBidValue()) {
            result = "Please place a bid of more than: $" + ai.getMinBidValue();
        } else if (ai.getStatus() == "closed") {
            result = "The auction period for this item is over. It is now closed. Please bid for another item.";
        } else {
            ai.setAuctionItemBid(bidder, bidValue);
            result = "The bid has been placed successfully for auction ID: " + auctionID;
        }
        return result;
    }

    // Save state (for server admin only)
    public String saveState() throws RemoteException {
        String result = "";
        try {
            ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(AUCTION_FILE)));
            o.writeObject(auctionMap);
            o.close();
            ObjectOutputStream o2 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(USER_FILE)));
            o2.writeObject(userMap);
            o2.close();
            ObjectOutputStream o3 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(BUYER_FILE)));
            o3.writeObject(buyerMap);
            o3.close();
            result = "Auction state saved in: " + AUCTION_FILE + "\nUsers state saved in: " + USER_FILE + "\nBuyers State restored from:" + BUYER_FILE;;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Restore to previous state (for server admin only)
    @SuppressWarnings("unchecked")
    public String restoreState() throws RemoteException {
        String result = "";
        try {
            ObjectInputStream i = new ObjectInputStream(new BufferedInputStream(new FileInputStream(AUCTION_FILE)));
            auctionMap = (ConcurrentHashMap<Long, AuctionItem>) i.readObject();
            ObjectInputStream i2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(USER_FILE)));
            userMap = (ConcurrentHashMap<String, SellerInterface>) i2.readObject();
            ObjectInputStream i3 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(BUYER_FILE)));
            buyerMap = (ConcurrentHashMap<String, BuyerInterface>) i3.readObject();

            for (AuctionItem ai : auctionMap.values()) {
                ai.setTimer();
            }

            result = "Auction state restored from: " + AUCTION_FILE + "\nUsers state restored from: " + USER_FILE + "\nBuyers State restored from:" + BUYER_FILE;
            i.close(); i2.close(); i3.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Check server status and return number of users currently online
    public String checkServer() throws RemoteException{
        return "Yes, the server is active with " + (userMap.size()-1) + " users online.\n";
    }
}