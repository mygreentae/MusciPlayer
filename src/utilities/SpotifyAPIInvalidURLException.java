package utilities;

/**
 * This class represents an Exception for our Wordle game for an invalid guess. This exception will be thrown
 * if the users guess is either less than 5 letters, more than 5 letters or if their guess is not contained 
 * inside of our Dictionary.txt file.
 * 
 * @author Seth Jeppson
 */

public class SpotifyAPIInvalidURLException extends Exception {

	
	public SpotifyAPIInvalidURLException(String message) {
		super(message);
	}
	
	
	
	public String toString() {
		return "Invalid url. " + super.getMessage();
	}
}
