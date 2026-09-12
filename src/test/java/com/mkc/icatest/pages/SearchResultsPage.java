package com.mkc.icatest.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResultsPage extends BasePage {

	public SearchResultsPage(WebDriver driver) {
		super(driver);
	}

	/** Waits for navigation away from the given starting URL. */
	public void waitForNavigationAwayFrom(String startingUrl, Duration timeout) {
		new WebDriverWait(driver, timeout).until(d -> !d.getCurrentUrl().equals(startingUrl));
	}

	public String getUrl() {
		return driver.getCurrentUrl();
	}

	public String getBodyText() {
		return waitVisible(By.tagName("body")).getText();
	}
}
