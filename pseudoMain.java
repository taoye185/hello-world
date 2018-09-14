import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;


public class pseudoMain{
	public static PepAndroidDriver<?> mobiledriver;

	@BeforeTest
	public void beforeTest( ) throws MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, "1.9.0");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "8.0.0");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Samsung Galaxy S9");
//		capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Browser");
//		capabilities.setCapability("udid", "192.168.56.101:5555"); //设备的udid (adb devices 查看到的)
//	    cap.setCapability("browserName", "chrome");//设置HTML5的自动化，打开谷歌浏览器	
		capabilities.setCapability("appPackage","com.mobeewave.pep.test.debug");//被测app的包名
		capabilities.setCapability("appActivity","com.mobeewave.mpos2.login.LoginActivity");//被测app的入口Activity名称	
	    capabilities.setCapability("app", "C:\\Users\\YeTao\\eclipse-workspace\\automationTemplates\\apps\\pep-v1.4.1-198-integration-debug.apk");//安装apk
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("newCommandTimeout", 2000);
//		mobiledriver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);//把以上配置传到appium服务端并连接手机
		mobiledriver = new PepAndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	}

	@AfterTest
	public void afterTest( ){
//		mobiledriver.quit();
	}

	
	@Test
	public static void launchApp() throws InterruptedException{
		mobiledriver.merchantSignin(5,"005040100000004");		//wait for 5 seconds, then input Merchant ID and click OK
		mobiledriver.merchantPassword(7,"P27O7FZM2");
//		Assert.assertEquals(mobiledriver.findElementById("toolbar_title").getText(), "Merchant Registration");

//		Assert.assertEquals(mobiledriver.getTitle(), "Appium: Mobile App Automation Made Awesome.", "Title Mismatch");
	}
	
	
	
	
	
}