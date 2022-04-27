package api;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SpotifyBuilder {
	
	private static final String clientID = "f8d521b06e3146e4ac81c0e72e4cf010";
	private static final String clientSecret = "0f054c9bef8c45a0ba131870cff369fa"; 
	private static final String callback = "https://github.com/spr22CSC335/MusicPlayerSethLeighannaJacksonParis";
	private static final String tokType = "Bearer"; 
	private static String token = "";
	
	public SpotifyBuilder() {
		
	}
	
	
	public void getTrackInfo(String artist, String type) { // add parameters so we can look up any song from passed in data. 
		String uriAuth = "https://api.spotify.com/v1/search?q="
				+ "Drake" +"&type="+"album";
		try {
			URL url = new URL(uriAuth);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", token);
			//con.setRequestProperty("Authorization", tokType + " " + token);
			String info = "grant_type=client_credentials&client_id="
					+ clientID + "&client_secret="
					+ clientSecret +"";
			byte[] out = info.getBytes(StandardCharsets.UTF_8);
			OutputStream stream = con.getOutputStream();
			stream.write(out);
			InputStream temp = con.getInputStream();
			String tmp = new String(temp.readAllBytes());
			System.out.println(tmp);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void requestAccessToken() {
		if (token != "") {
			return; 
		}
		String uriAuth = "https://accounts.spotify.com/api/token";
		try {
			URL url = new URL(uriAuth);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true); 
			con.setDoInput(true);
			//con.setRequestProperty("Authorization", "Basic " + clientID + ":" + clientSecret);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			String info = "grant_type=client_credentials&client_id="
					+ clientID + "&client_secret="
					+ clientSecret +"";
			byte[] out = info.getBytes(StandardCharsets.UTF_8);
			OutputStream stream = con.getOutputStream();
			stream.write(out);
			InputStream temp = con.getInputStream();
			token = new String(temp.readAllBytes());
			String[] tmp = token.split(",");
			String tok = tmp[0].split(":")[1].substring(1);
			tok = tok.substring(0, tok.length() - 1);
			token = tok; 
			System.out.println(token);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void authenticate() {
		String uriAuth = "https://accounts.spotify.com/authorize?"
				+ "client_id="+clientID+"&"
				+ "response_type=code&"
				+ "redirect_uri="+callback+"&"
				+ "scope=user-read-private%20user-read-email&"
				+ "state=7WwsUkOgugujRct7"; // might need to save this into var. 
		System.out.println(uriAuth);
		try {
			URL url = new URL(uriAuth);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			if (con.getResponseCode() != 200) {
				throw new RuntimeException("Failed: " + con.getResponseCode());
			}
			System.out.println(con.getResponseCode());
			con.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("connection error or input stream issue");
			e.printStackTrace();
		}	
	}
	
}
