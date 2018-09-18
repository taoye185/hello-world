import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;


public class pseudoMain{
	public static MWAndroidDriver<?> mobiledriver;
	static String casefile = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\base test case config.csv"; //configuration file to construct test cases
	static String file = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\config.csv"; //Configuration file to set capabilities. notice the parameter shouldn't have space after commas as of current version.
	static String elementFile = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\app elements.csv"; //Configuration file for app element xpath.

	@BeforeTest
	public void beforeTest( ) throws MalformedURLException {
		String[] capNames = new String[10];
		String[] capValues = new String[10];
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, "1.9.0");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "8.0.0");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Samsung Galaxy S9");
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("newCommandTimeout", 2000);
		try { 
		        FileReader filereader = new FileReader(file); 
		        CSVReader csvReader = new CSVReader(filereader); 
		        String[] nextRecord; 
		        int i = 0;
		        // we are going to read data line by line 
		        while ((nextRecord = csvReader.readNext()) != null) { 
		        	int columnCount = 1;
		            for (String cell : nextRecord) { 
		            	if (columnCount == 1) {	
		            		capNames[i] = cell; //it is assumed that the first column of the config file contains capabilities name
		            	}
		            	else if (columnCount == 2) {
		            		capValues[i] = cell;//it is assumed that the second column of the config file contains capabilities value
		            		if (i>0) {
		            		capabilities.setCapability(capNames[i], capValues[i]); //unless this is title row, set the capabilities
		            		}
		            	}
		            	else {
		            		  	//it is assumed that the 3rd column (or later) of the config file contains garbage
		            	}
		                columnCount ++;
		            } 
		            i++;
		        } 
		        csvReader.close();
		    } 
		    catch (Exception e) { 
		        e.printStackTrace(); 
		    } 		
		
		mobiledriver = new MWAndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities, elementFile);

	}

	@AfterTest
	public void afterTest( ){
//		mobiledriver.quit();
	}

	
	@Test
	public static void launchApp() throws InterruptedException{
		// read from casefile and construct the respective test case methods
		try { 
	        FileReader filereader = new FileReader(casefile); 
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextRecord; 
	        String methodName = "";
	        String[] methodParameters = new String[10];
	        int i = 0;

	        while ((nextRecord = csvReader.readNext()) != null) { 
	        	int columnCount = 1;
	            for (String cell : nextRecord) { 
	            	if (columnCount == 1) {	
	            		methodName = cell; 
	            		//it is assumed that the first column of the config file contains method name to be called
	            	}
	            	else {
	            		methodParameters[columnCount-2] = cell;
	            		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
	            	}

	                columnCount ++;
	            } 
	            if (i > 0) {
	            mobiledriver.testScenarioConstructor(methodName, methodParameters); 
	            //unless it is title row, construct and execute the method
	            }
	            i++;
	        } 
	        csvReader.close();
	    } 
	    catch (Exception e) { 
	        e.printStackTrace(); 
	    } 	
	
	
	
	}
	
	
	
	
	
}