package com.mkc.icatest.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

import com.mkc.icatest.drivers.DriverFactory;
import com.mkc.icatest.pages.CookieConsentBanner;
import com.mkc.icatest.pages.HomePage;

public abstract class BaseTest {

	protected WebDriver driver;
	protected HomePage homePage;

	@BeforeEach
	void setUpDriver() {
		driver = DriverFactory.createChromeDriver();
		homePage = new HomePage(driver).open();
		new CookieConsentBanner(driver).acceptAllIfPresent();
	}

	@AfterEach
	void tearDownDriver() {
		if (driver != null) {
			driver.quit();
		}
	}
}
