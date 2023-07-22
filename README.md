# RMI-Auction-System
A Java RMI auction system with simultaneous client access

The system will consist of an auctioning 
Server and two --Clients--.
One of the clients named as ‘Seller’ should be able to create a new auction, preferably with an ‘Auction 
ID’, for an item to be sold quoting a starting price and the reserve price (i.e., the minimum price of the 
item expected). 
The reserve price is kept secret. The auction is time bounded, which means that, the 
Seller should be able to close the auction after a specified time or when the deadline is reached. When 
an auction is closed, the Seller should either display the details of the winner or should inform that the 
reserve price is not attained.

The second client is called ‘Buyer’. This client is meant for placing a bid against the items under 
auction. The Buyer should be able to fetch the list of active auctions and bid for a selected item using 
the details of the buyer like Name and mobile number.

The Server should deal with requests from both Seller and Buyer maintaining the appropriate details of 
items, auctions, sellers and buyers.