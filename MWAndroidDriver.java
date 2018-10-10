import static org.junit.Assert.assertNotEquals;
import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.testng.Assert;
import org.testng.Reporter;

import com.opencsv.CSVReader;


import bsh.This;
import io.appium.java_client.android.*;

import java.util.logging.Level;
import java.util.logging.Logger;


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
	private Map<String, mobilePage> pageName = new HashMap<String, mobilePage>();
	private String elementName;
	private String elementPath;
//	private Logger testLog = Logger.getLogger("log test.txt");
	private Vector pages = new Vector();
	
	
	public MWAndroidDriver(URL url, DesiredCapabilities capabilities, String elementConfig) {
/* Pre: elementConfig is the path of a configuration file, listing all relevant URIs for the test project
 * Post: a MWAndroidDriver is constructed, while an URI-XPath mapping is established for all the relevant
 * 		elements
 */
		super(url,capabilities);
//		System.out.println(getClass().getClassLoader().getResource("logging.properties"));
		// TODO Auto-generated constructor stub
		try { 
	        FileReader filereader = new FileReader(elementConfig);  //reads the elementConfig configuration file. the ID field of the file are assumed to be unique
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextRecord; 
	        int i = 0;
	        while ((nextRecord = csvReader.readNext()) != null) { 
	        	if(i>0) {
	        		pElement.put(nextRecord[0], nextRecord[1]);
	        		//establish the ElementName->ElementXPath mapping
	        		if (pageName.get(nextRecord[2])==null) {
	        			mobilePage newPage = new mobilePage(nextRecord[2], nextRecord[0]);
	        			pages.addElement(newPage); //record the different pages exists under the app to be tested
	        			pageName.put(nextRecord[2], newPage);
	        			//establish the PageName -> mobilePage object mapping
	        		}
	        		else {
	        			pageName.get(nextRecord[2]).addElement(nextRecord[0]);
	        			// if a PageName already exist, simply record the element under the existing page
	        		}
        			if (nextRecord[5].equals("yes")) {
        				pageName.get(nextRecord[2]).setUniqueID(nextRecord[0], nextRecord[6]);
        				//If an element is identified as a "unique identifier" for a page, record this 
        				//information by set the Unique ID and Unique Value variables for the mobilePage
//        				System.out.println( nextRecord[0]+" is a unique element with value: " + nextRecord[6]);
        			}
//	        		System.out.println(pageName.get(nextRecord[2]).getPageName());
//	        		System.out.println(pageName.get(nextRecord[2]).getAllElements().toString());
	        	}
	            i++;
	        } 
	        csvReader.close();
	    } 
	    catch (Exception e) { 
	    	//this.logerror(e);
	    	e.printStackTrace(); 
	    } 		
	}
	
	public void testScenarioConstructor (String[] parameters) throws InterruptedException {
/* Pre: parameters[0] is a string that can be parsed into a non-negative integer
 * Post: a method corresponding to the methodName variable is called and executed. 
 * 		Data validation would be needed for this method which is not implemented yet.
 */
		int waitSec = Integer.parseInt(parameters[1]);
		System.out.println("The method \"" +parameters[0] + " ("  + ")\" are called");
		switch (parameters[0]) {
			case "merchantSignin": {this.merchantSignin(waitSec, parameters[2]); break;}
			case "merchantPassword": {this.merchantPassword(waitSec, parameters[2]);break;}
			case "enterNumPad": {this.enterNumPad(waitSec, parameters[2]);break;}
			case "enterEmptyPIN": {this.enterEmptyPIN(waitSec);break;}
			case "clickNext": {this.clickNext(waitSec);break;}
			case "showSideMenu": {this.showSideMenu(waitSec);break;}
			case "clickButton": {this.clickButton(waitSec, parameters[2]);break;}
			case "checkPageOverlap": {this.checkPageOverlap(waitSec, parameters[2]);break;}
			case "launch": {this.launch(waitSec);break;}
			case "login": {this.login(waitSec); break;}
			case "multiplePurchase": {this.multiplePurchase(waitSec); break;}
			case "test": {this.test(waitSec, parameters[5], parameters[6], parameters[7]);break;}
			//notice for multiplePurchase, variable waitSec represents number of purchases to be made, not seconds to wait
			default: System.out.println(parameters[0] + " not found. No such method exists.");
		}
	}
	

	
	//The next 3 methods are the basic unit functions of this class (click/enter text/clear text) that forms the basis all other 
	//more complicated functions. More (for example, scrool up/down) basic functions are to be added as needed	
		public void clickButton (int waitSec, String URI) throws InterruptedException {
	/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file
	 * Post: 	The corresponding button is clicked.
	 * 			use this method when no validation or boundary cases are expected
	 */			
			Thread.sleep(waitSec * 1000);
			try {
			this.findElementByXPath(pElement.get(URI)).click();
			}
			catch (Exception e) {
				System.out.println("exception caught during button click, failed to find " + URI);
			}
		}
		
		public void inputText (int waitSec, String URI, String input ) throws InterruptedException {
	/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file.
	 * 			input can be empty but shoudn't be null
	 * Post: 	The input is typed into the field.
	 * 			use this method when no validation or boundary cases are expected
	 */			
			Thread.sleep(waitSec * 1000);
			try {
			this.findElementByXPath(pElement.get(URI)).sendKeys(input);
			}
			catch (Exception e) {
				System.out.println("exception caught during text input, failed to find " + URI);
			}
		}
		
		public void clearText(int waitSec, String URI) throws InterruptedException {
	/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file
	 * Post: 	The text field is cleared.
	 * 			use this method when no validation or boundary cases are expected
	 */			
			Thread.sleep(waitSec * 1000);
			try {
			this.findElementByXPath(pElement.get(URI)).clear();
			}
			catch (Exception e) {
				System.out.println("exception caught during text clearance, failed to find " + URI);
			}
		}
		
		
		
		
//The next set of functions uses the basic functions above to construct frequently used functions on the page level	
	public void merchantSignin(int waitSec, String ID) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			ID is not empty,
 * 			MerchantSignInID is accurately defined in configuration file,
 * 			MerchantSignInOK is accurately defined in configuration file.
 * Post: 	If the current page is not Merchant Signin page, do nothing,
 * 			otherwise input the merchant id as specified by the parameter "ID".
 */
//		Thread.sleep(waitSec * 1000);	//wait the current page to load
		try {
//			this.findElementByXPath(pElement.get("MerchantSignInID")).sendKeys(ID); 
			this.inputText(waitSec, "MerchantSignInID", ID); //input merchant ID
//			System.out.println("merchant id element found");
//			this.findElementByXPath(pElement.get("MerchantSignInOK")).click(); 
			this.clickButton(0,"MerchantSignInOK" ); // click OK
//			System.out.println("Sign in button clicked");
			}catch(Exception e) {
//			this.logMessage("exception caught, not on Merchant Sign IN page");
	    	//this.logerror(e);
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
//		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
//		this.findElementByXPath(pElement.get("MerchantPasswordpassword")).sendKeys(password); 
//		this.findElementByXPath(pElement.get("MerchantPasswordContinue")).click(); // click OK
		this.inputText(waitSec, "MerchantPasswordpassword", password);//input password
		this.clickButton(0,"MerchantPasswordContinue" ); // click OK
		}
		catch(Exception e) {
//			this.logMessage("exception caught, not on Merchant Password page.");
	    	//this.logerror(e);		
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
//			this.logMessage("exception caught, validation message doesn't exist");
	    	//this.logerror(e);	
		}
		try {
//			this.findElementByXPath(pElement.get("EnterPINOK")).click(); 
			this.clickButton(0, "EnterPINOK");//Click OK without entering PIN
			Thread.sleep(500);   //very important otherwise the validation message does not appear in time
			Assert.assertEquals(this.findElementByXPath("*[contains(@text,'6-digit')]").getText(), "Please enter a 6-digit PIN"); 
			//If the correct validation messages appears, test passed
		}
		catch (Exception e) {
//			this.logMessage("exception caught, not on PIN entering page.");
	    	//this.logerror(e);	
		}
	}
	
	
	public void enterNumPad(int waitSec, String PIN) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
 * Post: 	If the current page is not Enter PIN page, do nothing,
 * 			otherwise try enter the PIN digits one by one then click OK.
 */	
		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
			System.out.println("Number to be entered is: " + PIN);
			for (int i=0; i < PIN.length(); i++ ) {
				int digit = Character.getNumericValue(PIN.charAt(i));
				this.findElementByXPath(pElement.get("EnterPIN"+digit)).click(); //Enter each digit of the PIN
			}
			this.findElementByXPath(pElement.get("EnterPINOK")).click(); //Click OK
		}
		catch(Exception e) {
//			this.logMessage("exception caught, not on PIN entering page.");
	    	//this.logerror(e);
		}
	}

	public void clickNext(int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * Post: 	If the current page is not a pop-up asking user to enter information then click next,
 * 			do nothing, otherwise click next and proceed.
 */	
//		Thread.sleep(waitSec * 1000);//wait the current page to load
		try {
//			this.findElementByXPath(pElement.get("PurchaseDescriptionNext")).click();
			this.clickButton(waitSec, "PurchaseDescriptionNext");
		}
		catch (Exception e) {
//			this.logMessage("exception caught, Next button not found");
	    	//this.logerror(e);
		}
	}
	
	public void showSideMenu (int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * Post: 	The Side Menu is displayed.
 */	
		this.clickButton(waitSec, "SideMenuShowMenu"); 
	}


	public boolean test(int waitSec, String method, String fieldName, String expectedValue) throws InterruptedException {
		Thread.sleep(waitSec*1000);
		boolean testResult = false;
		String actualValue = "";
				switch (method) {
			case "equal": {
				actualValue = this.findElementByXPath(pElement.get(fieldName)).getText();
				return this.logTestResult("Current page", actualValue, expectedValue);
			}
			case "isOnPage": {
				actualValue = this.findCurrentPage(0);
				return this.logTestResult("Current page", actualValue, expectedValue);
			}
			default: {
				testResult = false;
				Reporter.log(method + " is not a defined test methodology, no test was conducted.");
				return testResult;	
			}
		}
	}
	

	public boolean logTestResult (String fieldName, String actualValue, String expectedValue) {
		boolean testResult = false;
		Assert.assertEquals(actualValue, expectedValue);
		if (actualValue.equals(expectedValue)) {
			testResult = true;
			Reporter.log(fieldName + " is equal to " + expectedValue + ", test passed.");
			return testResult;
		}
		else {
			testResult = false;
			Reporter.log(fieldName + " is expected to be "+ expectedValue + ", but is actually equal to: " + actualValue+ ", test failed.");
			return testResult;
		}
	}
	
	
//The next set of functions (launch & findCurrentPage & multiplePurchase, etc) are attempts to implement "reproduce steps" and are not completed yet.
	public void launch (int waitSec) throws InterruptedException {
/* Pre: This function should be used when the test case requires the user to luanch the app and reach the login page
 * Post: login page reached without any assertion used
 */
//		Thread.sleep(waitSec * 1000);
		String currentPage = "";
		int iteration = 0;
		while ((!currentPage.equals("EnterPIN")) && (iteration<10)) {
			iteration ++;
			currentPage = this.findCurrentPage(waitSec); //decide what to do depending on which page the user is on
			switch (currentPage) {
				case "SignInSelection": {
					this.clickButton(0, (String) this.getCapabilities().getCapability("Username")); break;
					// Have multiple user to select from, select the user specified in Config file to advance to Enter PIN page
				}
				case "EnterPIN": {
					Reporter.log("App launched and awaiting for login.");
					// In Enter PIN page, do nothing
					break;
				}
				case "MerchantSignIn": {
					this.merchantSignin(0, (String) this.getCapabilities().getCapability("MerchantID"));
					this.merchantPassword(0, (String) this.getCapabilities().getCapability("MerchantPassword"));
					// First time merchant registration page, entering merchant credentials
					// potential problem: taking too long to register, needs to add more checks and logics later
					break;
				}
				case "PaymentAcceptanceSetup": {
					this.clickButton(0, "PaymentAcceptanceSetupContinue");break;
					// Provision page, click continue to start provision
				}
				case "ActivatingSecureElement": {
					Thread.sleep(30000);break;
					//Under Provisioning, wait for 30 seconds before rechecking progress
				}
				case "ProvisionComplete": {
					this.clickButton(0, "ProvisionCompleteDone");break;
					//Provision is done, proceed
				}
				case "Update required": {
					//to be implemented
				}
				default: {
					System.out.println("This page "+currentPage+ " is not recognized.");
					iteration=10;
					//current page not one of the above
				}
			}
		}

	}
	
	public String findCurrentPage(int waitSec) throws InterruptedException {
/* Pre: mobilePage has been initialized
 * This function is to be used as strictly a helper function to determine what page the app is currently displaying
 * Post: return the PageName associated with the current mobilePage
 */
		Thread.sleep(waitSec*1000);
		String currentPage = "";
		System.out.println("");
		System.out.print("Current Page Verification in progress");
		for (int i=0; i<pages.size(); i++) {
			try {
//				System.out.println("1");				
				String name = ((mobilePage) pages.elementAt(i)).getPageName();
//				System.out.println("name is: " + name);
				String id = ((mobilePage) pages.elementAt(i)).getUID();
//				System.out.println("id is: " + id);
				String value = ((mobilePage) pages.elementAt(i)).getUValue();
//				System.out.println("value is: " + value);
				if (!(id.isEmpty())) {
//					System.out.println("in first if");
					String title = this.findElementByXPath(pElement.get(id)).getText();
//					System.out.println(title);
					if (title.equals(value)) {
//						System.out.println("in 2nd if");
						System.out.println("Current page is: "+name);
						return name;
					}
				}
			}
			catch (Exception e) {
				System.out.print(".");
			}
		}
		System.out.println("Current page is: "+currentPage);
		return currentPage;
	}
	
	
	public void login(int waitSec) throws InterruptedException {
/*Pre: Username and MerchantPIN is appropriately defined in Config file
 * Post: user logs in with the username and PIN defined in config file
 */
		this.enterNumPad(waitSec, (String) this.getCapabilities().getCapability("MerchantPIN"));
		Reporter.log("User Login performed.");
	}
	
	public void multiplePurchase(int numPurchase) throws InterruptedException {
/*Pre: numPurchase is a positive integer
 * Post: multiple purchases are performed 
 */ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions

		int i = 0;
		int iteration = 0;
		int waitSec = 5;
		while ((i<numPurchase) && (iteration<numPurchase*3)) {
			String page = this.findCurrentPage(waitSec);
			switch (page) {
				case "Purchase" : {			
//					String amount = "1000";
					this.enterPurchaseAmount(0, "1000");
					System.out.println("");
					System.out.println("This is the " + (i+1)+"th purchase attempt");
					waitSec = 3;
					break;
				}
				case "TapToPay":{
					System.out.println("waiting for card tap");
					waitSec = 10;
					break;
				}
				case "ProcessingPayment": {
					System.out.println("processing payment");
					iteration ++;
					waitSec = 3;
					break;
				}
				case "PurchaseDescription": {
					this.clickButton(0, "PurchaseDescriptionNext");
					waitSec = 3;
					break;
				}
				case "PurchaseResult": {
					this.clickButton(0, "PurchaseResultNoReceipt");
//					this.clickButton(0, "PurchaseResultEmailReceipt");
					System.out.println("Purchase made.");
					Reporter.log("Transaction #"+(i+1)+" is made.");
					i++;
					waitSec = 2;
					break;
				}
				case "CardNotSupported": {
					this.clickButton(0, "CardNotSupportedTryAgain");
					System.out.println("Purchase made.");
					Reporter.log("Transaction #"+(i+1)+" is denied due to card not supported.");
					i++;
					waitSec = 3;
					break;
				}
				case "PurchaseNotCompleted": {
					this.clickButton(0, "PurchaseNotCompletedDone");
					System.out.println("Purchase made.");
					Reporter.log("Transaction #"+(i+1)+" is attempted but not completed.");
					i++;
					waitSec = 3;
					break;
				}
				case "ReceiptSentConfirmation": {
					this.clickButton(0, "ReceiptSentConfirmationDone"); 
					waitSec = 3;
					break;

				}
				
				case "NewTagScanned": {
					System.out.println("Pressing key code 82, still needs implementation");
//					this.pressKeyCode(82);
					System.out.println("Pressing AllAppListingCBADebug");
					this.clickButton(3, "AllAppListingCBADebug");
					waitSec = 5;
					break;
				}
				case "EmailReceiptForPurchase": {
					this.clearText(1, "EmailReceiptForPurchaseEmail");
					this.inputText(0, "EmailReceiptForPurchaseEmail", (String) this.getCapabilities().getCapability("CustomerEmail"));
					this.clickButton(0, "EmailReceiptForPurchaseNext");
					waitSec = 3;
					break;
				}
				
				default: {
//					iteration = numPurchase*3+1;
					System.out.println("reached an unrecognized page: " + page);
					this.clickButton(0,"NoNetworkConnectionretry");
					//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
					waitSec = 5;
					
				}


			}
		}
		Reporter.log(numPurchase + " consecutive purchases attempted.");
	}
	
	public void enterPurchaseAmount(int waitSec, String amount) throws InterruptedException {
/*Pre: amount is a string that can be parsed into an integer representing the amount of purchases
 * Post: amount specified is entered and ok is clicked
 */
		this.enterNumPad(waitSec, amount);
	}
	

	
	

	

	
	
	
	
	
//log related functions that's not fully implemented yet	
/*
	private void logMessage(String message)
	{
		testLog.log( Level.FINE, message ); 
	}
	private void logError(Exception ex)
	{
		testLog.log( Level.SEVERE, ex.toString(), ex );
	}
*/	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
//The next three functions are attempts to automate UI tests. They are not completed yet due to technical difficulties that
//needs more time to think through
	public boolean checkPageOverlap(int waitSec, String pName) throws InterruptedException {
/*Pre: mobilePage has been appropriated initialized
 * Post: return true if any elements in this page has boundaries overlapping. false if otherwise
 */
		Thread.sleep(waitSec * 1000);
		Vector URIs = pageName.get(pName).getAllElements();
		boolean isOverlapping = false;
		try {
		int numElement = URIs.size();
		for (int i = 0; i<numElement; i++) {
			for (int j=i+1; j<numElement;j++) {
//System.out.println((String)URIs.elementAt(i));
				WebElement a = this.findElementByXPath(pElement.get((String) URIs.elementAt(i)));
//System.out.println((String)URIs.elementAt(j));
				WebElement b = this.findElementByXPath(pElement.get((String) URIs.elementAt(j)));
//System.out.println("no exception when finding element");
				boolean compare = this.isOverlap(a, b);
				isOverlapping = (compare||isOverlapping);
//				if (compare == true) {
//				System.out.println(URIs.elementAt(i) + " and " + URIs.elementAt(j) + " is compared, their overlapping status is: "+ compare);
//				}
			}
		}
		} 
		catch(Exception e) {
System.out.println("exception caught when comparing overlaps");			
//this.logMessage("exception caught when comparing overlaps");
		}
		return isOverlapping;
		
	}
	
	public boolean isOverlap(WebElement w1, WebElement w2) {
/* Pre: w1 and w2 are elements currently displayed on current page
 * Post: return true if w1 and w2 has boundaries overlapping. false if otherwise
 */
//		This method is not properly implemented yet. need revision & debug
//		WebElement w1 = this.findElementByXPath(pElement.get(object));
//		WebElement w2 = this.findElementByXPath(pElement.get(object2));
		boolean isOverlap = false;
		int[] x = new int[4];
		int[] y = new int[4];
		
		x[0] = w1.getLocation().getX();
		y[0] = w1.getLocation().getY();
		int width1 = w1.getSize().getWidth();
		int height1 = w1.getSize().getHeight();		
		x[1]= x[0] + width1;
		y[1]= y[0] + height1;		
		
		x[2] = w2.getLocation().getX();
		y[2] = w2.getLocation().getY();
		int width2 = w2.getSize().getWidth();
		int height2 = w2.getSize().getHeight();		
		x[3]= x[2] + width2;
		y[3]= y[2] + height2;		
		
		isOverlap = this.coordIsPartiallyContainedIn(w1, w2) || this.coordIsPartiallyContainedIn(w2, w1);
		return isOverlap;
	}
	
	public boolean coordIsPartiallyContainedIn (WebElement w1, WebElement w2) {
		boolean isContained = false;

		int ax1 = w1.getLocation().getX();
		int ay1 = w1.getLocation().getY();
		int width1 = w1.getSize().getWidth();
		int height1 = w1.getSize().getHeight();		
		int ax2 = ax1 + width1;
		int ay2 = ay1 + height1;		
		int bx1 = w2.getLocation().getX();
		int by1 = w2.getLocation().getY();
		int width2 = w2.getSize().getWidth();
		int height2 = w2.getSize().getHeight();
		int bx2 = bx1 + width2;
		int by2 = by1 + height2;
		
		if ((bx2>=ax2) && (ax2 > bx1) && (by2>=ay2) && (ay2 > by1)  ) {
			System.out.println("A possible overlap: 1st element has [" +ax1+ " ,"+ax2+ " ,"+ay1+ " ,"+ay2+ "]");
			System.out.println("A possible overlap: 2nd element has [" +bx1+ " ,"+bx2+ " ,"+by1+ " ,"+by2+ "]");
			return true;
		}
		else if ( (ax1 < bx2) && (bx2<=ax2)&& (ay1 < by2)&&(by2<=ay2)) {
			System.out.println("A possible overlap: 1st element has [" +ax1+ " ,"+ax2+ " ,"+ay1+ " ,"+ay2+ "]");
			System.out.println("A possible overlap: 2nd element has [" +bx1+ " ,"+bx2+ " ,"+by1+ " ,"+by2+ "]");
			return true;
		}
//		System.out.println("not overlapping");
		return isContained;
	}
	
	public int areaWithTwoRectangle (int[] x, int[] y) {
		int area = 0;
		int minX, maxX,minY,maxY =0;
		minX = Math.min(x[0], x[2]);
		maxX = Math.max(x[1], x[3]);
		minY = Math.min(y[0], y[2]);
		maxY = Math.max(y[1], y[3]);
		
		for (int i= minX; i< maxX; i++ ) {
			for (int j = minY; j<maxY; j++) {
				if ((x[0]<=i && i<x[1] && y[0]<=j && j<y[1])|| (x[2]<=i && i<x[3] && y[2]<=j && j<y[3])) {
					area ++;
				}
			}
		}
		System.out.println("area is: " + area);
		return area;
	}
	
	
	
	
	public boolean isaligned (String mode, String URI1, String URI2) {
/* Pre: all elements are appropriately defined and initialized
 * Post: return true if all elements are aligned. return false otherwise
 */
// not implemented yet		
		switch (mode) {
		
		case ("HorizontalTop"): {}
		case ("HorizontalMid"): {}
		case ("HorizontalBottom"): {}
		case ("VerticalLeft"): {}
		case ("VerticalMid"): {}
		case ("VerticalRight"): {}
		dafault: return true;
		
		}
		
		
		return true;
	}
	
}
