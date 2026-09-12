# ica-se-automation

Selenium + JUnit 5 UI automation tests for [https://www.ica.se/](https://www.ica.se/).

## Important: verify locators before relying on this

This project was written in a sandboxed environment with **no network access
to ica.se**, so the selectors below could not be checked against the live
DOM. They target the most common real-world patterns for a site like this
(OneTrust/Cookiebot cookie banners, a `type="search"` or `name="q"` search
input, a `<nav>` inside `<header>`), with a few fallbacks tried in order —
but they are still best-effort. Run the suite, see which locators miss, and
update them in the page objects under `pages/`:

- `pages/CookieConsentBanner.java` — the "accept all" button
- `pages/HomePage.java` — search input/toggle, main navigation
- `pages/SearchResultsPage.java` — results page content

## Requirements

- JDK 17+
- Maven
- Google Chrome installed locally (Selenium 4's built-in Selenium Manager
  downloads a matching `chromedriver` automatically — no manual driver setup)

## Running the tests

```bash
mvn test
```

Runs headless by default. Useful overrides:

```bash
mvn test -Dheadless=false        # watch it run in a real browser window
mvn test -DbaseUrl=https://www.ica.se/
mvn test -DtimeoutSeconds=20
```

## What's covered

- `HomePageTest` — the home page loads with an ICA-branded title, and the
  main navigation is visible.
- `SearchTest` — searching for a product ("mjölk") navigates to a results
  page that references the search term.

## One-off tools

`src/main/java/com/mkc/icatest/tools/MilkPriceLookup.java` is not a test —
it's a standalone script that searches ica.se for "mjölk 3%" and prints any
page lines mentioning "3 %" plus every price-like token it finds, so you can
read the actual price back from the console output. Run its `main` method
directly in IntelliJ, or:

```bash
mvn -q compile exec:java -Dexec.mainClass="com.mkc.icatest.tools.MilkPriceLookup"
```

It runs with a visible (non-headless) browser window by default so you can
watch what it's doing.

## Structure

```
src/test/java/com/mkc/icatest/
  config/    run configuration (base URL, headless, timeout)
  drivers/   WebDriver creation
  pages/     Page Object Model classes
  tests/     JUnit 5 test classes
```
