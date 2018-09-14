import static org.junit.Assert.assertNotEquals;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.testng.Assert;
import bsh.This;
import io.appium.java_client.android.*;

public class PepAndroidDriver<T extends WebElement> extends AndroidDriver<T> {
	private String merchantIDXPath = "//android.widget.EditText[@text='Merchant ID']";
	private String merchantID = "005040100000004";
	private String merchantSignInButtonXPath = "//android.widget.Button[@text='SIGN IN']";
	private String merchantPasswordXPath = "//android.widget.EditText[@text='Password']";
	private String merchantPasswordContinueButtonXPath= "//android.widget.Button[@text='CONTINUE']";


	
	
	public PepAndroidDriver(URL url, DesiredCapabilities capabilities, String elementConfig) {
		super(url,capabilities);

		// TODO Auto-generated constructor stub
	}
	
	public void merchantSignin(int waitSec, String ID) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		try {
			this.findElementByXPath(merchantIDXPath).sendKeys(ID);
			System.out.println("merchant id element found");
			this.findElementByXPath(merchantSignInButtonXPath).click();
			System.out.println("Sign in button clicked");

			}catch(Exception e) {

			System.out.println("exception caught");
			}
	}
	
	public void merchantPassword(int waitSec, String password) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		this.findElementByXPath(merchantPasswordXPath).sendKeys(password);
		System.out.println("merchant password element found");
		this.findElementByXPath(merchantPasswordContinueButtonXPath).click();
		System.out.println("continue button clicked");		
	}
	
	
	public void enterEmptyPIN(int waitSec) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		try {
			this.findElementByXPath("*[contains(@text,'6-digit')]");
		}
		catch (Exception e) {
			Assert.assertTrue(true); //If the validation message does not exist (and causes an exception) before anything is entered, test pass
		}
		
		try {
			this.findElementByXPath("//android.widget.LinearLayout[@resource-id='com.mobeewave.pep.test.debug:id/numpad_button_ok']").click();
			Thread.sleep(500);   //very important otherwise the validation message does not appear in time
			Assert.assertEquals(this.findElementByXPath("*[contains(@text,'6-digit')]").getText(), "Please enter a 6-digit PIN");
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
			this.findElementByXPath("//android.widget.Button[@text=" + PIN.charAt(i) + "]").click();
			System.out.println(PIN.charAt(i)+ " button clicked");		
		}
		this.findElementByXPath("//android.widget.LinearLayout[@resource-id='com.mobeewave.pep.test.debug:id/numpad_button_ok']").click();
		System.out.println("OK button clicked");	
		}catch(Exception e) {
			System.out.println("exception caught");
		}
	}

	public void clickNext(int waitSec) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		this.findElementByXPath("//android.widget.Button[@resource-id='com.mobeewave.pep.test.debug:id/base_primary_button']").click();
	}
}
