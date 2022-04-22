package utilities;

import java.util.ArrayList;
import java.util.Set;

import song.Song;

public class PlayList {
	
	private String name;
	private int size;
	private ArrayList<Song> songList;
	
	// playing songs;
	private Set<Song> shuffle;
	private ArrayList<Song> playOrder;
	private Song currentSong;
	
	//metadata
	//Hashmap that allows person to know what dominant genre a playlist is
	// add meta data to Model to track user data
	private ArrayList<String> genres;
	
	/**
	 * Creates the PlayList Object
	 * 
	 * @param name, the name of the playlist
	 */
	public PlayList(String name) {
		this.name = name;
		this.size = 0;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public ArrayList<Song> getPlayOrder(){
		if (this.playOrder != null) {
			return this.playOrder;
		}
		return this.songList;
	}
	
	public Song getCurrentSong() {
		return this.currentSong;
	}
	
	/**
	 * Adds a song to the Playlist
	 * 
	 * @param song, the Song to be added to the PlayList
	 */
	public void addSong(Song song) {
		songList.add(song);
		this.size += 1;
	}
	
	/**
	 * Removes a specified song from a PlayList
	 * 
	 * @param song, song to be removed
	 */
	public void removeSong(Song song) {
		if (size == 0) {
			return;
		}
		songList.remove(song);
		this.size -= 1;
	}
	
	//shuffle and play in order might need to not be in the PLaylist model
	//same thing with stop
	// we'll see
	
	/**
	 * Plays songs in added Order
	 */
	public void playInOder() {
		this.playOrder = songList;
		for (Song song : songList) {
			song.play();
			currentSong = song;
			//somehow stop the song 
			while (song.isPlaying()) {
				//do some random thing
				System.out.println("playing");
			}
		}
	}
	
	/**
	 * Plays songs in random order
	 */
	public void shuffle() {
		//shuffles with Set
		for (Song song : songList) {
			shuffle.add(song);
		}
		//random makes playOrder
		for (Song song : shuffle) {
			playOrder.add(song);
		}
		//plays song
		for (Song song : playOrder) {
			song.play();
			currentSong = song;
			//somehow stop the song 
			while (song.isPlaying()) {
				//do some random thing that relies on Song.length
				//if(something){
				//song.stop()
				//}
				System.out.println("playing");
			}
		}
	}
	
	
	public void stop() {
		for (Song song : playOrder) {
			song.stop();
		}
		currentSong = null;
		playOrder = null;
	}
	
}
