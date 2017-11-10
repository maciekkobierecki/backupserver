package logServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static File configFile;
	private static Properties prop;
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
	public static String getProperty(String key){
		return prop.getProperty(key);
	}
	public static void incrementLastClientId(){
		try {
			int lastId=Integer.parseInt(prop.getProperty("id"));
			lastId++;
			prop.setProperty("id", Integer.toString(lastId));
			FileOutputStream out = new FileOutputStream("Config.properties");
			prop.store(out,null);
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("config.properties file not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
