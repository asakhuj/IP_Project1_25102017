import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Arpita
 *
 */
class RegisterPeer extends LinkedList {
	String hostName;
	int cookie;
	boolean isActive;
	int ttl;
	int portNumber;
	int noOfTimesActive;
	String lastActiveOn;
	static LinkedList<RegisterPeer> peerList = new LinkedList<RegisterPeer>();
	
	//Getters and setters
	public String getHostName() {
		
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getCookie() {
		return cookie;
	}

	public void setCookie(int cookie) {
		this.cookie = cookie;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public int getNoOfTimesActive() {
		return noOfTimesActive;
	}

	public void setNoOfTimesActive(int noOfTimesActive) {
		this.noOfTimesActive = noOfTimesActive;
	}

	public String getLastActiveOn() {
		return lastActiveOn;
	}

	public void setLastActiveOn(String lastActiveOn) {
		this.lastActiveOn = lastActiveOn;
	}

	
	//Default constructor
	RegisterPeer(){
		
	}
	
	
	public RegisterPeer(String a, int b, boolean c, int d,int e, int f, String g) {
		
			hostName = a;
			cookie = b;
			isActive = c;
			ttl = d;
			portNumber = e;
			noOfTimesActive =f;
			lastActiveOn = g;
		
	}
	
	
	/**
	 * This method is used to get the cookie of the peer already registered with RS
	 * @param peerList is the list of peers registered with the RS
	 * @param hostName is the host trying to register with the RS
	 * @param portNumber of the host trying to register with the RS
	 * @return
	 */
	public int getCookieOfPeer(LinkedList<RegisterPeer> peerList,String hostName, int portNumber) {
		int requiredCookie=0;
	
		 if(peerList!=null){
		    	for(RegisterPeer b:peerList){  
		    		if(b.hostName.equals(hostName) && b.portNumber == portNumber){
		    			requiredCookie = b.cookie;
		    		}
		    	}
		 }
		    	
		    return requiredCookie;			
		    	     
		
	}
	
	@Override
    public String toString() {
        return "RegisterPeer [hostName=" + hostName + ", cookie=" + cookie + ", isActive="
                + isActive + ", ttl=" + ttl    + ", portNumber=" + portNumber   + ", noOfTimesActive=" + noOfTimesActive   + ", lastActiveOn=" + lastActiveOn          + "]";
    }
	
	
	/**
	 * This method is used to find whether the peer with a combination of Hostname and Portnumber are registered with the RS 
	 * @param peerList
	 * @param hostName
	 * @param portNumber
	 * @return true if the peer is already registered with the RS
	 */
	public boolean findElement(LinkedList<RegisterPeer> peerList,String hostName, int portNumber){
	  
		boolean alreadyRegistered=false;
	    if(peerList!=null){
	    	for(RegisterPeer b:peerList){  
	    		if(b.hostName.equals(hostName) && b.portNumber == portNumber){
	    			alreadyRegistered=true;
	    		}
	    	
	    			
	    	    }     
	    }
	 return alreadyRegistered;
	}
	
	/**
	 * This method is used to update the fields of the peer already registered with the peer when RS receives Keep Alive message
	 * @param peerList is the static peerList which the RS is maintaining
	 * @param hostName of the peer trying to send keep alive message
	 * @param portNumber of the peer trying to send keep alive message
	 */
	public void updateActiveFieldsOfPeer(LinkedList<RegisterPeer> peerList,String hostName, int portNumber) {
		 if(peerList!=null){
		    	for(RegisterPeer b:peerList){  
		    		if(b.hostName.equals(hostName) && b.portNumber == portNumber){
		    			b.isActive =true;
		    			b.lastActiveOn = new Date().toString();
		    			b.noOfTimesActive = ++(b.noOfTimesActive);
		    			b.ttl = 7200;
		    		}
		    	}
		 }
	}
	
	/**
	 * This method is used to update the active fields of the peers whenever RS receives Leaving message
	 * @param peerList is the list of the peers RS maintains
	 * @param hostName of the peer sending Leaving message
	 * @param portNumber of the peer sending Leaving mssage
	 */
	public void disableActiveFieldsOfPeer(LinkedList<RegisterPeer> peerList,String hostName, int portNumber) {
		 if(peerList!=null){
		    	for(RegisterPeer b:peerList){  
		    		if(b.hostName.equals(hostName) && b.portNumber == portNumber){
		    				b.isActive =false;
		    				b.ttl = 0;
		    		}
		    	}
		 }
	}
	
	
	
	/**
	 * This method is used to find the peers which are currently active
	 * @param peerList is the list of peers RS maintains
	 * @param hostName of the peer asking for PQuery
	 * @param portNumber of the peer asking for PQuery
	 * @return
	 */
	public LinkedList<RegisterPeer> getActivePeerList(LinkedList<RegisterPeer> peerList,String hostName, int portNumber) {
		LinkedList<RegisterPeer> activePeerList = new LinkedList<RegisterPeer>();
		 if(peerList!=null){
		    	for(RegisterPeer b:peerList){  
		    		if(b.isActive) {
		    		if(!(b.hostName.equals(hostName) && b.portNumber == portNumber)){ //Shouldnot be equal to current peer
		    			activePeerList.add(b);
		    		}
		    	}
		    }
		 }
		 return activePeerList;
	}
	
	
	
	
}



/**
 * @author Arpita
 * This class contains the main method along with the Server code for RS
 *
 */
public class RegistrationServer {
	
	static int uniqueCookie = 100;
	
	public static void main(String args[]) throws Exception{
		String hostName ;
		int portNumber;
		int initialTTL = 7200;
		boolean isActive = true;
		Date lastActiveOn = new Date();
		int numberOfTimesActive = 1;
		RegisterPeer registerObj;
		int cookieAssigned=0;
				
		//Step 1 : Open a Welcome socket
		ServerSocket welcomeSocket = new ServerSocket(65423);
		
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run()  {
				// TODO Auto-generated method stub
				try {
					if(RegisterPeer.peerList !=null) {
						for(RegisterPeer obj : RegisterPeer.peerList) {
							if(obj.isActive) {
								if(obj.ttl >0)
									obj.ttl = obj.ttl -1;
							
								if(obj.ttl<0) {
									obj.isActive=false;
							
								}
							}
						}
					
						
					}
				}
				catch(Exception e) {}
				
			}
		};
		
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(timerTask, 0, 1*1000); //After every 1 second  to decrement the TTL field of the peers active with RS
		
				
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			//Client is sending hostname and portNumber to register
			String response = inputFromClient.readLine();
			if(response.startsWith("REGISTER")) {
				hostName = inputFromClient.readLine().substring(11);
				portNumber = Integer.parseInt(inputFromClient.readLine().substring(14));
				RegisterPeer regObject = new RegisterPeer();
				boolean findElement = regObject.findElement(RegisterPeer.peerList, hostName, portNumber);
				if(findElement) {
					cookieAssigned = regObject.getCookieOfPeer(RegisterPeer.peerList, hostName, portNumber);
					//Update lastActiveOn,isActive and numberOfTimesActive
					regObject.updateActiveFieldsOfPeer(RegisterPeer.peerList, hostName, portNumber);
					outToClient.writeBytes("REGISTERED" +'\n');
					outToClient.writeBytes("COOKIE : " +cookieAssigned+""+'\n');
					outToClient.flush();
				
				}
				else {
					cookieAssigned= (uniqueCookie)++;
					registerObj = new RegisterPeer(hostName, cookieAssigned,isActive, initialTTL, portNumber, numberOfTimesActive, lastActiveOn.toString());
				    RegisterPeer.peerList.add(registerObj);
				    outToClient.writeBytes("REGISTERED" +'\n');
				    outToClient.writeBytes("COOKIE : " +cookieAssigned+""+'\n');
				    outToClient.flush();
				    outToClient.close();
				
					}
			
			
		}
			else if(response.startsWith("LEAVING")) {
					String hostLeaving = inputFromClient.readLine().substring(11);
					int portNumberLeaving = Integer.parseInt(inputFromClient.readLine().substring(14));
					RegisterPeer objReg = new RegisterPeer();
					objReg.disableActiveFieldsOfPeer(RegisterPeer.peerList, hostLeaving, portNumberLeaving);
					connectionSocket.close();
			}
			
			else if(response.startsWith("PQuery")) {
				String hostRequesting = inputFromClient.readLine().substring(11);
				outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				int portNumberRequesting = Integer.parseInt(inputFromClient.readLine().substring(14));
                RegisterPeer objReg = new RegisterPeer();
				LinkedList<RegisterPeer> activePeerList = objReg.getActivePeerList(RegisterPeer.peerList, hostRequesting, portNumberRequesting);
				String PQuery_Response = "PQuery-Response";
				if(activePeerList.size()>0) {
					outToClient.writeBytes(PQuery_Response +'\n');
					outToClient.writeBytes("STATUS 200 OK"+'\n');
					outToClient.writeBytes(activePeerList.size()+""+'\n');
					
					for(RegisterPeer e : activePeerList) {
						outToClient.writeBytes(e.portNumber+""+'\n');
						outToClient.flush();
				}
					
					connectionSocket.close();
				}
				else {
					outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					outToClient.writeBytes(PQuery_Response +'\n');
					outToClient.writeBytes("STATUS 100 NOT_OK"+'\n');
					outToClient.flush();
					connectionSocket.close();
				}
			}
		
		
		}
		
		
		
		
		
		
		
	}

}
