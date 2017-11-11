import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

	private Socket incoming;
	public ConnectionHandler(Socket socket){
		incoming= socket;
	}
	@Override
	public void run() {
		try {
			InputStream inStream=incoming.getInputStream();
			OutputStream outStream=incoming.getOutputStream();
			
			incoming.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		


	}

}
