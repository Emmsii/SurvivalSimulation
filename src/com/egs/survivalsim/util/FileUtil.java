package com.egs.survivalsim.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FileUtil {
	
	public boolean below10 = true;
	
	public File file;
	public File file10;

	public void start(String value) throws FileNotFoundException, IOException{
		
		
	}
	
	public void createLog() throws IOException{
		int numberOfLogs = new File("logs/").listFiles().length + 1;
		file = new File("logs/log_0" + (numberOfLogs) + ".txt");
		file10 = new File("logs/log_" + (numberOfLogs) + ".txt");
			
		if(numberOfLogs >= 10){
			below10 = false;
		}else{
			below10 = true;
		}
		
		if(!file.exists() && below10){
			file.createNewFile();
		}else if(!file10.exists() && !below10){
			file10.createNewFile();
		}
		
	}
	
	public void addLog(String value) throws IOException{
		if(below10){
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter buffer = new BufferedWriter(fileWriter);
			PrintWriter printWriter = new PrintWriter(buffer);
			printWriter.println(value);
			printWriter.close();
		}else if(!below10){
			FileWriter fileWriter = new FileWriter(file10, true);
			BufferedWriter buffer = new BufferedWriter(fileWriter);
			PrintWriter printWriter = new PrintWriter(buffer);
			printWriter.println(value);
			printWriter.close();
		}
	}

}
