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
import utilities.SpotifyAPIInvalidDownloadException;
import utilities.SpotifyAPIInvalidStreamException;
import utilities.SpotifyAPIInvalidURLException;




/**
 * This class facilitates making calls to Spotify's API.
 * 
 * SpotifyAPI handles all of the necessary calls that our Music Player will need to make
 * to dynamically add songs to the Library. This class authenticates with the API, receives 
 * an authorization token that's used when making requests for data from the API, and then
 * this class can call the API to receive metadata in JSON format which we parse for the 
 * relevant information from the metadata. This class can parse the metadata received when
 * searching for a track and will parse the data for the song name, artist name, genre and
 * release date of the track. This class will also parse the metadata for a preview link and
 * the tracks artwork link and will download the artwork into src/images and will use the 
 * preview link to download the 30 second audio preview that we get from Spotify and will place
 * that audio file in .wav format into the audios folder in the project. This class also can 
 * throw multiple exceptions indicating a failed download, invalid link or an invalid stream. 
 * These exceptions shouldn't be thrown unless an error occurs when querying the API. This class
 * also contains no constructor, so Java provides the zero arg constructor for us, if you wish
 * to use it.
 * @author Seth Jeppson
 *
 * 
 */
public class SpotifyAPI {


	// hard coded constants
	/**
	 * String representing the clientID for Spotify API authentication process.
	 */
	private static final String clientID = "f8d521b06e3146e4ac81c0e72e4cf010";
	/**
	 * String representing the clientSecret token for Spotify API authentication process. 
	 */
	private static final String clientSecret = "0f054c9bef8c45a0ba131870cff369fa"; 
	/**
	 * String representing a callback address, which does nothing but is required for the API calls
	 */
	private static final String callback = "http://localhost:8888/callback";
	/**
	 * String representing the token type which is always Bearer
	 */
	private static final String tokType = "Bearer"; 
	/**
	 * String constant for the token, which will be updated after we receive our token.
	 */
	private static String token = "";
	/**
	 * String constant for the state, required for API calls, used for nothing in program. 
	 */
	private static String state = "7WwsUkOgugujRct7"; // not sure if I need it but saving it anyway
	

	
	/**
	 * getMetadata queries Spotify's API to search for a track based on the artist and song name.
	 * 
	 * This method will query the API for song metadata. The JSON metadata that is returned from
	 * Spotify is then parsed in a loop and we grab the artist name, song name, genre, release date
	 * artwork link and audio preview link. We create a Song object out of all of these pieces of 
	 * data from the API. This method also calls other methods to facilitate downloading the audio 
	 * preview and artwork image from the links given in the metadata and places those files
	 * in the src/images directory for the artwork and the audios folder for the audio in .wav format. 
	 * If at any point we can't grab all of the specified information or the song is contained in the 
	 * library already, we throw an exception. This method will also update data.txt with the new song
	 * information so when the program is closed and reopened, the program can load in every song it has
	 * before the user wants to add new songs. 
	 * @param artist Artist name we are searching for.
	 * @param songName Song name we are searching for.
	 * @return Song object if the song is successfully added to the library. 
	 * @throws SpotifyAPIInvalidURLException Exception that gets thrown if the URL is invalid according to the API.
	 * @throws SpotifyAPIInvalidStreamException Exception that gets thrown if the Stream we are given back isn't valid, i.e error.
	 * @throws SpotifyAPIInvalidDownloadException Exception that gets thrown if the song queried already exists in the library. 
	 */
	public static Song getMetadata(String artist, String songName) throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException, SpotifyAPIInvalidDownloadException  {
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
				} else if (output.contains("preview_url") && output.length() > 23) {
					prevLink = output.substring(23, output.length() -2);
				} else if (output.contains("release_date") && i <= 0) {
					date = output.substring(26, 36);
					i++;
				}
			}
			if (prevLink.equals("") || coverLink.equals("")) {
				throw new SpotifyAPIInvalidDownloadException("No preview link found. Try a different track.");
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
	
	/**
	 * This method appends to data.txt with new Songs that are added to the library.
	 * @param retval Song object to add info about to data.txt. 
	 */
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
			System.out.println("oh no"); // might change but this should never happen
		}
	}

	/**
	 * Returns the path of the location of the .wav file that is downloaded by this method.
	 * @param prevLink 30 Second preview link given from Spotify API
	 * @param artist Artists name
	 * @param name Name of the song
	 * @return String path to place into the Song object representing where the audio is for the Song.
	 * @throws SpotifyAPIInvalidURLException Thrown if URL is incorrect according to Spotify's API. 
	 */
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


	/**
	 * Returns the path of the location of the .jpg file that is downloaded by this method.
	 * @param coverLink Album artwork link given from Spotify API
	 * @param artist Artists name
	 * @param name Name of the song
	 * @return String path to place into the Song object representing where the audio is for the Song.
	 * @throws SpotifyAPIInvalidURLException Thrown if URL is incorrect according to Spotify's API. 
	 */
	private static String downloadArt(String coverLink, String artist, String name) throws SpotifyAPIInvalidURLException {
		try {
			URL url = new URL(coverLink);
			fetchContent(url, "src/images/"+ artist + "_" + name +  ".jpg");
		} catch (MalformedURLException e) {
			throw new SpotifyAPIInvalidURLException("Invalid path. Ensure the path is correct for saving the art.");
		}
		return "src/images/"+ artist + "_" + name +  ".jpg"; 
	}
	
	
	/**
	 * This method handles actually downloading the content either from an artwork link or preview link.
	 * @param url The URL we receive from Spotify API either for the artwork or 30 second audio preview.
	 * @param outputFileName The path to store the file in the project. 
	 */
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

	/**
	 * Returns a String representing the genre from the given artist which we send to get a URL to search
	 * just the artist, which is a slightly different call to the API than getMetadata but very similar. 
	 * @param artist Name of an artist
	 * @return returns the String representing the genre of the artist
	 * @throws SpotifyAPIInvalidURLException Thrown if the URL is invalid according to the API.
	 * @throws SpotifyAPIInvalidStreamException Thrown if the metadata isn't readable. 
	 */
	private static String getGenre(String artist) throws SpotifyAPIInvalidURLException, SpotifyAPIInvalidStreamException {
		String url = formArtistURL(artist);
		return parseGenre(url);
	}

	/**
	 * This method works very similarly to getMetadata but this query specifically queries an Artist and then only grabs
	 * the genre that artist is apart of and returns that String. 
	 * @param link
	 * @return String representing the genre of the artist. 
	 * @throws SpotifyAPIInvalidURLException Thrown if the URL is invalid according to the API.
	 * @throws SpotifyAPIInvalidStreamException Thrown if the metadata isn't readable. 
	 */
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
	
	/**
	 * Forms a valid URL according to Spotify's API to specifically search for a specific Artist
	 * only. Returns the formatted URL as a String at the end. 
	 * @param artist Name of the artist.
	 * @return String representing valid URL to query Spotify's API.
	 */
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

	/**
	 * Forms a valid URL according to Spotify's API to specifically search for a Track based on artist and 
	 * songName params. Returns the formatted URL as a String at the end. 
	 * @param artist Name of the artist.
	 * @param songName Name of the song.
	 * @return
	 */
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

	/**
	 * This method handles the first part of the authentication process for being able to query
	 * Spotify's API. This method will only throw an exception if the API service isn't working,
	 * i.e server or connection issues. 
	 * @throws SpotifyAPIInvalidURLException Thrown if the URL is invalid according to the API.
	 * @throws SpotifyAPIInvalidStreamException Thrown if the metadata isn't readable. 
	 */
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
	
	/**
	 * This method handles the second step of the authentication process, which is receiving our token,
	 * to be able to query the API for JSON metadata. We send information about our client along with
	 * the secret ID to receive our token so the API knows who this token is for. 
	 * @throws SpotifyAPIInvalidURLException
	 * @throws SpotifyAPIInvalidStreamException
	 */
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
