package com.mkc.icatest.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Dismisses the GDPR cookie-consent banner if one shows up. The exact markup
 * couldn't be confirmed against the live site from this environment, so this
 * tries the most common consent-management-platform patterns (OneTrust,
 * Cookiebot) plus a text match on the Swedish "accept all" wording, and
 * simply does nothing if none of them appear within a short wait.
 */
public class CookieConsentBanner extends BasePage {

	private static final List<By> ACCEPT_ALL_CANDIDATES = List.of(
			By.id("onetrust-accept-btn-handler"),
			By.cssSelector("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"),
			By.xpath("//button[contains(translate(., 'ÅÄÖ', 'åäö'), 'godkänn alla')]"),
			By.xpath("//button[contains(translate(., 'ÅÄÖ', 'åäö'), 'acceptera alla')]"),
			By.xpath("//button[contains(translate(., 'ÅÄÖ', 'åäö'), 'tillåt alla')]"));

	public CookieConsentBanner(WebDriver driver) {
		super(driver);
	}

	/**
	 * @return true if a banner was found and dismissed, false if none appeared.
	 */
	public boolean acceptAllIfPresent() {
		return findFirstVisible(ACCEPT_ALL_CANDIDATES, Duration.ofSeconds(5))
				.map(button -> {
					clickReliably(button);
					return true;
				})
				.orElse(false);
	}
}
