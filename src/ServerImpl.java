import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


	
	
 	class ServerImpl extends UnicastRemoteObject implements ServerInterface {

		Registry rmiRegistry;
		private ServerSocket serverSocket;
		
		protected ServerImpl() throws RemoteException {
			super();
		}
		
		public void start() throws Exception {
			rmiRegistry=LocateRegistry.createRegistry(1099);
			rmiRegistry.bind("server",this);
			System.out.println("server started");
		}
		public void startListen(){
			while(true){
				Socket incoming=serverSocket.accept();
				Runnable r=new ConnectionHandler(incoming);
			}
		}
		public void stop() throws Exception {
			rmiRegistry.unbind("server");
			unexportObject(this,true);
			unexportObject(rmiRegistry,true);
			System.out.println("server stopped");
		}
		@Override
		public String lastModificationDate(String fileName) throws RemoteException {
			return "dawno temu";
		}
		
	}

