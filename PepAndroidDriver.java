import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;

import io.appium.java_client.android.*;

public class PepAndroidDriver<T extends WebElement> extends AndroidDriver<T> {
	private String merchantIDXPath = "//android.widget.EditText[@text='Merchant ID']";
	private String merchantID = "005040100000004";
	private String merchantSignInButtonXPath = "//android.widget.Button[@text='SIGN IN']";
	private String merchantPasswordXPath = "//android.widget.EditText[@text='Password']";
	private String merchantPasswordContinueButtonXPath= "//android.widget.Button[@text='CONTINUE']";
/*	public PepAndroidDriver(HttpCommandExecutor remoteAddress, Capabilities desiredCapabilities) {
		super(remoteAddress, desiredCapabilities);
		
		// TODO Auto-generated constructor stub
	}
*/
	public PepAndroidDriver(URL url, DesiredCapabilities capabilities) {
		super(url,capabilities);
		// TODO Auto-generated constructor stub
	}
	
	public void merchantSignin(int waitSec, String ID) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		this.findElementByXPath(merchantIDXPath).sendKeys(ID);
		System.out.println("merchant id element found");
		this.findElementByXPath(merchantSignInButtonXPath).click();
		System.out.println("Sign in button clicked");
	}
	
	public void merchantPassword(int waitSec, String password) throws InterruptedException {
		Thread.sleep(waitSec * 1000);
		this.findElementByXPath(merchantPasswordXPath).sendKeys(password);
		System.out.println("merchant password element found");
		this.findElementByXPath(merchantPasswordContinueButtonXPath).click();
		System.out.println("continue button clicked");		
	}

}
