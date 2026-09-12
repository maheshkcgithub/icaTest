package com.mkc.icatest.tools;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * One-off lookup tool (not a test): searches ica.se for milk with 3% fat and
 * prints any lines from the results page that look like a price, so a human
 * can read the actual number back to me. This environment has no network
 * access to ica.se, so this must be run locally.
 *
 * Run with: mvn -q compile exec:java -Dexec.mainClass="com.mkc.icatest.tools.MilkPriceLookup"
 * (or just run the main method directly from IntelliJ).
 */
public final class MilkPriceLookup {

	// Matches Swedish price notations: "19:90", "19,90 kr", "19.90 kr", "19 kr".
	private static final Pattern PRICE_PATTERN = Pattern.compile("\\d{1,4}([:.,]\\d{2})?\\s*(kr|:-)?", Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) throws InterruptedException {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--lang=sv-SE");
		WebDriver driver = new ChromeDriver(options);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		try {
			driver.get("https://www.ica.se/");
			acceptCookiesIfPresent(driver, wait);

			String startingUrl = driver.getCurrentUrl();
			search(driver, wait, "mjölk 3%");
			wait.until(d -> !d.getCurrentUrl().equals(startingUrl));
			Thread.sleep(2000); // let the results grid finish rendering

			System.out.println("Results URL: " + driver.getCurrentUrl());
			System.out.println("Results title: " + driver.getTitle());
			System.out.println();
			System.out.println("Lines mentioning \"3 %\" fat content:");
			printMatchingLines(driver, "3\\s*%");

			System.out.println();
			System.out.println("All price-like tokens found on the page:");
			printPriceTokens(driver);
		} finally {
			driver.quit();
		}
	}

	private static void acceptCookiesIfPresent(WebDriver driver, WebDriverWait wait) {
		List<By> candidates = List.of(
				By.id("onetrust-accept-btn-handler"),
				By.cssSelector("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"),
				By.xpath("//button[contains(translate(., 'ÅÄÖ', 'åäö'), 'godkänn alla')]"),
				By.xpath("//button[contains(translate(., 'ÅÄÖ', 'åäö'), 'acceptera alla')]"));
		for (By locator : candidates) {
			List<WebElement> found = driver.findElements(locator);
			if (!found.isEmpty() && found.get(0).isDisplayed()) {
				click(driver, found.get(0));
				return;
			}
		}
	}

	private static void search(WebDriver driver, WebDriverWait wait, String query) {
		List<By> inputCandidates = List.of(
				By.cssSelector("header input[type='search']"),
				By.cssSelector("header input[name='q']"),
				By.cssSelector("input[type='search']"),
				By.cssSelector("input[name='q']"));
		WebElement input = null;
		for (By locator : inputCandidates) {
			List<WebElement> found = driver.findElements(locator);
			for (WebElement candidate : found) {
				if (candidate.isDisplayed()) {
					input = candidate;
					break;
				}
			}
			if (input != null) {
				break;
			}
		}
		if (input == null) {
			List<By> toggleCandidates = List.of(
					By.cssSelector("button[aria-label*='ök' i]"),
					By.cssSelector("[data-testid*='search' i] button"));
			for (By locator : toggleCandidates) {
				List<WebElement> found = driver.findElements(locator);
				if (!found.isEmpty()) {
					click(driver, found.get(0));
					break;
				}
			}
			input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='search']")));
		}
		click(driver, input);
		input.sendKeys(query);
		input.sendKeys(Keys.ENTER);
	}

	private static void click(WebDriver driver, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
		try {
			element.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", element);
		}
	}

	private static void printMatchingLines(WebDriver driver, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		String bodyText = driver.findElement(By.tagName("body")).getText();
		for (String line : bodyText.split("\\R")) {
			if (pattern.matcher(line).find()) {
				System.out.println("  " + line.trim());
			}
		}
	}

	private static void printPriceTokens(WebDriver driver) {
		String bodyText = driver.findElement(By.tagName("body")).getText();
		Matcher matcher = PRICE_PATTERN.matcher(bodyText);
		while (matcher.find()) {
			String token = matcher.group().trim();
			if (!token.isEmpty() && Character.isDigit(token.charAt(0))) {
				System.out.println("  " + token);
			}
		}
	}

	private MilkPriceLookup() {
	}
}
