package utilities;

/**
 * Handles the an invalid stream from the Spotify API
 * @author Seth
 *
 */
public class SpotifyAPIInvalidStreamException extends Exception{

	public SpotifyAPIInvalidStreamException() {
		super();
	}
	
	
	/**
	 * The print out that the user will see if this exception is thrown
	 */
	public String toString() {
		return "Invalid stream detected. This could be due to an invalid "
				+ "URL or an invalid preview link given from the API."
				+ "Attempt to call the API again and ensure params "
				+ "are correct according to documentation";
	}
}
