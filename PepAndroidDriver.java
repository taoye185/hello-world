import static org.junit.Assert.assertNotEquals;
import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.testng.Assert;
import com.opencsv.CSVReader;
import bsh.This;
import io.appium.java_client.android.*;

public class PepAndroidDriver<T extends WebElement> extends AndroidDriver<T> {
/* This class extends the basic android driver and adds the following variables/methods to the 
 * automation testing specifically designed for mobeewave testing. It mighted be best suited to actually 
 * rename this class to MobeeWaveAndroidDriver, and have new classes such as PepAndroidDiver/CBAAndroidDriver 
 * etc extends this class. For now though, we'll make do with one single class. 
 * 
 * pElement: a mapping between all the interesting WebElements and their XPath. Defined by the 
 * configuration file that's specific to each apk. Note the configuration file is currently partially complete
 * and needs to be maintained and expanded in the future.
 * 
 * public PepAndroidDriver (URL, DesiredCapabilities, String): a constructor method to read the configuration 
 * files for the above mentioned pElement mapping
 * 
 * merchantSignIn: This method detects whether the Merchant Signin page is active, and input the merchant ID
 * parameter if positive.
 * 
 * merchantPassword: This method detects whether the Merchant Password page is active, and input the password
 * parameter if positive.
 * 
 * enterPIN: This method detects whether the Enter PIN page is active, and input the PIN one digit at a time 
 * if positive.
 * 
 * enterEmptyPIN: This method detects whether the Enter PIN page is active, and click continue without enter a
 * PIN if positive.
 * 
 * clickNext: This method click next on the purchase description window - to be expanded into input descriptions 
 * later
 * 
 * 
 * 
 */
	
	private Map pElement = new HashMap();
	private String elementName;
	private String elementPath;
	
	public PepAndroidDriver(URL url, DesiredCapabilities capabilities, String elementConfig) {
		super(url,capabilities);

		// TODO Auto-generated constructor stub
		try { 
	        FileReader filereader = new FileReader(elementConfig);  //reads the elementConfig configuration file. the ID field of the file are assumed to be unique
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextRecord; 
	        int i = 0;
	        while ((nextRecord = csvReader.readNext()) != null) { 
	        	int columnCount = 1;
	            for (String cell : nextRecord) { 
	            	if (columnCount == 1) {	
	            		elementName = cell; //it is assumed that the first column of the config file contains a unique ID
	            	}
	            	else if (columnCount == 2) {
	            		elementPath = cell;//it is assumed that the second column of the config file contains the element XPath
	            		if (i>0) {
	            		pElement.put(elementName, elementPath); //unless this is title row, set the WebElement Name&XPath mapping
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
	}
	
	public void testScenarioConstructor (String methodName, String[] parameters) throws InterruptedException {
			int waitSec = 5;
		switch (methodName) {
			case "merchantSignIn": this.merchantSignin(waitSec, parameters[1]);
			case "merchantPassword": this.merchantPassword(waitSec, parameters[1]);
			case "enterPIN": this.enterPIN(waitSec, parameters[1]);
			case "enterEmptyPIN": this.enterEmptyPIN(waitSec);
			case "clickNext": this.clickNext(waitSec);
			case "ShowSideMenu": this.showSideMenu(waitSec);
			default: System.out.println(methodName + " not found. No such method exists.");
		}
	}
	
	public void merchantSignin(int waitSec, String ID) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			ID is not empty,
 * 			MerchantSignInID is accurately defined in configuration file,
 * 			MerchantSignInOK is accurately defined in configuration file.
 * Post: 	If the current page is not Merchant Signin page, do nothing,
 * 			otherwise input the merchant id as specified by the parameter "ID".
 */
		Thread.sleep(waitSec * 1000);	//wait the current page to load
		try {
			this.findElementByXPath((String) pElement.get("MerchantSignInID")).sendKeys(ID); //input merchant ID
			System.out.println("merchant id element found");
			this.findElementByXPath((String) pElement.get("MerchantSignInOK")).click(); // click OK
			System.out.println("Sign in button clicked");

			}catch(Exception e) {
			System.out.println("exception caught");
			}
	}
	
	public void merchantPassword(int waitSec, String password) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			password is not empty,
 * 			MerchantPasswordpassword is accurately defined in configuration file,
 * 			MerchantPasswordContinue is accurately defined in configuration file.
 * Post: 	If the current page is not Merchant Password page, do nothing,
 * 			otherwise input the merchant password as specified by the parameter "password".
 */		
		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
		this.findElementByXPath((String) pElement.get("MerchantPasswordpassword")).sendKeys(password); //input password
		System.out.println("merchant password element found");
		this.findElementByXPath((String) pElement.get("MerchantPasswordContinue")).click(); // click OK
		System.out.println("continue button clicked");	
		}
		catch(Exception e) {
		System.out.println("exception caught");
		}
		
	}
	
	
	public void enterEmptyPIN(int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
 * Post: 	If the current page is not Enter PIN page, do nothing,
 * 			otherwise try to click OK without entering the PIN and verify whether the correct validation is
 * 			present.
 */		
		
		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
			this.findElementByXPath("*[contains(@text,'6-digit')]"); //is validation message already present?
			Assert.assertFalse(true);//If the validation message is already present without any action, it is a bug.
		}
		catch (Exception e) {
			System.out.println("exception caught");
		}
		
		try {
			this.findElementByXPath((String) pElement.get("EnterPINOK")).click(); //Click OK without entering PIN
			Thread.sleep(500);   //very important otherwise the validation message does not appear in time
			Assert.assertEquals(this.findElementByXPath("*[contains(@text,'6-digit')]").getText(), "Please enter a 6-digit PIN"); 
			//If the correct validation messages appears, test passed
		}
		catch (Exception e) {
			System.out.println("exception caught");
		}
	}
	
	
	public void enterPIN(int waitSec, String PIN) throws InterruptedException {
		Thread.sleep(waitSec * 1000);

		try {
		int len = PIN.length();
		int i = 0;
		PIN.charAt(i);
		for (i=0; i < len; i++ ) {
			System.out.println(PIN.charAt(i));
			this.findElementByXPath((String) pElement.get("EnterPIN"+(i+1))).click(); //Enter each digit of the PIN
			System.out.println(PIN.charAt(i)+ " button clicked");		
		}
		System.out.println("next to click OK button");	
		this.findElementByXPath((String) pElement.get("EnterPINOK")).click(); //Click OK
		System.out.println("OK button clicked");	
		}catch(Exception e) {
			System.out.println("exception caught");
		}
	}

	public void clickNext(int waitSec) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		try {
		this.findElementByXPath((String) pElement.get("PurchaseDescriptionNext")).click();
		}
		catch (Exception e) {
			System.out.println("exception caught");
		}
	}
	
	public void showSideMenu (int waitSec) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		try {
		System.out.print("before side menu");
		this.findElementByXPath((String) pElement.get("SideMenuShowMenu")).click();
		System.out.print("after side menu");
		}
		catch (Exception e) {
			System.out.println("exception caught");
		}
	}
}
