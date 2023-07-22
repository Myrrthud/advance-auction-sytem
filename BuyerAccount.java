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
 
 Account provides a class for user account creation in the system
 */

import java.rmi.RemoteException;

public class BuyerAccount implements BuyerInterface {

    private String buyerID;
    private String name;
    private String mobileNum;
    private boolean admin;

    protected BuyerAccount() throws RemoteException {
        buyerID = "";
        name = "";
        mobileNum = "";
        admin = false;
    }

  // Get Buyer Login ID
    public String getLoginID() throws RemoteException {
        return buyerID;
    }

    // Get Name
    public String getName() throws RemoteException {
        return name;
    }

    // Get Password
    public String getMobileNum() throws RemoteException {
        return mobileNum;
    }

    // Get Admin
    public boolean getAdmin() throws RemoteException {
        return admin;
    }

    // Set Buyer Login ID
    public void setLoginID(String login) throws RemoteException {
        buyerID = login;
    }

    // Set Name
    public void setName(String n) throws RemoteException {
        name = n;
    }

    // Set Password
    public void setMobileNum(String mn) throws RemoteException {
        mobileNum = mn;
    }

    // Set Admin
    public void setAdmin(boolean a) throws RemoteException {
        admin = a;
    }

    // Receive Message from Server
    public void receiveMessage(String message) throws RemoteException {
        System.out.println(message);
    }
}
