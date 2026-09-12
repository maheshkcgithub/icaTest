package com.mkc.icatest.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mkc.icatest.config.TestConfig;

/**
 * Page object for the ica.se start page. Search-box selectors are best-effort
 * (see class-level note on {@link CookieConsentBanner}) — several common
 * patterns are tried in order since the live markup couldn't be inspected
 * from this environment.
 */
public class HomePage extends BasePage {

	private static final List<By> SEARCH_TOGGLE_CANDIDATES = List.of(
			By.cssSelector("button[aria-label*='ök' i]"),
			By.cssSelector("[data-testid*='search' i] button"),
			By.cssSelector("button[class*='search' i]"));

	// Header-scoped selectors are tried first so a hidden/off-canvas input
	// elsewhere on the page (e.g. a hidden mobile drawer, or a footer form)
	// that also happens to match isn't picked up instead of the visible one.
	private static final List<By> SEARCH_INPUT_CANDIDATES = List.of(
			By.cssSelector("header input[type='search']"),
			By.cssSelector("header input[placeholder*='ök' i]"),
			By.cssSelector("header input[aria-label*='ök' i]"),
			By.cssSelector("header input[name='q']"),
			By.cssSelector("header [data-testid*='search' i] input"),
			By.cssSelector("input[type='search']"),
			By.cssSelector("input[placeholder*='ök' i]"),
			By.cssSelector("input[aria-label*='ök' i]"),
			By.cssSelector("input[name='q']"),
			By.cssSelector("[data-testid*='search' i] input"));

	private static final List<By> NAV_CANDIDATES = List.of(
			By.cssSelector("header nav"),
			By.tagName("nav"));

	public HomePage(WebDriver driver) {
		super(driver);
	}

	public HomePage open() {
		driver.get(TestConfig.BASE_URL);
		return this;
	}

	public String getTitle() {
		return driver.getTitle();
	}

	public boolean isMainNavigationVisible() {
		return findFirstVisible(NAV_CANDIDATES, Duration.ofSeconds(TestConfig.TIMEOUT_SECONDS)).isPresent();
	}

	/**
	 * Reveals the search input (clicking a search icon/toggle first if the
	 * input isn't already on screen) and returns it.
	 */
	public WebElement openSearchBox() {
		return findFirstVisible(SEARCH_INPUT_CANDIDATES, Duration.ofSeconds(3))
				.orElseGet(() -> {
					findFirstVisible(SEARCH_TOGGLE_CANDIDATES, Duration.ofSeconds(5)).ifPresent(this::clickReliably);
					return findFirstVisible(SEARCH_INPUT_CANDIDATES, Duration.ofSeconds(TestConfig.TIMEOUT_SECONDS))
							.orElseThrow(() -> new IllegalStateException(
									"Could not locate the search input via any known selector. "
											+ "Inspect ica.se's current markup and update HomePage.SEARCH_INPUT_CANDIDATES."));
				});
	}

	public void searchFor(String query) {
		WebElement searchBox = openSearchBox();
		clickReliably(searchBox);
		searchBox.sendKeys(query);
		searchBox.sendKeys(Keys.ENTER);
	}
}
