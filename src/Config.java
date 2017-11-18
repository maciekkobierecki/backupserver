import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static File configFile;
	private static Properties prop;
	private static FileOutputStream outStream;
	private static final int FILE_NUMBER_POSITION=4;
	public static void init(){
		configFile=new File("config.properties");
		try {
			FileReader reader=new FileReader(configFile);
			prop=new Properties();
			prop.load(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Config file not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("err");
		}
		
	}
	public static synchronized String getProperty(String key){
		return prop.getProperty(key);
	}
	private static void openFileStream() throws FileNotFoundException{
		outStream=new FileOutputStream("config.properties");
	}
	public static String getLastFileName() {
		return getProperty("lastFileName");
	}
	public static synchronized String prepareNextFileName() {
		String name=getProperty("lastFileName");
		int fileNumber=getFileNumber(name);
		fileNumber=increment(fileNumber);
		String newFileName=createFileName(fileNumber);
		prop.setProperty("lastFileName", newFileName);
		updateFile();
		return newFileName;
	}
	private static synchronized void updateFile(){
		try {
			openFileStream();
			prop.store(outStream, null);
			outStream.close();
			
		} catch (IOException e) {
			System.out.println("file config.properties not found");
			System.exit(0);
		}
	}
	private static String createFileName(int fileId){
		return "file"+Integer.toString(fileId);
	}
	private static int getFileNumber(String fileName){
		String fileId=fileName.substring(FILE_NUMBER_POSITION);
		int number=Integer.parseInt(fileId);
		return number;
	}
	private static int increment(int fileName){
		return ++fileName;
	}
	

}
