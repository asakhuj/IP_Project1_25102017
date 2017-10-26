/**
 * 
 *//*

*//**
 * @author Arpita
 *
 */

import java.io.*;
import java.util.*;
import java.net.*;

class Peer5RFC  {
	//Create LL for the RFC Index 
	
	public Peer5RFC() {
		
	}
		
		
		public String getRfcnumber() {
			return rfcnumber;
		}
		public void setRfcnumber(String rfcnumber) {
			this.rfcnumber = rfcnumber;
		}
		public String getRfcname() {
			return rfcname;
		}
		public void setRfcname(String rfcname) {
			this.rfcname = rfcname;
		}
		public String getHostname() {
			return hostname;
		}
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		
		String rfcname;
		String hostname;
		String rfcnumber;
		
		
		
	public Peer5RFC(String a, String b, String c) {
		
			
			rfcnumber = a;
			rfcname = b;
			hostname = c;
			
			
		}
	
	static LinkedList<Peer5RFC> rfcList = new LinkedList<Peer5RFC>();
	@Override
    public String toString() {
        return "RFCList [rfcnumber=" + rfcnumber + ", rfcname=" + rfcname + ", hostname="
                + hostname    + "]";
    }
	
	
	public  boolean searchForFilesToDownload(LinkedList<Peer5RFC> myRFCIndex, String myPortNumber) {
		boolean areThereAnyFiles =false;
		
		if(myRFCIndex!=null) {
		for(Peer5RFC element : myRFCIndex) {
			if(element.getHostname()!=myPortNumber) {
				areThereAnyFiles=true;
			}
		}
		
		
	}
		
		
      return areThereAnyFiles;
	}
	
	
}



class ServerPeer5 implements Runnable {
	
	public void run() {
		try {
			ServerSocket welcomeSocket = new ServerSocket(65406);
		
			while(true) {
				
				
				Socket connectionSocket = welcomeSocket.accept();
				
				
				BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				String data = inputFromClient.readLine();
				//System.out.println(data);
				
				
				if(data.equalsIgnoreCase("GET RFC-INDEX")) {
					
				
				
					StringBuffer rfcIndexResponse = new StringBuffer("RFC-INDEX RESPONSE");
					rfcIndexResponse.append(" 200");
					rfcIndexResponse.append(" OK");
					//System.out.println(rfcIndexResponse.toString());
					outToClient.writeBytes(rfcIndexResponse.toString()+'\n');
					outToClient.writeBytes("Size : "+Peer5.arrFiles.size() +'\n');
				
					for(String abc : Peer5.arrFiles) {
							outToClient.writeBytes(abc+'\n');
					}
				
					outToClient.flush();
					inputFromClient.close();
					outToClient.close();
					connectionSocket.close();
					}
				

				else if(data.startsWith("PROVIDE")) {
					
						//Server will upload the files now :
						String fileFinal = data.substring(12);
						//String downloadFolder = "F:/Arpita/IP/Project 1/RFC/Peer5";
						String currentDirectory = System.getProperty("user.dir");
						String myFolder = currentDirectory+File.separator+"Peer5"; 
						String file_name = myFolder+"/"+fileFinal;
						File myFile = new File (file_name);
						long sizeOfFile = myFile.length();
						int changeToInt = (int)sizeOfFile;
						byte [] mybytearray  = new byte [(int)myFile.length()];
						FileInputStream fis = new FileInputStream(file_name);
						BufferedInputStream bis = new BufferedInputStream(fis);
						bis.read(mybytearray,0,mybytearray.length);
						OutputStream os = connectionSocket.getOutputStream();
						//System.out.println("Sending " + file_name + "(" + mybytearray.length + " bytes)");
						outToClient = new DataOutputStream(os);
						String header1 = "PROVIDING RFC "+fileFinal + '\n';
						//System.out.println(header1);
						outToClient.writeBytes(header1);
						String header2 = "CONTENT-LENGTH "+sizeOfFile + '\n'; //15
						outToClient.writeBytes(header2);
						os.write(mybytearray,0,changeToInt);
						os.flush();
						bis.close();
						os.close();
						connectionSocket.close();
					
				}
				
				
			}
	} catch(Exception e) {
		
	}
	}
	
	}


public class Peer5 {
	
	static boolean appActive = true;
	static ArrayList<String> arrFiles =  new ArrayList<String>();
	static LinkedList <RegisterPeer> activePeerList;
	static long timeAppend =0;
	
	public static void main (String args[]) throws Exception {
		
			Socket clientSocket0;
			String myHostName = InetAddress.getLocalHost().getHostName();
			String myPortNumber = "65406"; //Port number for Peer2
			String rsHostname = myHostName;
			Scanner sc; //For user inputs
			String reply; //Reply from user
			boolean activePeersPresent=true; //To check if any peers are active
			ArrayList<Integer> portNumbersOfActivePeers = new ArrayList<Integer>();
		
	
		 				 
		//Creating Local RFC-Index
				//File folder = new File("F:/Arpita/IP/Project 1/RFC/Peer5"); //Folder for Local RFC
			String currentDirectory = System.getProperty("user.dir");
			String myFolderPath = currentDirectory+File.separator+"Peer5";
			File folder = new File(myFolderPath);
				File[] listOfFiles = folder.listFiles();

				   for (int i = 0; i < listOfFiles.length; i++) {
				      if (listOfFiles[i].isFile()) {
				        System.out.println("File " + listOfFiles[i].getName());
				        String fileName = listOfFiles[i].getName();
				        String rfc_number[] = fileName.split("\\.");
				        Peer5RFC peerObj = new Peer5RFC(rfc_number[0], listOfFiles[i].getName() ,myPortNumber); //Have to change
				        Peer5RFC.rfcList.add(peerObj);
				        arrFiles.add(fileName);
				       
				      } 
				 
				    }
				    
				    System.out.println("Local RFC Index" +Peer5RFC.rfcList.toString());
				    
				    //Thread for the server process
				    ServerPeer5 obj = new ServerPeer5();
					Thread thread = new Thread(obj);
				    thread.start();
				    
				    //Checking Abrupt leaving of the peer
				  //To check if the application is terminated
					
					class ShutdownHook  extends Thread 
					{
					    public void run() 
					    {
					    	try {
					      System.out.println("Exit application initated : Notify the Registration Server");
					      Socket clientSocket = new Socket(rsHostname, 65423);
							
					      DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); //For sending output to RS
					
					      
						  Peer5.appActive = false;  // boolean to check if the application is active
						  
						  String LeaveQuery = "LEAVING";
						  outToServer.writeBytes(LeaveQuery + '\n');
						  outToServer.writeBytes("Hostname : "+myHostName + '\n');
						  outToServer.writeBytes("Port number : " +myPortNumber + '\n');
						  clientSocket.close();
						
						  System.out.println("After sending leave query to RS");
					      
					      
					    }
					    
					    catch(Exception e) {}
					}
				}

					
			        //Detecting random shutdown
					Runtime.getRuntime().addShutdownHook(new ShutdownHook());
				    
				    
		
				    //Starting registration with RS
		           System.out.println("Do you want to register with Registration Server ?, type Y if you want to proceed");
				   sc = new Scanner(System.in);
				   reply = sc.next();
				    
					if(reply.equalsIgnoreCase("y")) {
					      TimerTask timerTask = new TimerTask() {
								
								@Override
								public void run()  {
									// TODO Auto-generated method stub
									try {
										if(Peer5.appActive) {
											Socket clientSocket = new Socket(rsHostname, 65423);
											DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); //For sending output to RS
									
											BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //For getting Input from RS
									
									     //Sending Registration details to RS
											String RSQuery = "REGISTER";
								     
											outToServer.writeBytes(RSQuery + '\n');
								
											outToServer.writeBytes("Hostname : "+myHostName + '\n');
								
											outToServer.writeBytes("Port number : " +myPortNumber + '\n');
  
											String registerServerResponse = inFromServer.readLine(); //Registered
											String receivedCookie = inFromServer.readLine().substring(9);
											System.out.println(registerServerResponse +" " +receivedCookie);
											clientSocket.close();
											outToServer.close();
											System.out.println("You are registered with the Registration Server");
									    
										}
									}
									catch(Exception e) {}
									
								}
							};
							
							Timer timer = new Timer();

							timer.scheduleAtFixedRate(timerTask, 0, 7200*1000); //After every 7200 seconds, register with RS
					     
					    
					
						}
					
					
		//Sending PQuery to RS
					
		System.out.println("You are registered with RS, type PQuery to get the list of active peers from RS");
		sc = new Scanner(System.in);
		reply = sc.next();
		if(reply.equalsIgnoreCase("PQuery")) {
			   Socket clientSocket = new Socket(rsHostname, 65423);
			   DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); //For sending output to RS
		       InputStream inputStream = clientSocket.getInputStream();
		       BufferedReader inFromServer = new BufferedReader(new InputStreamReader(inputStream)); //For getting Input from RS
		
		     //Sending Query details to RS
		      String PQuery = "PQuery";
	          outToServer.writeBytes(PQuery + '\n');
	          outToServer.writeBytes("Hostname : "+myHostName + '\n');
	          outToServer.writeBytes("Port number : " +myPortNumber + '\n');
	          
	     
	          String responseHeader = inFromServer.readLine();
	          String statusHeader = inFromServer.readLine();
	          
	          if(statusHeader.equalsIgnoreCase("STATUS 200 OK")) {
	        	  int numberOfActivePeers = Integer.parseInt(inFromServer.readLine());
		          System.out.println(numberOfActivePeers);
	        	  for(int i=0;i<numberOfActivePeers;i++) {
	        		  //System.out.println(inFromServer.readLine()+"---");
	        		  String val = inFromServer.readLine();
	        		  System.out.println(val);
	        		  portNumbersOfActivePeers.add(Integer.parseInt(val));  
	        	  }
	        	  
	        }
	     else {
	    	 System.out.println(statusHeader);
	    	 System.out.println("No active peers left");
	    	 activePeersPresent=false;
			}
	          outToServer.close();
	          inFromServer.close();
	          inputStream.close();
	          clientSocket.close();
	     
		}
		System.out.println(portNumbersOfActivePeers+"----------------");
		
		//Enable sending only when active peers are present
		if(activePeersPresent) {
		
			System.out.println("Type RFC-Index if you need RFC-Index from all the active peers in system");
			sc = new Scanner(System.in);
			reply = sc.next();
			System.out.println("Your response " +reply);
		
			if(reply.equalsIgnoreCase("RFC-Index")) {
		
				//First check if any active peers are present
					if(portNumbersOfActivePeers != null) {
							for(int element : portNumbersOfActivePeers) {
		
								clientSocket0 = new Socket(myHostName, element); //Peer0
		
								DataOutputStream outToServer0 = new DataOutputStream(clientSocket0.getOutputStream());
		
								InputStream inputStream = clientSocket0.getInputStream();
		
								BufferedReader inFromServer0 = new BufferedReader(new InputStreamReader(inputStream));
		
								String rfc_query = "GET RFC-INDEX";
				
								outToServer0.writeBytes(rfc_query + '\n');
		
								String rfc_query_response =inFromServer0.readLine();
								String numberOfFilesAtRemote =inFromServer0.readLine().substring(7);
				      
								System.out.println(numberOfFilesAtRemote);
								int num = Integer.parseInt(numberOfFilesAtRemote);

								ArrayList<String> titleList = new ArrayList<String>();
				      
								for(int i=0; i<num;i++) {
									titleList.add(inFromServer0.readLine());
				    	  
								}
								System.out.println(titleList);
	
						
								for(String fileName : titleList) {
									String rfc_number[] = fileName.split("\\.");
									Peer5RFC peerObj = new Peer5RFC(rfc_number[0], fileName ,element+""); //Have to change
									Peer5RFC.rfcList.add(peerObj);
							
								}
								inFromServer0.close();
								inputStream.close();
								outToServer0.close();
								clientSocket0.close();
		
							}
			}
			System.out.println(Peer5RFC.rfcList);
			
		}
		
		
	//Download file code
    	
  	System.out.println("Do you want to download all files from all the active peers ?, type Y if you want to proceed");
  	sc = new Scanner(System.in);
	reply = sc.next();
	Peer5RFC obj2 = new Peer5RFC();
	
	if(reply.equalsIgnoreCase("y")) {
	
	//Main download code
		
		boolean otherFilesTobeDownloaded = obj2.searchForFilesToDownload(Peer5RFC.rfcList,myPortNumber);
	

		if(otherFilesTobeDownloaded) {
	      	
			//String downloadFolder = "F:/Arpita/IP/Project 1/RFC/Peer5"; //Folder where the files will be downloaded
			 
			String downloadFolder = myFolderPath;
			  	String SERVER = "127.0.0.1";  // localhost
			  	int bytesRead;
			  	int current;
	
			  	long startTime = System.currentTimeMillis();
			  	
			  	for(Peer5RFC abc : Peer5RFC.rfcList) 
			  	    {
			  		if(abc.getHostname() != myPortNumber ) {
			  			
			  			String fileNameToBeDownloaded = abc.rfcname;
				  		int portNumberOfServer = Integer.parseInt(abc.hostname);
				  		clientSocket0 = new Socket(SERVER, portNumberOfServer);
			  		
				  		DataOutputStream outToServer0 = new DataOutputStream(clientSocket0.getOutputStream());

				  		String provide_rfc_msg = "PROVIDE RFC "+fileNameToBeDownloaded + '\n';
				  		outToServer0.writeBytes(provide_rfc_msg);
				  		
				  		long time1 = System.currentTimeMillis();
			  		
				  		InputStream inStream = clientSocket0.getInputStream();
					
				  		BufferedReader inFromServer0 = new BufferedReader(new InputStreamReader(inStream));
					
				  		String response_header_RFC_query = inFromServer0.readLine();
					
				  		int content_length = Integer.parseInt(inFromServer0.readLine().substring(15));
					
				  		System.out.println("Content length " +content_length);
			  		
			  	    // receive file
			  		String fileDownloaded = downloadFolder+"/"+fileNameToBeDownloaded; //Path where the file will be created
			  		System.out.println(fileDownloaded);
			  		bytesRead=0;
			  		current = 0;
			  	    byte [] mybytearray  = new byte [content_length]; //Creating a byte array with required content length
			  	    InputStream inputStream = clientSocket0.getInputStream(); //Getting input from peer0
			  	    FileOutputStream fileOutputStream = new FileOutputStream(fileDownloaded); //Sending the output to local directory
			  	    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			  	  //read the input from peer0
			  	    bytesRead = inputStream.read(mybytearray,0,content_length);
			  	    System.out.println("First read : " +bytesRead);
			  	  current = bytesRead;
			  	    if(bytesRead!=content_length) {
			  	    	current = bytesRead;
			  	    	do {
					  	       bytesRead =
					  	          inputStream.read(mybytearray, current, (mybytearray.length-current));
					  	     System.out.println("***"+bytesRead);
					  	     if(bytesRead == -1) {
					  	    	 current = content_length;
					  	     }
					  	     else 
					  	       current += bytesRead;
			  	    	 } while(current < content_length );
			  	    }
			  	    
			  	    bufferedOutputStream.write(mybytearray, 0 , current);
			  	    
			  	    bufferedOutputStream.flush();
			  	    bufferedOutputStream.close();
			  	    inputStream.close();
			  	    inFromServer0.close();
			  	    
			  	    fileOutputStream.close();
			  	    System.out.println("File " + downloadFolder+"/"+fileNameToBeDownloaded
			  	        + " downloaded (" + current + " bytes read)");
			  	    
			  	  long time2 = System.currentTimeMillis();
			  	    long time3 = time2-time1;
			  	    timeAppend += time3;
			  	  String fileToStoreTimeStamps = currentDirectory+File.separator+"Peer5.text";
			  	  PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileToStoreTimeStamps, true)));
			  	  //PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("F:/Arpita/IP/Project 1/Time Calculated/Peer5_task1.txt", true)));
				  out.println(timeAppend);
				  out.close();
				  
			  	  }
			  	
			  	
			  	
			}
			  	/*long stopTime = System.currentTimeMillis();
			  	System.out.println("Start time of timer" +startTime);
			  	System.out.println("Stop time of timer" +stopTime);*/
			  	long timeTaken = timeAppend;
			  	System.out.println("Time taken in milliseconds : "+timeTaken);
	
		  }
			  	
			}
		
	
		  }
		
		
		
		//Arpita 19 Oct
		
		

					
	
							
		
		
		}	            
	}
	


