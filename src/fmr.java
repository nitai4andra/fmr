import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.BrowserVersion;


public class fmr {
	
	static List<WebElement> files;
	
	static Boolean hasFiles;
	
	static WebDriver driver;
	
	static WebElement login;
	
	static WebElement pass;
	
	static int filesNumber;
	
	static Element eElement;
	
	public static void main(String[] args) throws InterruptedException, SAXException, IOException, ParserConfigurationException{
		NodeList nList = readXml("cfg.xml");
				
		for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				eElement = (Element) nNode;
				/*// Use this to test in a real browser (though web-sites are treating HtmlUnitDriver differently)
				driver = new FirefoxDriver();*/
				//driver = new HtmlUnitDriver(BrowserVersion.BEST_SUPPORTED);
				//((HtmlUnitDriver) driver).setJavascriptEnabled(true);
				System.setProperty("webdriver.gecko.driver","/home/nitai4andra/soft/geckodriver-v0.19.0-linux64/geckodriver");
				driver = new FirefoxDriver();
							
				//driver.get("https://e.mail.ru/login");
				driver.get("https://mail.ru/");
				//Thread.sleep(100);
				
				login = driver.findElement(By.name("login"));
				pass = driver.findElement(By.name("password"));
				
				login.sendKeys(getTagValue("login", eElement));
				pass.sendKeys(getTagValue("password", eElement));
				driver.findElement(By.xpath("//input[@value='Войти']")).click();				
				/* After authorization in HtmlUnitDriver we would be redirected to our mail page
				(mail.ru), so we need to get back to files.mail.ru again */
				driver.get("http://files.mail.ru/ls/1");
				//Thread.sleep(100);				
				/*//Use this to understand what html page site is sending you looks like
				 
				FileWriter fstream = new FileWriter("log.html");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(driver.getPageSource());
				out.close();*/
				
				FileWriter fstream = new FileWriter("log.html");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(driver.getPageSource());
				out.close();
				
				driver.findElement(By.xpath("//a[@href='/ls/4']")).click();
				filesNumber = filesCount();
				if (filesNumber > 0) {
		    		hasFiles = true;		    		
		    	} else {
		    		hasFiles = false;
		    	}		
	    		
				printLog();
	    		
				while (true == hasFiles) {
					driver.findElement(By.xpath("//table[@class='fileList']/tbody/tr[3]/td[@class='do']/div/a/img[@alt='[Undelete]']/..")).click();	
					Thread.sleep(4000);		
					filesNumber = filesCount();
					if (filesNumber > 0) {
						printLog();
						hasFiles = true;						
					} else {
						printLog();
						hasFiles = false;
					}
				}
				driver.close();	
		   }
		}
	}
	
	public static NodeList readXml(String file) throws SAXException, IOException, ParserConfigurationException {
		File fXmlFile = new File(file);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("account");
		
		return nList;
	}
		
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}
	
	private static void printLog() {
		System.out.print("Account " + getTagValue("login", eElement) + " has " + filesNumber + " files\n");
	}
	
	private static int filesCount() {
		return Integer.parseInt(driver.findElement(By.xpath("//span[@id='ffoldercnt4']")).getText());
	}
}