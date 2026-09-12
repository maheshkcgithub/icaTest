package com.mkc.icatest.pages;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mkc.icatest.config.TestConfig;

/**
 * Shared wait helpers for page objects. ica.se is a JS-rendered site, so
 * tests wait for elements rather than asserting on them immediately.
 */
public abstract class BasePage {

	protected final WebDriver driver;
	protected final WebDriverWait wait;

	protected BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.TIMEOUT_SECONDS));
	}

	protected WebElement waitVisible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	protected WebElement waitClickable(By locator) {
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	/**
	 * Scrolls the element into the center of the viewport and clicks it,
	 * falling back to a JavaScript click if the browser reports the point as
	 * covered by another element (e.g. a sticky header, or a slide-out panel
	 * that "isDisplayed()" doesn't detect as visually obscured).
	 */
	protected void clickReliably(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
		try {
			element.click();
		} catch (ElementClickInterceptedException e) {
			js.executeScript("arguments[0].click();", element);
		}
	}

	/**
	 * Returns the first locator (in order) that matches a visible element
	 * within the timeout, or empty if none do. Used where the real markup
	 * couldn't be verified up front and more than one selector is plausible.
	 */
	protected Optional<WebElement> findFirstVisible(List<By> candidates) {
		return findFirstVisible(candidates, Duration.ofSeconds(TestConfig.TIMEOUT_SECONDS));
	}

	protected Optional<WebElement> findFirstVisible(List<By> candidates, Duration timeout) {
		long deadline = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < deadline) {
			for (By locator : candidates) {
				List<WebElement> found = driver.findElements(locator);
				for (WebElement element : found) {
					if (element.isDisplayed()) {
						return Optional.of(element);
					}
				}
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}
