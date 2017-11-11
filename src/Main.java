import java.rmi.RemoteException;

public class Main {

	public static void main(String[] args) throws RemoteException {
		try {
			ServerImpl server=new ServerImpl();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
