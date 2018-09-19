import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

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

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.openqa.selenium.WebDriver;




public class AutomationMain{
	public static MWAndroidDriver<?> mobiledriver;
	static String casefile = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\base test case config.csv"; 
	//This configuration file contains all the test cases to be run during the test - QA engineers should update this file
	//any time test cases changes
	static String deviceFile = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\config.csv"; 
	//This configuration file contains the app configurations and apk locations - QA engineers should update this file
	//any time a different app is to be tested.
	static String elementFile = "C:\\Users\\YeTao\\Desktop\\automation\\Testcase configuration\\app elements.csv"; 
	//This configuration file contains the URI for all the relevant WebElement that the mobile app contains - QA engineers
	//should update this file whenever the app is modified in a way such that one or more WebElement has been created or changed
	//to a new URI.

	@BeforeTest
	public void beforeTest( ) throws MalformedURLException {
		String[] capNames = new String[10];
		String[] capValues = new String[10];
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, "1.9.0"); 
		//initiate capability, the first 5 capabilities are unique capabilities that are easier initiated directly rather than read through config file
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "8.0.0");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Samsung Galaxy S9");
		capabilities.setCapability("noReset", true); 
		//non String type capabilities, could be put into configuration file with a bit modification on code, but I will leave it as-is for now
		capabilities.setCapability("newCommandTimeout", 2000);
		try { 	//read the remaining capability value from the configuration file
		        FileReader filereader = new FileReader(deviceFile); 
		        CSVReader csvReader = new CSVReader(filereader); 
		        String[] nextRecord; 
		        int i = 0;

		        while ((nextRecord = csvReader.readNext()) != null) { 
		        	int columnCount = 1;
		            for (String cell : nextRecord) { 
		            	if (columnCount == 1) {	
		            		capNames[i] = cell; 
		            		//it is assumed that the first column of the config file contains capabilities name
		            	}
		            	else if (columnCount == 2) {
		            		capValues[i] = cell;
		            		//it is assumed that the second column of the config file contains capabilities value
		            		if (i>0) {
		            		capabilities.setCapability(capNames[i], capValues[i]); 
		            		//unless this is title row, set the capabilities
		            		}
		            	}
		            	else {
		            		  	//it is assumed that the 3rd column (or later) of the config file contains notes/comments not
		            			//needed for the program itself
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
		//construct a MWAndroidDriver to allow more flexibility and encapsulation compared to the standard AndroidDriver

	}

	@AfterTest
	public void afterTest( ){


		sendPDFReportByGMail("taooyee@gmail.com", "Mobeewave2015", "ytao@mobeewave.com", "test Report", "");
//		There is an excellent tutorial for emailing testing result at https://www.guru99.com/pdf-emails-and-screenshot-of-test-reports-in-selenium.html,
//		The current functionality can be further expanded by following instructions from there.
		
		mobiledriver.quit();
	}

	
	@Test
	public static void appTesting() throws InterruptedException{
		// read from the casefile configuration file and construct the respective test case methods
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
		//end of test case
	
	
	
	}
	
	
	
	
	
    private static void sendPDFReportByGMail(String from, String pass, String to, String subject, String body) {
    //The email functionality to send out reports through emails after testings are done. 

    	Properties props = System.getProperties();

    	String host = "smtp.gmail.com";
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", host);
    	props.put("mail.smtp.user", from);
    	props.put("mail.smtp.password", pass);
    	props.put("mail.smtp.port", "587");
    	props.put("mail.smtp.auth", "true");

    	Session session = Session.getDefaultInstance(props);
    	MimeMessage message = new MimeMessage(session);

    	try {

    		//Set from address
    		message.setFrom(new InternetAddress(from));
    		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    		//Set subject
    		message.setSubject(subject);
    		message.setText(body);
    		BodyPart objMessageBodyPart = new MimeBodyPart();
    		objMessageBodyPart.setText("Greetings from the QA team! This is an auto-generated email for the automated test cases just completed. Please Find The Attached Report File!");
    		Multipart multipart = new MimeMultipart();
    		multipart.addBodyPart(objMessageBodyPart);
    		objMessageBodyPart = new MimeBodyPart();
    		//Set path to the pdf report file
    		//String filename = System.getProperty("user.dir")+"\\Default test.pdf";
    		//Create data source to attach the file in mail
    		DataSource source = new FileDataSource("C:\\Users\\YeTao\\eclipse-workspace\\automationTemplates\\test-output\\index.html");
    		objMessageBodyPart.setDataHandler(new DataHandler(source));
    		objMessageBodyPart.setFileName("Test Report.html");
    		multipart.addBodyPart(objMessageBodyPart);
    		message.setContent(multipart);
    		Transport transport = session.getTransport("smtp");
    		transport.connect(host, from, pass);
    		transport.sendMessage(message, message.getAllRecipients());
    		transport.close();
    	}

    	catch (AddressException ae) {
    		ae.printStackTrace();
    	}

    	catch (MessagingException me) {
    		me.printStackTrace();
    	}

    }


	
	
}