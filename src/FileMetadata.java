import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileMetadata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -994332227976061506L;
	private String function;
	private File file;
	private String date;
	
	public FileMetadata(String function, String fileDirectory, String date){
		this.function=function;
		file=new File(fileDirectory);
		this.date=date;
	}
	public String getFunction(){ return function; }
	public String getFileDirectory() { return file.getAbsolutePath(); }
	public String getDate() { return date; }
	public String getFileName(){ return file.getName(); }
	public String getFileExtension(){
		String fileName=getFileName();
		String[] data=fileName.split("\\.");
		return data[1];
	}
	//date format must be dd/MM/yyyy HH:ss:mm
	public Boolean isOlderThan(String date) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:ss:mm");
		Date myDate=sdf.parse(this.date);
		Date foreignDate=sdf.parse(date);
		if(myDate.compareTo(foreignDate)<0)
			return true;
		else 
			return false;
			
	}
}
