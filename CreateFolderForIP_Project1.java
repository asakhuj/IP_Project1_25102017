import java.io.File;

public class CreateFolderForIP_Project1 {
	public static void main(String args[]) {
		System.out.println(System.getProperty("user.dir"));
		String currentDirectory = System.getProperty("user.dir");
		String peer0Directory = currentDirectory+File.separator+"Peer0";
		String peer1Directory = currentDirectory+File.separator+"Peer1";
		String peer2Directory = currentDirectory+File.separator+"Peer2";
		String peer3Directory = currentDirectory+File.separator+"Peer3";
		String peer4Directory = currentDirectory+File.separator+"Peer4";
		String peer5Directory = currentDirectory+File.separator+"Peer5";
		String timeCalculations = currentDirectory+File.separator+"TimeCalculations";
		
		boolean peer0Success = (new File(peer0Directory)).mkdir();
		boolean peer1Success = (new File(peer1Directory)).mkdir();
		boolean peer2Success = (new File(peer2Directory)).mkdir();
		boolean peer3Success = (new File(peer3Directory)).mkdir();
		boolean peer4Success = (new File(peer4Directory)).mkdir();
		boolean peer5Success = (new File(peer5Directory)).mkdir();
		boolean timeCalSucess = (new File(timeCalculations)).mkdir();
		
		
		
		System.out.println(peer0Directory +"---"+peer0Success);
		System.out.println(peer1Directory+"---"+peer1Success);
		System.out.println(peer2Directory+"---"+peer2Success);
		System.out.println(peer3Directory+"---"+peer3Success);
		System.out.println(peer4Directory+"---"+peer4Success);
		System.out.println(peer5Directory+"---"+peer5Success);
		System.out.println(timeCalculations+"---"+timeCalSucess);
		
		
		
	}

}
