package com.harquin.codegenerator;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.harquin.files.FileUtils;

public class CodeGenerator {
	
	private static final String inFilePrefix = "./src/main/resources/";
	private static final String outfilePrefix = "C:/Users/Amandla Blue-Ashley/Documents/Harquin/Software Consulting/Wound Care App/src/main/java/com/harquin/common/";
	private static final String outFileTestPrefix = "C:/Users/Amandla Blue-Ashley/Documents/Harquin/Software Consulting/Wound Care App/src/test/java/com/harquin/common/";
	private static final String outFileTestConfigPrefix = "C:/Users/Amandla Blue-Ashley/Documents/Harquin/Software Consulting/Wound Care App/src/test/java/com/harquin/config/";
	private static final String outFileConfigPrefix = "C:/Users/Amandla Blue-Ashley/Documents/Harquin/Software Consulting/Wound Care App/src/main/java/com/harquin/config/";
	
	public static void main(String[] args){
		//Read in CSV
		Iterable<CSVRecord> records = getCsvRecords(inFilePrefix + "daoCsv.csv");
		for (CSVRecord record : records) {
		    String objectName = record.get("objectName");
		    String lowerCaseParameterName = record.get("lowerCaseParameterName");
		    String packageName = record.get("packageName");
		    String tableName = record.get("tableName");
		    
		    //make test pacakge
		    new File(outFileTestPrefix + packageName).mkdir();
		    
		    //DaoTest Template
		    makeConcreteFile(inFilePrefix + "DaoTestTemplate.txt", outFileTestPrefix + packageName + "/" + objectName + "DaoTest.java", record);
		    
		    //make main package
		    new File(outfilePrefix + packageName).mkdir();
		    
		    //POJO Template
		    makeConcreteFile(inFilePrefix + "POJOTemplate.txt", outfilePrefix + packageName + "/" + objectName + ".java", record);
		    
		    //IDao Template
		    makeConcreteFile(inFilePrefix + "IDaoTemplate.txt", outfilePrefix + packageName + "/" + "I" + objectName + "Dao.java", record);
		    
		    //Read in DaoTemplate
		    //Write ObjectNameDao.java
		    makeConcreteFile(inFilePrefix + "DaoTemplate.txt", outfilePrefix + packageName + "/" + objectName + "Dao.java", record);
		    //Read in MapperTemplate
		    //Write o
		    makeConcreteFile(inFilePrefix + "MapperTemplate.txt", outfilePrefix + packageName + "/" + objectName + "Mapper.java", record);
		    //Read in XMlTemplate
		    //Write ObjectNameMapper.xml
		    makeConcreteFile(inFilePrefix + "XmlTemplate.txt", outfilePrefix + packageName + "/" + objectName + "Mapper.xml", record);
		}

		//read in template
		List<String> templateFileLines = FileUtils.readInTextFile(inFilePrefix + "DataConfigTemplate.txt");
		for (CSVRecord record : records)
		{			
			//convert template to concrete
			List<String> linesToAppend = createNewFileFromTemplate(templateFileLines, record);
			//add to end of file
			FileUtils.appendTextToFile(outFileTestConfigPrefix + "TestDataConfig.java", linesToAppend);
		}
		
		for (CSVRecord record : records)
		{			
			//convert template to concrete
			List<String> linesToAppend = createNewFileFromTemplate(templateFileLines, record);
			//add to end of file
			FileUtils.appendTextToFile(outFileConfigPrefix + "DataConfig.java", linesToAppend);
		}
	}

	
	private static void makeConcreteFile(String templateName, String finalFileName, CSVRecord record)
	{
		//Read in IDao Template
	    List<String> linesOfFile = FileUtils.readInTextFile(templateName);
		//For each line as you read in DAO template, see if it has special marker
	  //For each special marker in line, replace with proper field from csv

	    List<String> newFileLines = createNewFileFromTemplate(linesOfFile, record);

		//Write out new Dao to IObjectNameDao.java
	    for (String line : newFileLines)
	    {
	    	System.out.println(line);		    	
	    }
	    
	    FileUtils.writeTextToFile(finalFileName, newFileLines);
	}
	
	private static List<String> createNewFileFromTemplate(List<String> templateFile, CSVRecord record)
	{
		List<String> newFileLines = new ArrayList<>();
		
	    String objectName = record.get("objectName");
	    String lowerCaseParameterName = record.get("lowerCaseParameterName");
	    String packageName = record.get("packageName");
	    String tableName = record.get("tableName");
		
	    for (String line : templateFile)
	    {
	    	System.out.println(line);
	    	
	    	String newLine;
    	
    		newLine = line.replace("%o", objectName);
    	
    		newLine = newLine.replace("%l", lowerCaseParameterName);
    	
    		newLine = newLine.replace("%p", packageName);
    	
    		newLine = newLine.replace("%t", tableName);
    	
    		newFileLines.add(newLine);
	    }
	    
	     return newFileLines;
	}
	
	private static Iterable<CSVRecord> getCsvRecords(String fileName)
	{
		Iterable<CSVRecord> records = null;
		
		try 
		{
			Reader in = new FileReader(fileName);
			records = CSVFormat.EXCEL.withHeader("objectName", "lowerCaseParameterName", "packageName", "tableName").withSkipHeaderRecord().parse(in);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return records;
	}
}
