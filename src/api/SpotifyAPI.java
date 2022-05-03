package api;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javafx.application.Platform;
import song.Song;
import utilities.SpotifyAPIInvalidStreamException;
import utilities.SpotifyAPIInvalidURLException;

public class SpotifyAPI {


	// hard coded to use my(seths) info for the API calls 
	private static final String clientID = "f8d521b06e3146e4ac81c0e72e4cf010";
	private static final String clientSecret = "0f054c9bef8c45a0ba131870cff369fa"; 
	private static final String callback = "http://localhost:8888/callback";
	private static final String tokType = "Bearer"; 
	private static String token = "";
	private static String state = "7WwsUkOgugujRct7"; // not sure if I need it but saving it anyway
	
	
	
	
	public static String getSongDate(String artist, String songName) throws Exception {
		String link = formTrackURL(artist, songName);
		try {
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + token);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(con.getInputStream())));
			String date = "";
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				if (output.contains("release_date")) {
					date = output.substring(26, 36);
					break;
				}
			}
			con.disconnect();
			return date;
		} catch (MalformedURLException e) {
			throw new SpotifyAPIInvalidURLException("Invalid call to Spotify's API. Ensure the link is a valid SpotifyAPI URL");
		} catch (IOException e) {
			throw new SpotifyAPIInvalidStreamException();
		}
	}
	
	
	public static Song getMetadata(String artist, String songName) throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException  {
		String link = formTrackURL(artist, songName);
		try {
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + token);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(con.getInputStream())));
			String date = "";
			String coverLink = ""; 
			String prevLink = ""; 
			String output;
			int i = 0; 
			while ((output = br.readLine()) != null) {
				if (output.contains("images")) {
					output = br.readLine(); output = br.readLine(); // doing twice to get artwork image link. 
					coverLink = output.substring(19, output.length() -2);
				} else if (output.contains("preview_url")) {
					prevLink = output.substring(23, output.length() -2);
				} else if (output.contains("release_date") && i <= 0) {
					date = output.substring(26, 36);
					i++;
				}
			}
			String audioPath = downloadAudio(prevLink, artist, songName);
			String artPath = downloadArt(coverLink, artist, songName);
			String genre = getGenre(artist);
			Song retval = new Song(songName, artist, genre, artPath, date, audioPath); 
			// add method to write to data.txt
			updateData(retval);
			con.disconnect();
			return retval; 
		} catch (MalformedURLException e) {
			throw new SpotifyAPIInvalidURLException("Invalid call to Spotify's API. Ensure the link is a valid SpotifyAPI URL");
		} catch (IOException e) {
			throw new SpotifyAPIInvalidStreamException();
		}
	}

	private static void updateData(Song retval) {
		
		try {
			FileWriter fw = new FileWriter("data.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(retval.getName() + ", " + 
			retval.getArtist() + ", " + 
			retval.getGenre() + ", " +
			retval.getArtPath() + ", " +
			retval.getSongDate() + "," + 
			retval.getAudioPath());
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// returns path of the audio 
	private static String downloadAudio(String prevLink, String artist, String name) throws SpotifyAPIInvalidURLException {
		try {
			URL url = new URL(prevLink);
			fetchContent(url, "Audios/"+ artist + "_" + name +  ".wav"); // likely will get rid of artist maybe? 
		} catch (MalformedURLException e) {
			try {
				URL url = new URL(prevLink);
				fetchContent(url, "Audios/"+ artist + "_" + name +  ".mp3");
			} catch (MalformedURLException e2) { 
				throw new SpotifyAPIInvalidURLException("Invalid path. Ensure the path is correct for saving the audio.");
			}
			
		} 
		return "Audios/"+ artist + "_" + name +  ".wav";
	}


	// returns path of the artwork. 
	private static String downloadArt(String coverLink, String artist, String name) throws SpotifyAPIInvalidURLException {
		try {
			URL url = new URL(coverLink);
			fetchContent(url, "src/images/"+ artist + "_" + name +  ".jpg");
		} catch (MalformedURLException e) {
			throw new SpotifyAPIInvalidURLException("Invalid path. Ensure the path is correct for saving the art.");
		}
		return "src/images/"+ artist + "_" + name +  ".jpg"; 
	}
	
	
	
	private static void fetchContent(URL url, String outputFileName) {
		Runnable download = new Runnable() {
			@Override
			public void run() {
				try (InputStream in = url.openStream();
			            ReadableByteChannel rbc = Channels.newChannel(in);
			            FileOutputStream fos = new FileOutputStream(outputFileName)) {
			            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			        } catch (IOException e) {
						// need to figure this out
					}
				}	
			};
		Thread thread = new Thread(download);
		thread.start();
		
	}

	
	private static String getGenre(String artist) throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException {
		String url = formArtistURL(artist);
		return parseGenre(url);
	}

	private static String parseGenre(String link) throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException {
		String genre = ""; 
		try {
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + token);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(con.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				if (output.contains("genres")) {
					String[] split = output.split(",");
					genre = split[0];
				}
			}
		} catch (MalformedURLException e) {
			throw new SpotifyAPIInvalidURLException("Invalid call to Spotify's API. Ensure the link is a valid SpotifyAPI URL");
		} catch (IOException e) {
			throw new SpotifyAPIInvalidStreamException();
		}
		genre = genre.substring(20, genre.length() - 1);
		return genre;
	}

	private static String formArtistURL(String artist) {
		String uriAuth = "https://api.spotify.com/v1/search?q="; 
		String[] splitArtist = artist.split(" ");
		for (int i = 0; i < splitArtist.length; i++) {
			if (i == splitArtist.length - 1) {
				uriAuth = uriAuth + splitArtist[i];
			} else {
				uriAuth = uriAuth + splitArtist[i] + "%20";
			}
		}
		uriAuth = uriAuth + "&type=artist&market=US&limit=1"; 
		
		return uriAuth;
	}

	private static String formTrackURL(String artist, String songName) {
		String uriAuth = "https://api.spotify.com/v1/search?q="; 
		String[] splitName = songName.split(" ");
		String[] splitArtist = artist.split(" ");
		for (int i = 0; i < splitName.length; i++) {
			uriAuth = uriAuth + splitName[i] + "%20";
		}
		for (int i = 0; i < splitArtist.length; i++) {
			if (i == splitArtist.length - 1) {
				uriAuth = uriAuth + splitArtist[i];
			} else {
				uriAuth = uriAuth + splitArtist[i] + "%20";
			}
		}
		uriAuth = uriAuth + "&type=track&market=US&limit=1"; 
		return uriAuth;
	}


	public static void authenticate() throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException {
		String uriAuth = "https://accounts.spotify.com/authorize?"
				+ "client_id="+clientID+"&"
				+ "response_type=code&"
				+ "redirect_uri="+callback+"&"
				+ "scope=user-read-private%20user-read-email&"
				+ "state=7WwsUkOgugujRct7"; // might need to save this into var. 
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
			throw new SpotifyAPIInvalidURLException("Invalid call to Spotify's API. Ensure the link is a valid SpotifyAPI URL");
		} catch (IOException e) {
			throw new SpotifyAPIInvalidStreamException();
		}	
	}
	
	
	public static void getToken() throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException {
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
		} catch (MalformedURLException e) {
			throw new SpotifyAPIInvalidURLException("Invalid call to Spotify's API. Ensure the link is a valid SpotifyAPI URL");
		} catch (IOException e) {
			throw new SpotifyAPIInvalidStreamException();
		}
	}
	
	
}
