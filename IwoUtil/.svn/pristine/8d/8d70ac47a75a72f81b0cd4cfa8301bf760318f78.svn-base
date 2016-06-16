package com.test.iwomag.android.pubblico.util;

import android.util.Log;

/**
 * Debug.java Functions and helpers to aid debugging and engine reports.
 * DebugMode can be toggled to avoid using the print command.
 */
public class Logger {

	private static String tag = "IwoMedia";

	protected static boolean debugMode = Config.DEBUG;

	public static String getDebugTag() {
		return tag;
	}

	/**
	 * Sets the tag to be used in LogCat debug messages
	 * 
	 * @param tag
	 *            any valid String for LogCat tags
	 */
	public static void setDebugTag(String tag) {
		Logger.tag = tag;
	}

	/**
	 * set debugMode
	 * 
	 * @param debug
	 */
	public static void setDebugMode(boolean debug) {
		Logger.debugMode = debug;
	}

	/**
	 * Prints a warning to LogCat with information about the engine warning
	 * 
	 * @param source
	 *            The source of the warning, such as function name
	 * @param message
	 *            The message to be passed on
	 */
	public static void warning(String source, String message) {
		if (!debugMode)
			return;
		Log.w(tag, source + " - " + message);
		Exception e = new Exception(source + " - " + message);
		e.printStackTrace();
	}

	/**
	 * Prints a warning to LogCat with information about the engine warning
	 * 
	 * @param source
	 *            The source of the warning, such as function name
	 * @param message
	 *            The message to be passed on
	 */
	public static void w(String source, String message) {
		if (!debugMode)
			return;
		Log.w(tag, source + " - " + message);
		Exception e = new Exception(source + " - " + message);
		e.printStackTrace();
	}

	/**
	 * Prints a warning to LogCat with information about the engine warning
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void warning(String message) {
		if (!debugMode)
			return;
		Log.w(tag, message);
	}

	/**
	 * Prints a warning to LogCat with information about the engine warning
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void w(String message) {
		if (!debugMode)
			return;
		Log.w(tag, message);
	}

	/**
	 * Prints to the verbose stream of LogCat with information from the engine
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void print(String message) {
		if (!debugMode)
			return;
		if (message == null)
			return;
		Log.v(tag, message);
	}

	/**
	 * Prints to the error stream of LogCat with information from the engine
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void error(String message) {
		Log.e(tag, message);
		Exception e = new Exception(message);
		e.printStackTrace();
	}

	/**
	 * Prints to the error stream of LogCat with information from the engine
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void e(String message) {
		Log.e(tag, message);
		// Exception e = new Exception(message);
		// e.printStackTrace();
	}

	/**
	 * Prints to the error stream of LogCat with information from the engine
	 * 
	 * @param message
	 * @param t
	 */
	public static void error(String message, Throwable t) {
		Log.e(tag, message, t);
	}

	/**
	 * Prints to the error stream of LogCat with information from the engine
	 * 
	 * @param message
	 * @param t
	 */
	public static void e(String message, Throwable t) {
		Log.e(tag, message, t);
	}

	/**
	 * Prints to the verbose stream of LogCat, with information that comes in
	 * when in Rokon.verboseMode
	 * 
	 * @param method
	 * @param message
	 */
	public static void verbose(String method, String message) {
		if (!debugMode)
			return;
		Log.v(tag, method + " - " + message);
	}

	/**
	 * Prints to the verbose stream of LogCat, with information that comes in
	 * when in Rokon.verboseMode
	 * 
	 * @param method
	 * @param message
	 */
	public static void v(String method, String message) {
		if (!debugMode)
			return;
		Log.v(tag, method + " - " + message);
	}

	/**
	 * Prints to the verbose stream of LogCat, with information that comes in
	 * when in Rokon.verboseMode
	 * 
	 * @param message
	 */
	public static void verbose(String message) {
		if (!debugMode)
			return;
		if (message == null)
			return;
		Log.v(tag, message);
	}

	/**
	 * Prints to the verbose stream of LogCat, with information that comes in
	 * when in Rokon.verboseMode
	 * 
	 * @param message
	 */
	public static void v(String message) {
		if (!debugMode)
			return;
		if (message == null)
			return;
		Log.v(tag, message);
	}

	/**
	 * Forces the application to exit, this is messy, unsure if it should be
	 * used. For debugging purposes while testing, it will be.
	 */
	public static void forceExit() {
		System.exit(0);
	}

	/**
	 * Prints to the info stream of LogCat
	 * 
	 * @param message
	 */
	public static void i(String message) {
		if (!debugMode)
			return;
		if (message == null)
			return;
		Log.i(tag, message);
	}

	/**
	 * Prints to the info stream of LogCat
	 * 
	 * @param message
	 */
	public static void info(String message) {
		if (!debugMode)
			return;
		if (message == null)
			return;
		Log.i(tag, message);
	}

}
