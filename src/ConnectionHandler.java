import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ConnectionHandler implements Runnable {
	public final static String PERMISSION="PERMISSION\n";
	public final static String NEWER_FILE_EXISTS_ON_SERWER="NEWER OR UP TO DATE FILE EXISTS ON SERWER\n";
	public final static int INTEGER_SIZE=4;
	public final static int DIRECTORY_POSITION=1;
	public final static int DATE_POSITION=2;
	private Socket incoming;
	private InputStream inStream;
	private OutputStream outStream;
	private DataInputStream dinStream;
	private FileMetadata metadata;
	public ConnectionHandler(Socket socket) throws IOException{
		incoming= socket;	
		inStream=incoming.getInputStream();
		outStream=incoming.getOutputStream();
		dinStream=new DataInputStream(inStream);
	}
	private FileOutputStream getFileStream(FileMetadata metadata) throws FileNotFoundException{
		File file=new File("data\\"+metadata.getFileName());
		FileOutputStream fos=new FileOutputStream(file);
		return fos;
	}
	private void sendPermission() throws IOException{
		byte[] permissionBytes=PERMISSION.getBytes();
		outStream.write(permissionBytes);
		outStream.flush();
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
			FileMetadata metadata=receiveMetadata(dinStream);
			FileOutputStream fos=getFileStream(metadata);
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

			incoming.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public FileMetadata receiveMetadata(DataInputStream inStream) throws IOException{
			byte[] length=new byte[INTEGER_SIZE];
			inStream.readFully(length, 0, INTEGER_SIZE);
			int metadataLength=ByteBuffer.wrap(length).getInt();
			byte[] metadata=new byte[metadataLength];
			inStream.readFully(metadata, 0, metadataLength);
			String stringMetadata=new String(metadata);
			String[] properties=stringMetadata.split("\n");
			String fileDirectory=properties[DIRECTORY_POSITION];
			String date=properties[DATE_POSITION];
			return new FileMetadata(fileDirectory, date);
	}

}
