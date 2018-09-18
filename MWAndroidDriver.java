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

public class MWAndroidDriver<T extends WebElement> extends AndroidDriver<T> {
/* This class extends the basic android driver and adds the following variables/methods to the 
 * automation testing specifically designed for mobeewave testing. 
 * 
 * pElement: a mapping between all the interesting WebElements and their XPath. Defined by the 
 * configuration file that's specific to each apk. Note the configuration file is currently partially complete
 * and needs to be maintained and expanded in the future.
 * 
 * public MWAndroidDriver (URL, DesiredCapabilities, String): a constructor method to read the configuration 
 * files for the above mentioned pElement mapping
 * 
 * testScenarioConstructor: This method will dynamically decide which specific method is going to be revoked 
 * depending on the parameters passed in.
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
 * clickButton: This method clicks a URI specified by the parameter - use this method when no validation or
 * assertions are needed for a simple step
 * 
 */
	
	private Map<String, String> pElement = new HashMap<String, String>();
	private String elementName;
	private String elementPath;
	
	public MWAndroidDriver(URL url, DesiredCapabilities capabilities, String elementConfig) {
/* Pre: elementConfig is the path of a configuration file, listing all relevant URIs for the test project
 * Post: a MWAndroidDriver is constructed, while an URI-XPath mapping is established for all the relevant
 * 		elements
 */
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
/* Pre: parameters[0] is a string that can be parsed into a non-negative integer
 * Post: a method corresponding to the methodName variable is called and executed. 
 */
		int waitSec = Integer.parseInt(parameters[0]);
		System.out.println("The method " +methodName + " with parameter " + parameters[0] + " and " + parameters[1] + " are called");
		switch (methodName) {
			case "merchantSignin": {this.merchantSignin(waitSec, parameters[1]); break;}
			case "merchantPassword": {this.merchantPassword(waitSec, parameters[1]);break;}
			case "enterPIN": {this.enterPIN(waitSec, parameters[1]);break;}
			case "enterEmptyPIN": {this.enterEmptyPIN(waitSec);break;}
			case "clickNext": {this.clickNext(waitSec);break;}
			case "showSideMenu": {this.showSideMenu(waitSec);break;}
			case "clickButton": {this.clickButton(waitSec, parameters[1]);break;}
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
			this.findElementByXPath(pElement.get("MerchantSignInID")).sendKeys(ID); //input merchant ID
			System.out.println("merchant id element found");
			this.findElementByXPath(pElement.get("MerchantSignInOK")).click(); // click OK
			System.out.println("Sign in button clicked");

			}catch(Exception e) {
			System.out.println("exception caught, not on Merchant Sign IN page");
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
		this.findElementByXPath(pElement.get("MerchantPasswordpassword")).sendKeys(password); //input password
		this.findElementByXPath(pElement.get("MerchantPasswordContinue")).click(); // click OK
		}
		catch(Exception e) {
		System.out.println("exception caught, not on Merchant Password page.");
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
			System.out.println("exception caught, validation message doesn't exist");
		}
		
		try {
			this.findElementByXPath(pElement.get("EnterPINOK")).click(); //Click OK without entering PIN
			Thread.sleep(500);   //very important otherwise the validation message does not appear in time
			Assert.assertEquals(this.findElementByXPath("*[contains(@text,'6-digit')]").getText(), "Please enter a 6-digit PIN"); 
			//If the correct validation messages appears, test passed
		}
		catch (Exception e) {
			System.out.println("exception caught, not on PIN entering page.");
		}
	}
	
	
	public void enterPIN(int waitSec, String PIN) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
 * Post: 	If the current page is not Enter PIN page, do nothing,
 * 			otherwise try enter the PIN digits one by one then click OK.
 */	
		Thread.sleep(waitSec * 1000); //wait the current page to load

		try {
		int len = PIN.length();
		int i = 0;
		PIN.charAt(i);
		for (i=0; i < len; i++ ) {
			this.findElementByXPath(pElement.get("EnterPIN"+(i+1))).click(); //Enter each digit of the PIN
		}
		this.findElementByXPath(pElement.get("EnterPINOK")).click(); //Click OK

		}catch(Exception e) {
			System.out.println("exception caught, not on PIN entering page.");
		}
	}

	public void clickNext(int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * Post: 	If the current page is not a pop-up asking user to enter information then click next,
 * 			do nothing, otherwise click next and proceed.
 */	
		Thread.sleep(waitSec * 1000);//wait the current page to load
		try {
		this.findElementByXPath(pElement.get("PurchaseDescriptionNext")).click();
		}
		catch (Exception e) {
			System.out.println("exception caught, Next button not found");
		}
	}
	
	public void showSideMenu (int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * Post: 	The Side Menu is displayed.
 */	
		Thread.sleep(waitSec * 1000);
		try {
		System.out.print("before side menu");
		this.findElementByXPath(pElement.get("SideMenuShowMenu")).click();
		System.out.print("after side menu");
		}
		catch (Exception e) {
			System.out.println("exception caught, can't find Show Side Menu button.");
		}
	}
	
	public void clickButton (int waitSec, String URI) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, URI are accurately defined in the configuration file
 * Post: 	The corresponding button is clicked.
 * 			use this method when no validation or boundary cases are expected
 */			
		Thread.sleep(waitSec * 1000);
		try {
		this.findElementByXPath(pElement.get(URI)).click();
		}
		catch (Exception e) {
			System.out.println("exception caught, failed to find " + URI);
		}
	}
}
