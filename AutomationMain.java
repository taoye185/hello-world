import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.Reporter;
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

import java.util.logging.Level;
import java.util.logging.Logger;



public class AutomationMain{
	public static MWAndroidDriver<?> mobiledriver;
	static String testManagementFile = ".\\src\\Testcase configuration\\Test Management.csv";
	static String groupFile = "";
	//This configuration file contains all the test cases to be run during the test - QA engineers should update this file
	//any time test cases changes
	static String appFile = "";
	//This configuration file contains the app configurations and apk locations - QA engineers should update this file
	//any time a different app is to be tested.
	static String elementFile = "";
	//This configuration file contains the URI for all the relevant WebElement that the mobile app contains - QA engineers
	//should update this file whenever the app is modified in a way such that one or more WebElement has been created or changed
	//to a new URI.
	static String deviceFile = "";

	static DesiredCapabilities capabilities = new DesiredCapabilities();
	
	@BeforeTest
	public void beforeTest( ) throws MalformedURLException {
		try { 	
			FileReader fileReaderTMF = new FileReader(testManagementFile); 
			//read the different configuration files to be loaded from the managing file
	        CSVReader csvReaderFR1 = new CSVReader(fileReaderTMF); 
	        String[] nextRecordTMF; 
	        int i = 0;
	        while ((nextRecordTMF = csvReaderFR1.readNext()) != null) { 
 	        	if (i>0) {		//unless this is title row, read the configuration file locations
 	        		switch (nextRecordTMF[0]) {
 	        			case "groupFile":{
 	        				groupFile = nextRecordTMF[1];
 	        				break;
 	        			}
 	        			case "appFile":{
 	        				appFile = nextRecordTMF[1];
 	        				break;
 	        			}
 	        			case "elementFile":{
 	        				elementFile = nextRecordTMF[1];
 	        				break;
 	        			}
 	        			case "deviceFile":{
 	        				deviceFile = nextRecordTMF[1];
 	        				break;
 	        			}
 	        			default: {
 	        				break;
 	        			} //case
 	        		} //switch
          		} //if
	            i++;
	        } //while
            csvReaderFR1.close();	//close management file
			
			FileReader filereader = new FileReader(deviceFile); 
			//read the remaining capability value from the configuration file
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextRecord; 
	        int j = 0;
	        while ((nextRecord = csvReader.readNext()) != null) { //this code is only good for one device initiation. will need to modify to accommodate for multiple devices
 	        	if (j>0 && Integer.parseInt(nextRecord[0])==1) {			            		//unless this is title row, set the capabilities
         			capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, nextRecord[1]); 
        			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, nextRecord[2]);
        			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,nextRecord[3]);
        			capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,nextRecord[4]);
        			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, nextRecord[5]);
          		}
	            j++;
	        } 
	            csvReader.close();
    			capabilities.setCapability("noReset", true); 
    			//non String type capabilities, could be put into configuration file with a bit modification on code, but I will leave it as-is for now
    			capabilities.setCapability("newCommandTimeout", 2000);
	    } 
	    catch (Exception e) { 
	        e.printStackTrace(); 
	    } 		
		
		try { 	//read the remaining capability value from the configuration file
		        FileReader filereader = new FileReader(appFile); 
		        CSVReader csvReader = new CSVReader(filereader); 
		        String[] nextRecord; 
		        int i = 0;
		        while ((nextRecord = csvReader.readNext()) != null) { 
            		if (i>0) {			            		//unless this is title row, set the capabilities
            			capabilities.setCapability(nextRecord[0], nextRecord[1]); 	
            		//it is assumed that the first column of the config file contains capabilities name		
            			//it is assumed that the second column of the config file contains capabilities value
            			//it is assumed that the 3rd column (or later) of the config file contains notes/comments not
            		}
		            i++;
		        } 
		            csvReader.close();
		    } 
		    catch (Exception e) { 
		        e.printStackTrace(); 
		    } 		
	}

	@AfterTest
	public void afterTest( ) throws MalformedURLException{

		try {
//		sendPDFReportByGMail("taooyee@gmail.com", "Thankyou1", "ytao@mobeewave.com", "test Report", "");
		}
		catch (Exception e) {
			e.printStackTrace(); 
		}
//		There is an excellent tutorial for emailing testing result at https://www.guru99.com/pdf-emails-and-screenshot-of-test-reports-in-selenium.html,
//		The current functionality can be further expanded by following instructions from there.
	

	}

	
	@Test
	public static void appTesting() throws InterruptedException, MalformedURLException{
		// read from the groupFile configuration file and construct the respective test case methods

		try { 
	        FileReader filereader = new FileReader(groupFile); 
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextCase; 
	        int caseRow = 0;
	        while ((nextCase = csvReader.readNext()) != null) { 
	            if (caseRow > 0) { 	            //unless it is title row, construct and execute the method
	            	String casefile = nextCase[1];
	    			mobiledriver = new MWAndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities, elementFile);
	    			//construct a MWAndroidDriver to allow more flexibility and encapsulation compared to the standard AndroidDriver	
	    			mobiledriver.logColorText("blue", "Test Case: " + nextCase[0] + " initiated.");
	    			System.out.println(nextCase[0] + " mobiledriver created.");
	    	        FileReader casefilereader = new FileReader(casefile); 
	    	        CSVReader casecsvReader = new CSVReader(casefilereader); 
	    	        String[] nextRecord; 	    			
	    	        int row = 0;
	    	        while ((nextRecord = casecsvReader.readNext()) != null) { 
	    	            if (row > 0) { 	            //unless it is title row, construct and execute the method
	    	            mobiledriver.testScenarioConstructor(nextRecord); 
	            		//it is assumed that the first column of the config file contains method name to be called
	            		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
	    	            }
	    	            row++;
	    	        } 
	    	        casecsvReader.close();
	    			mobiledriver.logColorText("blue", "Test Case: " + nextCase[0] + " completed.");
//	    	        Reporter.log("<font color='blue'>Test Case: " + nextCase[0] + " completed.</font>");
	    			Reporter.log("-----------------------------------<br>");
	    			mobiledriver.quit();
        		//it is assumed that the first column of the config file contains method name to be called
        		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
	            }
	            caseRow++;
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
    		DataSource source = new FileDataSource("C:\\Users\\YeTao\\eclipse-workspace\\automationTemplates\\test-output\\emailable-report.html");
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