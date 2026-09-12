package com.mkc.icatest.tests;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.mkc.icatest.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchTest extends BaseTest {

	private static final String QUERY = "mjölk";

	@Test
	void searchingForAProductNavigatesToResults() {
		String startingUrl = driver.getCurrentUrl();

		homePage.searchFor(QUERY);

		SearchResultsPage resultsPage = new SearchResultsPage(driver);
		resultsPage.waitForNavigationAwayFrom(startingUrl, Duration.ofSeconds(15));

		assertTrue(resultsPage.getBodyText().toLowerCase().contains(QUERY.toLowerCase()),
				"Expected the results page to reference the search term \"" + QUERY + "\"");
	}
}
