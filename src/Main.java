import java.rmi.RemoteException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws RemoteException {
		try {
			Config.init();
			FilesScheduler.init();
			FilesScheduler.loadMetadataListFromFile(Config.getProperty("serializedMetadataFile"));
			RMIService rmiService=new RMIService();
			rmiService.start();
			BackupServer server=new BackupServer();
			Runnable serverThread=server;
			Thread t=new Thread(serverThread);
			t.start();
			System.out.println("server started");
			Scanner scanner=new Scanner(System.in);
			while(true){
				String command=scanner.next();
				if(command.equals("exit")){
					FilesScheduler.saveMetadataList(Config.getProperty("serializedMetadataFile"));
					rmiService.stop();
					System.exit(0);
					
				}

				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
