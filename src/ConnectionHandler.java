import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ConnectionHandler implements Runnable {
	public final static String PERMISSION="PERMISSION\n";
	public final static String NEWER_FILE_EXISTS_ON_SERWER="NEWER OR UP TO DATE FILE EXISTS ON SERWER\n";
	private Socket incoming;
	public ConnectionHandler(Socket socket){
		incoming= socket;	
	}
	@Override
	public void run() {
		try {
			InputStream inStream=incoming.getInputStream();
			OutputStream outStream=incoming.getOutputStream();
			DataInputStream dinStream=new DataInputStream(inStream);
			FileMetadata metadata=receiveMetadata(dinStream);
			if(FilesScheduler.isItNewerVersion(metadata.getFileDirectory(), metadata.getDate())){
				byte[] permissionBytes=PERMISSION.getBytes();
				outStream.write(permissionBytes);
				outStream.flush();
				File file=new File("data\\"+metadata.getFileName());
				FileOutputStream fos=new FileOutputStream(file);
				byte[] fileFragment=new byte[1024*1024];
				int byteNumber;
				while((byteNumber=dinStream.read(fileFragment))>0){
					fos.write(fileFragment, 0, byteNumber);
					fos.flush();
				}
				FilesScheduler.addFileMetadata(metadata);
				String OK="OK \n";
				byte[] okBytes=OK.getBytes();
				outStream.write(okBytes);
				outStream.flush();
				fos.close();
			}
			else{
				byte[] fileExists=this.NEWER_FILE_EXISTS_ON_SERWER.getBytes();
				outStream.write(fileExists);
				outStream.flush();
			}

			incoming.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public FileMetadata receiveMetadata(DataInputStream inStream) throws IOException{
			byte[] length=new byte[4];
			inStream.readFully(length, 0, 4);
			int metadataLength=ByteBuffer.wrap(length).getInt();
			byte[] metadata=new byte[metadataLength];
			inStream.readFully(metadata, 0, metadataLength);
			String stringMetadata=new String(metadata);
			String[] properties=stringMetadata.split("\n");
			String function=properties[0];
			String fileDirectory=properties[1];
			String date=properties[2];
			return new FileMetadata(function, fileDirectory, date);
	}

}
