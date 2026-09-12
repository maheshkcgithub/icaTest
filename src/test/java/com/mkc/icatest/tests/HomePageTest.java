package com.mkc.icatest.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HomePageTest extends BaseTest {

	@Test
	void homePageLoadsWithIcaTitle() {
		String title = homePage.getTitle();

		assertTrue(title != null && title.toLowerCase().contains("ica"),
				"Expected the page title to mention ICA, but was: " + title);
	}

	@Test
	void mainNavigationIsVisible() {
		assertTrue(homePage.isMainNavigationVisible(), "Expected the main site navigation to be visible");
	}
}
