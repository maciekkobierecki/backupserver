import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
	public final static String PERMISSION="PERMISSION\n";
	public final static String NEWER_FILE_EXISTS_ON_SERWER="NEWER OR UP TO DATE FILE EXISTS ON SERWER\n";
	public final static int INTEGER_SIZE=4;
	public final static int FUNCTION_POSITION=0;
	public final static int DIRECTORY_POSITION=1;
	public final static int DATE_POSITION=2;
	public final static String UPLOAD_FUNCTION="upload";
	public final static String DOWNLOAD_FUNCTION="download";
	public final static String STORAGE_DIRECTORY="data\\";
	private final static int FRAGMENT_SIZE=1048576;
	private Socket incoming;
	private InputStream inStream;
	private OutputStream outStream;
	private DataInputStream dinStream;
	private BufferedReader responseReader;
	private ObjectInputStream ois;
	private FileMetadata metadata;
	private String function;
	public ConnectionHandler(Socket socket) throws IOException{
		incoming= socket;	
		inStream=incoming.getInputStream();
		outStream=incoming.getOutputStream();
		responseReader=new BufferedReader(new InputStreamReader(inStream));
		ois=new ObjectInputStream(inStream);
		dinStream=new DataInputStream(inStream);
	}
	private FileOutputStream getFileOutputStream(FileMetadata metadata) throws FileNotFoundException{
		File file=new File(STORAGE_DIRECTORY+metadata.getOnServerName()+"."+metadata.getFileExtension());
		FileOutputStream fos=new FileOutputStream(file);
		return fos;
	}

	private void sendConfirmation() throws IOException{
		String OK="OK \n";
		byte[] okBytes=OK.getBytes();
		outStream.write(okBytes);
		outStream.flush();
	}
	private void sendRefusal() throws IOException{
		byte[] fileExists=NEWER_FILE_EXISTS_ON_SERWER.getBytes();
		outStream.write(fileExists);
		outStream.flush();
	}
	private void saveFile(FileOutputStream fos) throws IOException{
		byte[] fileFragment=new byte[1024*1024];
		int byteNumber;
		while((byteNumber=dinStream.read(fileFragment))>0){
			fos.write(fileFragment, 0, byteNumber);
			fos.flush();
		}
	}
	@Override
	public void run() {
		try {
			function=receiveFunction();
			metadata=receiveMetadata();
			if(function.equals(UPLOAD_FUNCTION)){
				String nameOnServer=nameRequest();
				metadata.setNameOnServer(nameOnServer);
				receiveData();
			}
			else if(function.equals(DOWNLOAD_FUNCTION)){
				sendPermission();
				sendFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				incoming.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private void sendPermission() throws IOException{
		byte[] permissionBytes=PERMISSION.getBytes();
		outStream.write(permissionBytes);
		outStream.flush();
	}
	private String nameRequest(){
		String nameOnServer=FilesScheduler.assignName();
		return nameOnServer;
	}
	private String receiveFunction() throws IOException, ClassNotFoundException{
		String function=(String)ois.readObject();
		return function;
	}
	private void sendFile() throws IOException, ClassNotFoundException {
		DataInputStream dis=getDataInputStream();
		byte[] fileFragment=new byte[FRAGMENT_SIZE];
		int available=-1;
		while((available=dis.read(fileFragment))!=-1){
			outStream.write(fileFragment, 0, available);
			outStream.flush();
		}
		incoming.shutdownOutput();
		String response=readResponse();
		System.out.println(response);
	}
	private String readResponse() throws IOException{
		String response=responseReader.readLine();
		return response;
	}
	private DataInputStream getDataInputStream() throws FileNotFoundException {
		String fileName=metadata.getOnServerName()+"."+metadata.getFileExtension();
		DataInputStream dis=new DataInputStream(new FileInputStream(STORAGE_DIRECTORY+fileName));
		return dis;
	}
	private void receiveData() throws IOException{
		FileOutputStream fos=getFileOutputStream(metadata);
		if(FilesScheduler.isItNewerVersion(metadata.getFileDirectory(), metadata.getDate())){
			sendPermission();
			saveFile(fos);
			FilesScheduler.addFileMetadata(metadata);
			sendConfirmation();
			fos.close();
		}
		else{
			sendRefusal();
		}
	}
	public FileMetadata receiveMetadata() throws IOException, ClassNotFoundException{
			Object ob=ois.readObject();
			FileMetadata metadata=null;
			if(ob instanceof FileMetadata)
			metadata=(FileMetadata)ob;
			return metadata;
	}

}
