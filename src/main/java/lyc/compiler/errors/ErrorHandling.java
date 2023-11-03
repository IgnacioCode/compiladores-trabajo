package lyc.compiler.errors;

public final class ErrorHandling {

    public static boolean validateInt(String value) {
        try {
            Short.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean validateFloat(String value) {
        try {
            Float f = Float.valueOf(value);
            if (f.isInfinite() || f.isNaN()) {
                throw new NumberFormatException("Invalid float");
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static String formatError(String error_type, String msg) {
        return "[" + "\u001B[31m" + error_type + "::Error" + "\u001B[0m" + "]" + " - " + msg;
    }

}
