package utilities;

/**
 * This will be thown when there is an Invalid URL passed into the Spotify API
 * 
 * @author Seth
 */

public class SpotifyAPIInvalidURLException extends Exception {

	
	public SpotifyAPIInvalidURLException(String message) {
		super(message);
	}
	
	
	/**
	 * The message to be printed out if this exception is thrown
	 */
	public String toString() {
		return "Invalid url. " + super.getMessage();
	}
}
