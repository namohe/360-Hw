package helpers;
public class PasswordEvaluator {
	/**
	 * <p> Title: Directed Graph-translated Password Assessor. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of Directed Graph 
	 * diagram into an executable Java program using the Password Evaluator Directed Graph. 
	 * The code detailed design is based on a while loop with a cascade of if statements</p>
	 * 
	 * <p> Copyright: Lynn Robert Carter © 2022 </p>
	 * 
	 * @author Lynn Robert Carter
	 * 
	 * @version 0.00		2018-02-22	Initial baseline 
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String passwordErrorMessage = "";		// The error message text
	public static String passwordInput = "";			// The input being processed
	public static int passwordIndexofError = -1;		// The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running

	/**********
	 * This private method display the input line and then on a line under it displays an up arrow
	 * at the point where an error should one be detected.  This method is designed to be used to 
	 * display the error message on the console terminal.
	 * 
	 * @param input				The input string
	 * @param currentCharNdx	The location where an error was found
	 * @return					Two lines, the entire input line followed by a line with an up arrow
	 */
	private static void displayInputState() {
		// Display the entire input line
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}

	/**********
	 * This method is a mechanical transformation of a Directed Graph diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for directed graph processing
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String evaluatePassword(String input) {
		// The following are the local variable used to perform the Directed Graph simulation
		passwordErrorMessage = "";
		passwordIndexofError = 0;			// Initialize the IndexofError
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		
		if(input.length() <= 0) return "*** Error *** The password is empty!";
		
		// The input is not empty, so we can access the first character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		passwordInput = input;				// Save a copy of the input
		foundUpperCase = false;				// Reset the Boolean flag
		foundLowerCase = false;				// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundSpecialChar = false;			// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundLongEnough = false;			// Reset the Boolean flag
		running = true;						// Start the loop

		System.out.println("Debug - Starting password evaluation for: " + input);

	    while (running) {
	        System.out.println("Debug - Current Character: " + currentChar);

	        if (currentChar >= 'A' && currentChar <= 'Z') {
	            foundUpperCase = true;
	            System.out.println("Debug - Uppercase letter found.");
	        } else if (currentChar >= 'a' && currentChar <= 'z') {
	            foundLowerCase = true;
	            System.out.println("Debug - Lowercase letter found.");
	        } else if (currentChar >= '0' && currentChar <= '9') {
	            foundNumericDigit = true;
	            System.out.println("Debug - Numeric digit found.");
	        } else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
	            foundSpecialChar = true;
	            System.out.println("Debug - Special character found.");
	        } else {
	            passwordIndexofError = currentCharNdx;
	            System.out.println("Debug - Invalid character found at index: " + currentCharNdx);
	            return "*** Error *** An invalid character has been found!";
	        }

	        if (currentCharNdx >= 7) {
	            foundLongEnough = true;
	            System.out.println("Debug - Password length requirement met.");
	        }

	        currentCharNdx++;
	        if (currentCharNdx >= inputLine.length()) {
	            running = false;
	        } else {
	            currentChar = input.charAt(currentCharNdx);
	        }
	    }

	    System.out.println("Debug - Password evaluation completed.");

	    // Check which conditions were not satisfied
	    String errMessage = "";
	    if (!foundUpperCase) {
	        errMessage += "Upper case; ";
	        System.out.println("Debug - Missing uppercase letter.");
	    }

	    if (!foundLowerCase) {
	        errMessage += "Lower case; ";
	        System.out.println("Debug - Missing lowercase letter.");
	    }

	    if (!foundNumericDigit) {
	        errMessage += "Numeric digits; ";
	        System.out.println("Debug - Missing numeric digit.");
	    }

	    if (!foundSpecialChar) {
	        errMessage += "Special character; ";
	        System.out.println("Debug - Missing special character.");
	    }

	    if (!foundLongEnough) {
	        errMessage += "Long Enough; ";
	        System.out.println("Debug - Password is too short.");
	    }

	    if (errMessage.isEmpty()) {
	        return "";
	    }

	    passwordIndexofError = currentCharNdx;
	    return errMessage + "conditions were not satisfied";
	}}