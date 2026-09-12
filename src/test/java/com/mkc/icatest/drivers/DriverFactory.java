package com.mkc.icatest.drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.mkc.icatest.config.TestConfig;

/**
 * Creates the WebDriver instance. Uses Selenium 4's built-in Selenium
 * Manager, so no manual chromedriver download/path setup is needed as long
 * as Chrome is installed on the machine running the tests.
 */
public final class DriverFactory {

	private DriverFactory() {
	}

	public static WebDriver createChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		if (TestConfig.HEADLESS) {
			options.addArguments("--headless=new");
		}
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--disable-notifications");
		// Reduces the chance of ica.se serving a bot-check page instead of the real site.
		options.addArguments("--lang=sv-SE");
		options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });

		return new ChromeDriver(options);
	}
}
