import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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
	private FileMetadata metadata;
	public ConnectionHandler(Socket socket) throws IOException{
		incoming= socket;	
		inStream=incoming.getInputStream();
		outStream=incoming.getOutputStream();
		dinStream=new DataInputStream(inStream);
	}
	private FileOutputStream getFileOutputStream(FileMetadata metadata) throws FileNotFoundException{
		File file=new File(STORAGE_DIRECTORY+metadata.getOnServerName()+"."+metadata.getFileExtension());
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
			String function="";
			metadata=receiveMetadata(dinStream, function);
			if(function.equals(UPLOAD_FUNCTION))
				receiveData();
			else if(function.equals(DOWNLOAD_FUNCTION))
				sendFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void sendFile() throws IOException {
		DataInputStream dis=getDataInputStream();
		byte[] fileFragment=new byte[FRAGMENT_SIZE];
		int available=-1;
		while((available=dis.read(fileFragment))!=-1)
			outStream.write(fileFragment, 0, available);
		incoming.shutdownOutput();
		//TUTAJ ODEBRAC ODPOWIEDZ
	}
	private DataInputStream getDataInputStream() throws FileNotFoundException {
		String filePath=metadata.getOnServerName();
		DataInputStream dis=new DataInputStream(new FileInputStream(STORAGE_DIRECTORY+filePath));
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
		incoming.close();
	}
	public int readMetadataSize() throws IOException{
		byte[] length=new byte[INTEGER_SIZE];
		dinStream.readFully(length, 0, INTEGER_SIZE);
		int metadataLength=ByteBuffer.wrap(length).getInt();
		return metadataLength;
	}
	public String[] readMetadata(int metadataLength) throws IOException{
		byte[] bufor=new byte[metadataLength];
		dinStream.readFully(bufor, 0, metadataLength);
		String metadata=new String(bufor);
		return metadata.split("\n");
	}
	public FileMetadata receiveMetadata(DataInputStream inStream,String function) throws IOException{
			int metadataLength=readMetadataSize();
			String[] metadata=readMetadata(metadataLength);
			function=metadata[FUNCTION_POSITION];
			String fileDirectory=metadata[DIRECTORY_POSITION];
			String date=metadata[DATE_POSITION];
			String onServerFileName=FilesScheduler.assignName();
			return new FileMetadata(onServerFileName,fileDirectory, date);
	}

}
