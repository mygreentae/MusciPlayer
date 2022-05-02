package utilities;

public class SpotifyAPIInvalidDownloadException extends Exception {


	public SpotifyAPIInvalidDownloadException(String message) {
		super(message);
	}
	
	
	
	public String toString() {
		return "Invalid download detected. " + super.getMessage();
	}
}

