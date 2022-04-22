package utilities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import song.Song;

public class PlayList {
	
	private String name;
	private int size;
	private ArrayList<Song> songList;
	
	// playing songs;
	private ArrayList<Song> shuffle;
	private ArrayList<Song> playOrder;
	private Song currentSong;
	
	//metadata
	//Hashmap that allows person to know what dominant genre a playlist is
	// add meta data to Model to track user data
	private HashMap<String, Integer> genres;
	
	/**
	 * Creates the PlayList Object
	 * 
	 * @param name, the name of the playlist
	 */
	public PlayList(String name) {
		this.name = name;
		this.size = 0;
		this.songList = new ArrayList<Song>();
		this.shuffle = new ArrayList<Song>();
		this.playOrder = new ArrayList<Song>();
		this.genres = new HashMap<String, Integer>();
	}
	
	/**
	 * Returns the name of the PlayList
	 * @return the name of the PlayList
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the size of the PlayList
	 * @return the size of the PlayList
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Adds a song to the Playlist
	 * 
	 * @param song, the Song to be added to the PlayList
	 */
	public void addSong(Song song) {
		songList.add(song);
		this.size += 1;
		
		//metadata

        String genre = song.getGenre();
        
        PlayList.incrementValue(genres, genre);
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
		if (!this.songList.contains(song)) {
			return;
		}
		songList.remove(song);
		this.size -= 1;
		
		//metadata removes number from genre or removes it completely
		 String genre = song.getGenre();
	     PlayList.decrementValue(genres, genre);
	}
	
	
	/**
	 * Returns the genre metadata about the Playlist
	 * 
	 * @return the genre's within the Playlist
	 */
	public Map<String, Integer> getGenres(){
		return genres;
		
	}
	
	/**
	 * Puts the Songs in a random Play Order
	 */
	public void shuffle() {
		//shuffles with Set
		playOrder = new ArrayList<Song>();
		shuffle = new ArrayList<Song>();
		
		for (Song song : songList) {
			shuffle.add(song);
		}
		Random random = new Random();
		while (shuffle.size() > 0) {
			int i = random.nextInt(shuffle.size());
			Song song = shuffle.get(i);
			playOrder.add(song);
			shuffle.remove(i);
		}		
	}
	
	/**
	 * Returns the order in which to play the songs
	 * @return a List of Songs to play
	 */
	public ArrayList<Song> getPlayOrder(){
		if (this.playOrder.size() == 0) {
			return this.songList;
		} else {
			return this.playOrder;
		}
	}
	
	/**
	 * 
	 * @param <K>
	 * @param map
	 * @param key
	 */
	private static<K> void incrementValue(Map<K, Integer> map, K key)
    {
        // get the value of the specified key
        Integer count = map.get(key);
 
        // if the map contains no mapping for the key,
        // map the key with a value of 1
        if (count == null) {
            map.put(key, 1);
        }
        // else increment the found value by 1
        else {
            map.put(key, count + 1);
        }
    }
	
	/**
	 * 
	 * @param <K>
	 * @param map
	 * @param key
	 */
	private static<K> void decrementValue(Map<K, Integer> map, K key)
    {
        // get the value of the specified key
        Integer count = map.get(key);
        
        if (count == 1) {
        	map.remove(key);
        }

        // else decrement the found value by 1
        else {
            map.put(key, count - 1);
        }
    }

}
	
