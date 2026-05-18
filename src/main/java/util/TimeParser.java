package util;

public class TimeParser {
    /**
     * Normalizes flexible user input times (e.g. "12h30", "12:30", "12h")
     * into standard HH:mm format.
     */
    public static String normalizeTime(String input) {
        if (input == null) return "";
        String clean = input.trim().toLowerCase();
        
        // Replace 'h' with ':' (e.g., "12h30" -> "12:30", "12h" -> "12:")
        clean = clean.replace("h", ":");
        
        // Remove spaces
        clean = clean.replace(" ", "");
        
        // Handle "12:" -> "12:00"
        if (clean.endsWith(":")) {
            clean += "00";
        }
        
        // If there's no colon and it's just a number, like "12", make it "12:00"
        if (!clean.contains(":") && clean.matches("\\d+")) {
            clean += ":00";
        }
        
        // Pad single hour digits (e.g. "9:30" -> "09:30")
        if (clean.matches("\\d:\\d{2}")) {
            clean = "0" + clean;
        }
        
        return clean;
    }
}
