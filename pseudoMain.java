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
	public static PepAndroidDriver<?> mobiledriver;

	@BeforeTest
	public void beforeTest( ) throws MalformedURLException {
		String[] capNames = new String[10];
		String[] capValues = new String[10];
		String casefile = "configuration file for test cases to be executed. yet to be implemented.";
		String file = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\config.csv"; //Configuration file to set capabilities. notice the parameter shouldn't have space after commas as of current version.
		String elementFile = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\app elements.csv"; //Configuration file for app element xpath.
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
//		            		System.out.println(cell + " read, " + capNames[i] + " saved in capname");
		            	}
		            	else if (columnCount == 2) {
		            		capValues[i] = cell;//it is assumed that the second column of the config file contains capabilities value
//		            		System.out.println(cell + " read, " + capValues[i] + " saved in capvalue");
		            		if (i>0) {
		            		capabilities.setCapability(capNames[i], capValues[i]); //unless this is title row, set the capabilities
		            		}
		            	}
		            	else {
//		            		  System.out.println("column " + columnCount + ", don't know what to do."); 
		            		  	//it is assumed that the 3rd column (or later) of the config file contains garbage
		            	}
		                columnCount ++;
		            } 
//		            System.out.println(); 
		            i++;
		        } 
		        csvReader.close();
		    } 
		    catch (Exception e) { 
		        e.printStackTrace(); 
		    } 		
		
		mobiledriver = new PepAndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities, elementFile);

	}

	@AfterTest
	public void afterTest( ){
//		mobiledriver.quit();
	}

	
	@Test
	public static void launchApp() throws InterruptedException{
		mobiledriver.merchantSignin(5,"005040100000004");		//wait for 5 seconds, then input Merchant ID and click OK
		mobiledriver.merchantPassword(7,"6Z214UU53");
//		mobiledriver.enterEmptyPIN(5);
		mobiledriver.enterPIN(5, "123456");
		mobiledriver.showSideMenu(5);
//		mobiledriver.enterPIN(5, "1234"); //this is actually making purchase of $12.34;
//		mobiledriver.clickNext(5);
		//		Assert.assertEquals(mobiledriver.findElementById("toolbar_title").getText(), "Merchant Registration");

//		Assert.assertEquals(mobiledriver.getTitle(), "Appium: Mobile App Automation Made Awesome.", "Title Mismatch");
	}
	
	
	
	
	
}