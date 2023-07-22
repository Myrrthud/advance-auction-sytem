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
 AuctionSystemInterface provides method prototypes for each method required in AuctionSystemImpl
 */

import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.rmi.Remote;
import java.util.Date;

public interface AuctionSystemInterface extends Remote {

    void authenticate(PublicKey publicKey, byte[] encryptedKey, byte[] signature, PrivateKey privateKey) throws RemoteException;

    // Add a new user account to server
    public boolean addUser(String userID, String name, String password, SellerInterface acc) throws RemoteException;
    
    //Add Buyer details
    public boolean addBuyer(String buyerID, String name, String mobileNum, BuyerInterface acc) throws RemoteException;


    // Login to server
    public SellerInterface userLogin(String userID, String password, SellerInterface acc) throws RemoteException;

    //Buyer Login
    public BuyerInterface buyerLogin(String buyerID, String mobileNum, BuyerInterface acc) throws RemoteException;

    // Create an auction item
    public String createAuctionItem(String name, double minValue, Date endTime, SellerInterface a) throws RemoteException;

    // Get an auction item
    public String getAuctionItem(long id) throws RemoteException;

    // List all auction items
    public String listAllItems() throws RemoteException;

    // Bid for an auction item
    public String setBid(BuyerInterface bidder, long auctionID, double bidValue) throws RemoteException;


    // Save state (for server admin only)
    public String saveState() throws RemoteException;

    // Restore to previous state (for server admin only)
    public String restoreState() throws RemoteException;
    
    // Check server status and return number of users currently online
    public String checkServer() throws RemoteException;
}
