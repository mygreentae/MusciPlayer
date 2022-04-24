package song;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;

import utilities.PlayList;

/**
 * @author Seth/Jackson
 * 	
 * Holds a Song and all relevant information relating to it. Has dual functions 
 * mentioned below. 
 * 
 * Properties:
 * name, artist, coverImage, duration, lyrics?, audio file,
 * 
 * Linked List Compatibility:
 * Utilizes .prev and .next in order to use a "live" Queue System
 * 
 * Metadata: 
 * favorite used in model as special playlist
 * genre used as recommended songs metadata
 *
 */

public class Song {

	//properties
	private Clip audio;
	private AudioInputStream stream;
	private String name;
	private String artist; 
	private String cover; // temporary for now.
	private double durationInSeconds; // in seconds, needed for song play delay
	private ArrayList<String> lyrics;
	private boolean isPlaying; 
	
	//List shit
	private Song next;
	private Song prev;
	
	//metadata
	private String genre;
	private Boolean favorite;

	/**
	 * @param name, the name of the Song
	 * @param artist, the artist of the Song
	 */
	public Song(String name, String artist, String genre) {
		this.name = name;
		this.artist = artist; 
		this.genre = genre;
		this.favorite = false;
	}
	
	/**
	 * Returns the name of the Song
	 * 
	 * @return the name of the Song
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the Song's artist
	 * 
	 * @return the Song artist
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * Returns the Song's genre
	 * 
	 * @return the Song genre
	 */
	public String getGenre() {
		return genre;
	}
	
	/**
	 * Returns the cover Image
	 * 
	 * @return the Song  over
	 */
	public String getCover() {
		return cover;
	}
	
	/**
	 * Marks Song as a favorite
	 */
	public void makeFavorite() {
		this.favorite = true;
	}
	
	/**
	 * Unmarks Song as a favorite
	 */
	public void unFavorite() {
		this.favorite = false;
	}
	
	/**
	 * Returns if the Song is a favorite Song
	 * 
	 * @return returns if the Song is a favorite Song
	 */
	public Boolean isFavorite() {
		return this.favorite;
	}
	
	/**

	 * Sets the Song to isPlaying status
	 */
	public void setPlaying() {
		isPlaying = true; 
	}
	
	/**
	 * Returns if the Song is playing
	 * 
	 * @return true if the Song is playing
	 */
	public boolean isPlaying() {
		return isPlaying;
	}
	
	/**
	 * Sets the Song to notPlaying status
	 */
	public void notPlaying() {
		isPlaying = false; 
	}
	
	/**
	 * Gets the next Song if any
	 * 
	 * @return the next Song if applicable
	 */
	public Song getNext() {
		if (next == null) {
			return null;
		} else {
			return this.next;
		}
	}
	
	/**
	 * Gets the previous Song if any
	 * 
	 * @return the previous Song if applicable
	 */
	public Song getPrev() {
		if (prev == null) {
			return null;
		} else {
			return this.prev;
		}
	}
	
	/**
	 * Sets this instance's next Song
	 * 
	 * @param song, the next Song
	 */
	public void setNext(Song song) {
		this.next = song;
	}
	
	/**
	 * Sets this instance's previous Song
	 * 
	 * @param song, the Song previous to this
	 */
	public void setPrev(Song song) {
		this.prev = song;
	}
	
	public double getDuration() {
		return durationInSeconds;
	}
	
	
	
	/*
	 * will probably have to be used in gui function like displayCover() or something
	public void setCoverStream(String fileName) {
		//Passing FileInputStream object as a parameter 
		FileInputStream inputstream = new FileInputStream(fileName); 
		Image image = new Image(inputstream);
		//Loading image from URL 
		//Image image = new Image(new FileInputStream("url for the image));
	}
	*/
	
	/**
	 * Sets Song audio to specific .wav file
	 * 
	 * @param fileName, the name of the liked .wav file
	 */
	public void setAudioStream(String fileName) {
		try {
			stream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
			audio = AudioSystem.getClip();
			audio.open(stream);
			AudioFormat format = stream.getFormat();
			long frames = stream.getFrameLength();
			durationInSeconds = (frames+0.0) / format.getFrameRate();
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
	}
	
	/**
	 * Plays the song audio
	 */
	public void play() {
		// if we have to manually hardcode Song objects using setStream that I, jackson
		// have made just now, we can have play just start the stream
		audio.start();
		try {
			TimeUnit.SECONDS.sleep((long) durationInSeconds + 1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Stops the Song audio
	 */
	public void stop() {
		audio.stop();
		audio.setFramePosition(0);
	}
	
	/**
	 * maybe works
	 */
	public void pause() {
		audio.stop();
	}
	
	
	/**
	 * gonna hope this works
	 * 
	 * @param playlist
	 */
	public void addToPlaylist(PlayList playlist) {
		playlist.addSong(this);
	}	
}

