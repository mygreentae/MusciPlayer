package song;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 * @author Seth 
 *
 */

public class Song {

	//properties
	private static Clip audio;
	private static AudioInputStream stream;
	private String name;
	private String artist; 
	private String cover; // temporary for now.
	private int length; // in seconds, needed for song play delay
	private ArrayList<String> lyrics;
	
	
	//List shit
	private Song next;
	private Song prev;
	
	//metadata
	private String genre;
	private Boolean favorite;
	
	private boolean isPlaying; 
	
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
	
	
	public Song getNext() {
		if (next == null) {
			return null;
		} else {
			return this.next;
		}
	}
	
	public Song getPrev() {
		if (prev == null) {
			return null;
		} else {
			return this.prev;
		}
	}
	
	public void setNext(Song song) {
		this.next = song;
	}
	
	public void setPrev(Song song) {
		this.prev = song;
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
		try {
			stream = AudioSystem.getAudioInputStream(new File("yeat.wav").getAbsoluteFile());
			audio = AudioSystem.getClip();
			audio.open(stream);
			audio.start();
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Stops the Song audio
	 */
	public void stop() {
		audio.stop();
	}
	
	
}

