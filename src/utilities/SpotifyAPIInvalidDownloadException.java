package utilities;

/**
 * Handles an invalid download from the Spotify API
 * 
 * @author Seth
 *
 */
public class SpotifyAPIInvalidDownloadException extends Exception {


	public SpotifyAPIInvalidDownloadException(String message) {
		super(message);
	}
	
	
	/**
	 * The message to be printed if this exception is thrown
	 */
	public String toString() {
		return "Invalid download detected. " + super.getMessage();
	}
}

