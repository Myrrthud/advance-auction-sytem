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
 AccountInterface provides method prototypes for Account
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SellerInterface extends Remote {

    // Get Login ID
    public String getLoginID() throws RemoteException;

    // Get name
    public String getName() throws RemoteException;

    // Get Password
    public String getPassword() throws RemoteException;

    // Get Admin
    public boolean getAdmin() throws RemoteException;

    // Set Login ID
    public void setLoginID(String login) throws RemoteException;

    // Set Name
    public void setName(String name) throws RemoteException;
    

    // Set Password
    public void setPassword(String pw) throws RemoteException;

    // Set Admin
    public void setAdmin(boolean a) throws RemoteException;

    // Receive Message
    public void receiveMessage(String message) throws RemoteException;
}
