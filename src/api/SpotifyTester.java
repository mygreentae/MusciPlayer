package api;

public class SpotifyTester {

	public static void main(String[] args) {
		SpotifyBuilder sb = new SpotifyBuilder(); 
		SpotifyBuilder.authenticate();
		SpotifyBuilder.requestAccessToken(); // will set it in sb
		SpotifyBuilder.requestAccessToken(); // will set it in sb
		sb.getTrackInfo("",  "");
	}

}
