package EmailAutomate.GmailRead;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Registration {

	public static void signUp() throws Exception {
		// TODO Auto-generated method stub
		
		String exePath = "D:\\Training\\ChromeDriver\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
		driver.get("https://www.ministryoftesting.com/account/signup?plan=theclub");
		Thread.sleep(1000);
		
		driver.findElement(By.xpath("//*[@id=\"user_first_name\"]")).sendKeys("Kumari Priyanka");
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"user_email\"]")).sendKeys("priyankasel17@gmail.com");
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"user_username\"]")).sendKeys("PriyankaTest17");
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"user_password\"]")).sendKeys("Test@1718");
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"user_password_confirmation\"]")).sendKeys("Test@1718");
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"user_terms_accepted\"]")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"submit\"]")).click();
		Thread.sleep(3000);
		
		//*[@id="user_first_name"]
		

	}

}
