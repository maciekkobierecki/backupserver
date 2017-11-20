import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {
		public ArrayList<FileMetadata> getFilesExistingOnServer() throws RemoteException;
		public void stopArchivization(FileMetadata metadata) throws RemoteException;
	}

