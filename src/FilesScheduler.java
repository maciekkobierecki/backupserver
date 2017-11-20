import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;

public class FilesScheduler{
	private static ArrayList<FileMetadata>fileMetadataList;
	
	public FilesScheduler(){
		fileMetadataList=new ArrayList<>();
	}
	public static void init(){
		fileMetadataList=new ArrayList<>();
	}
	public static synchronized void saveMetadataList(String fileName){
		try {
			FileOutputStream fileOut= new FileOutputStream(fileName);
			ObjectOutputStream outStream=new ObjectOutputStream(fileOut);
			outStream.writeObject(fileMetadataList);
			outStream.close();
			fileOut.close();
			System.out.println("Metadata saved to file");
			
		} catch (FileNotFoundException e) {
			System.out.println("File: "+Config.getProperty("serializedMetadataFile")+" not found.");
		} catch (IOException e) {
			System.out.println("An IOException has occurred while saving metadata to file");
		}
	}
	public static synchronized void addFileMetadata(FileMetadata fileMetadata){
		fileMetadataList.add(fileMetadata);
		}
	public static synchronized void removeFileMetadata(FileMetadata metadata){
		fileMetadataList.remove(metadata);
	}
	public static synchronized void removeFile(File file){
		if(file.delete())
			System.out.println(file.getName()+" deleted.");
		else
			System.out.println("Delete operation is failed");
	}
	public static synchronized Boolean isItNewerVersion(String filePath, String date){
		Boolean thisFileExistsOnServer=false;
		for(FileMetadata metadata:fileMetadataList)
			try {
				if(metadata.getFileDirectory().equals(filePath)){
					thisFileExistsOnServer=true;
					if(metadata.isOlderThan(date))
						return true;
					}
			} catch (ParseException e) {
				System.out.println("error while comparing date");
				return true;
			}
		if(thisFileExistsOnServer)
			return false;
		else return true;
	}

	public static synchronized ArrayList<FileMetadata>getFileMetadataList(){ return fileMetadataList; }
	public static synchronized void loadMetadataListFromFile(String fileName){
		try {
			FileInputStream fileIn=new FileInputStream(fileName);
			ObjectInputStream in=new ObjectInputStream(fileIn);
			fileMetadataList=(ArrayList<FileMetadata>)in.readObject();
			
		} catch(EOFException e){
			saveMetadataList(Config.getProperty("serializedMetadataFile"));
		}
		catch (FileNotFoundException e) {
			System.out.println("file: "+Config.getProperty("serializedMetadataFile")+" not found");
		} catch (IOException e) {
			System.out.println("An IOException has occurred while reading metadata from file");
		} catch (ClassNotFoundException e) {
			System.out.println(Config.getProperty("serializedMetadataFile")+" is invalid.");
		}
	}
	public static synchronized String assignName() {
		String assignedName=Config.prepareNextFileName();
		return assignedName;
		
		
	}
	public static void stopArchivization(FileMetadata metadata) {
		fileMetadataList.remove(metadata);
		removeFile(metadata);
	}
	
	private static void removeFile(FileMetadata metadata){
		metadata.removeCorrespondingFile();
	}
}
