package com.mkc.icatest.config;

/**
 * Reads run settings from system properties so they can be overridden from
 * the command line, e.g. {@code mvn test -Dheadless=false -DbaseUrl=...}.
 */
public final class TestConfig {

	public static final String BASE_URL = System.getProperty("baseUrl", "https://www.ica.se/");
	public static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "true"));
	public static final int TIMEOUT_SECONDS = Integer.getInteger("timeoutSeconds", 15);

	private TestConfig() {
	}
}
