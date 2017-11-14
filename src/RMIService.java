import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIService extends UnicastRemoteObject implements ServerInterface{
	protected RMIService() throws RemoteException {
		super();
		}
	private Registry rmiRegistry;
	@Override
	public ArrayList<FileMetadata> getFilesExistingOnServer() throws RemoteException {
		return FilesScheduler.getFileMetadataList();
	}
	public void start() throws Exception {
		rmiRegistry=LocateRegistry.createRegistry(Integer.parseInt(Config.getProperty("RMIport")));
		rmiRegistry.bind("backupServer",this);
		System.out.println("server started");
	}
	public void stop() throws Exception {
		rmiRegistry.unbind("backupServer");
		System.out.println("server stopped");
	}
	

}
