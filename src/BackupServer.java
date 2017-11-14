import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


	
	
 	class BackupServer implements Runnable {
		private ServerSocket serverSocket;
		
		protected BackupServer() throws NumberFormatException, IOException {
			super();
			serverSocket=new ServerSocket(Integer.parseInt(Config.getProperty("port")));
		}
		
		public void startListen() throws IOException{
			while(true){
				Socket incoming=serverSocket.accept();
				Runnable r=new ConnectionHandler(incoming);
				Thread t=new Thread(r);
				t.start();
			}
		}
		

		@Override
		public void run() {
			try {
				startListen();
			} catch (IOException e) {
				System.out.println("An Exception has occurred while waiting for connections");
			} catch (Exception e) {
				System.out.println("RMI error");
			}
			
		}
		
	}

