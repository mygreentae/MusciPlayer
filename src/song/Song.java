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
 * mentioned below. ArrayList compatible, Linked List compatible, 
 * 
 * Properties:
 * name, artist, coverImage, duration, lyrics?, audio file,
 * 
 * ArrayList Compatibility:
 * Utilizes an index so that it is usable in Lists.
 * 
 * Metadata: 
 * favorite used in model as special PlayList
 * genre used as recommended songs metadata
 *
 */

public class Song {

	//properties
	private String name;
	private String artist; 
	private int date; //probably just a year
	private String cover; // temporary for now.
	private ArrayList<String> lyrics;
	
	//List shit
	private int index;
	
	//metadata
	private String genre;
	private Boolean favorite;
	private String art;
	private String audioPath; 
	private String songDate;

	/**
	 * @param name, the name of the Song
	 * @param artist, the artist of the Song
	 */
	public Song(String name, String artist, String genre, String artPath, String songDate, String audioPath) {
		this.name = name;
		this.artist = artist; 
		this.date = 0;
		this.genre = genre;
		this.index = 0;
		this.favorite = false;
		this.art = artPath;
		this.audioPath = audioPath; 
		this.songDate = songDate; 
	}
	
	
	
	/**
	 * Returns the release date of the Song
	 * 
	 * @return the release date for the Song
	 */
	public String getSongDate() {
		return songDate;
	}
	
	
	/**
	 * Returns the path of the artwork for the Song
	 * 
	 * @return the path of the artwork for the Song
	 */
	public String getArtPath() {
		return art;
	}
	
	/**
	 * Returns the path of the audio for the Song
	 * 
	 * @return the path of the audio for the Song
	 */
	public String getAudioPath() {
		return audioPath; 
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
	 * Returns the Song's release year
	 * 
	 * @return the Song release year
	 */
	public int getDate() {
		return date;
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
	 * Sets the cover Image URL
	 * 
	 */
	public void setCover(String url) {
		cover = url;
	}
	
	/**
	 * Returns the cover Image URL
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
	 * Returns the Song's index
	 * 
	 * @return the Song's index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the Song's index to whatever parameter is passed.
	 * 
	 * @param index, an int indicating the Song's index in a List
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Adds this Song to a playList, also probably not needed
	 * 
	 * @param playlist
	 */
	public void addToPlaylist(PlayList playlist) {
		playlist.addSong(this);
	}	
}
