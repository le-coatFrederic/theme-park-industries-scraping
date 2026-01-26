package com.fredlecoat.backend.utils;

/**
 * Utility class for parsing scraped text values into typed data.
 * Centralizes all parsing logic to avoid duplication across scrapers.
 */
public final class ScrapingParser {

    private ScrapingParser() {
        // Utility class - no instantiation
    }

    /**
     * Removes all types of spaces from a string (regular, non-breaking, narrow).
     */
    private static String removeAllSpaces(String str) {
        return str
            .replace(" ", "")       // regular space
            .replace("\u00A0", "")  // non-breaking space
            .replace("\u202F", "")  // narrow no-break space (French number separator)
            .replace("\u2009", ""); // thin space
    }

    /**
     * Parses a money value like "6 487 883 €" into a Long.
     */
    public static Integer parseMoney(Object value) {
        if (value == null) {
            return null;
        }
        String str = removeAllSpaces(value.toString().replace("€", "")).trim();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses an integer value like "2 956" into an Integer.
     * Returns null if parsing fails.
     */
    public static Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        String str = removeAllSpaces(value.toString())
            .replace("m", "")
            .replace("²", "")
            .trim();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses an integer value like "2 956" into an int.
     * Returns the specified default value if parsing fails.
     */
    public static int parseIntegerOrDefault(Object value, int defaultValue) {
        Integer result = parseInteger(value);
        return result != null ? result : defaultValue;
    }

    /**
     * Parses a surface value like "30 000 m²" into an Integer.
     */
    public static Integer parseSurface(Object value) {
        if (value == null) {
            return null;
        }
        String str = removeAllSpaces(value.toString().replace("m²", "")).trim();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a capacity value like "1 200 / h" into an Integer.
     */
    public static Integer parseCapacity(Object value) {
        if (value == null) {
            return null;
        }
        String str = removeAllSpaces(value.toString().replace("/ h", "").replace("/h", "")).trim();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Normalizes an image URL by extracting the path after "attractions/".
     * Example: "/images/attractions/coaster.png" -> "coaster.png"
     */
    public static String normalizeImageUrl(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }
        int attractionsIndex = rawUrl.indexOf("attractions/");
        if (attractionsIndex != -1) {
            return rawUrl.substring(attractionsIndex + "attractions/".length());
        }
        return rawUrl;
    }
}
