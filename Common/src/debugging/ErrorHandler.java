package com.tanndev.subwave.common.debugging;

/**
 * Provides a mechanism for reporting errors and exceptions. All calls to System.err.println() should instead use {@link
 * #logError(java.lang.String)}.{@link #logError(java.lang.String, java.lang.Exception)} can be used instead to provide
 * a stack trace as well.
 *
 * @author James Tanner
 */
public class ErrorHandler {

    // TODO Create and store error log.

    /** Setting: print error messages to System.err */
    private static boolean printToStandardErr = true;

    /** Setting: print stack traces with error. */
    private static boolean verboseMode = true;

    /**
     * Print the provided error to the error log and/or standard err.
     * <p/>
     * Behaves the same as {@link #logError(java.lang.String, java.lang.Exception)} with a null exception parameter.
     *
     * @param message error message to report
     */
    public static void logError(String message) {
        logError(message, null);
    }

    /**
     * Print the provided error to the error log and/or standard err.
     * <p/>
     * If {@link #printToStandardErr} is true, the provided error message will be printed to System.err. If {@link
     * #verboseMode} is also true, and an exception is provided, a stack trace will be printed as well.
     *
     * @param errorMsg  error message to report. Ignored if null.
     * @param exception exception for stack trace. Ignored if null.
     */
    public static void logError(String errorMsg, Exception exception) {
        if (printToStandardErr) {
            if (errorMsg != null) System.err.println(errorMsg);
            if (verboseMode && exception != null) exception.printStackTrace();
        }

        // TODO Save to error log.
    }
}
