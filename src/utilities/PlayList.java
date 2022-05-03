package utilities;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import song.Song;

/**
 * 
 * @author Jackson
 *
 *	A data structure used to hold Song objects in a list that 
 *	can be shuffle played. Utilizes indexing feature of Song 
 *	in order to play PlayList Songs. 
 *	
 *	NOTE: This class doesn't actually use indexing to go 
 *	through the Songs, it uses a for each loop. To be able to 
 *	play a PlayList from a specific Song, Song.getIndex() is 
 *	required, but is handled in the Model.
 *	
 *	
 *	Properties: 
 *	name: 
 *	The name of the PlayList, which is assigned when initialized.
 *
 *	size:
 *	The length of the PlayList, 0 when initialized.
 *	
 *	songList:
 *	The list of Songs added to the PlayList. Empty ArrayList
 *	when initialized. This ArrayList keeps the Songs in the 
 *	order that they were added to the PlayList.
 *	
 *	shuffle:
 *	This is also an ArrayList of Songs. It's just a temporary
 *	variable that is used to shuffle a PlayList. It is also used
 *	in playFirst() but I really don't think I need that function.
 *
 *	playOrder:
 *	The order in which the songs will be played. It is used in
 *	shuffle because playOrder will be the random order for the 
 *	PlayList. Also in playFirst() but again, don't think we 
 *	need it.
 *	
 *	genres:
 *	A HashMap of Strings mapped to Integers, for how many songs
 *	of a specific genre are in the PlayList.
 */
public class PlayList {
	
	private String name;
	private int size;
	private ArrayList<Song> songList;
	
	// playing songs;
	private ArrayList<Song> shuffle;
	private ArrayList<Song> playOrder;
	private ArrayList<Song> originalOrder;
	
	// metadata
	// HashMap that allows person to know what dominant genre a PlayList is
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
		this.originalOrder = new ArrayList<Song>();
	}
	
	public PlayList(ArrayList<Song> songList) {
		this.name = "Song Library";
		this.size = 0;
		this.songList = new ArrayList<Song>();
		this.shuffle = new ArrayList<Song>();
		this.playOrder = new ArrayList<Song>();
		this.genres = new HashMap<String, Integer>();
		this.originalOrder = new ArrayList<Song>();
		
		for (Song song: songList) {
			addSong(song);
		}
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
	
	public void setSongList(ArrayList<Song> songs){
		this.songList = songs;
	}
	
	
	/**
	 * Returns the list of Songs in the PlayList
	 * @return the list of Songs in the PlayList
	 */
	public ArrayList<Song> getSongList(){
		return this.songList;
	}
	
	/**
	 * Adds a song to the PlayList
	 * 
	 * @param song, the Song to be added to the PlayList
	 */
	public void addSong(Song song) {
		playOrder = songList;
		songList.add(song);
		originalOrder.add(song);
		this.size += 1;
		song.setIndex(this.size - 1);
		
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
		originalOrder.remove(song);
		this.size -= 1;
		
		//metadata removes number from genre or removes it completely
		 String genre = song.getGenre();
	     PlayList.decrementValue(genres, genre);
	}
	
	/**
	 * Returns if the PlayList contains the Song
	 * 
	 * @param song, a Song
	 * @return true if PlayList contains song
	 */
	public boolean contains(Song song) {
		if (songList.contains(song)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the genre metadata about the PlayList
	 * 
	 * @return the genre's within the PlayList
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
		int index = 0;
		while (shuffle.size() > 0) {
			int i = random.nextInt(shuffle.size());
			Song song = shuffle.get(i);
			song.setIndex(index);
			index += 1;
			playOrder.add(song);
			shuffle.remove(i);
		}		
	}
	
	
	public void unshuffle() {
		playOrder = songList;
		int count = 0;
		for (Song song : songList) {
			song.setIndex(count);
			count += 1;
		}
	}
	
	
	/**
	 * This is used to reassign the song that plays first in the 
	 * PlayList. might not be necessary. WE DO NOT NEED THIS
	 * 
	 * It is mostly used for when a user clicks on a song in a PlayList, 
	 * whilst playing said PlayList. It just shifts where the song used
	 * to be and moves it to the front.
	 * 
	 * @param song, the song select to be first
	 */
	public void playFirst(Song song) {
		shuffle = new ArrayList<Song>();
		shuffle.add(song);
		song.setIndex(0);
		
		int index = 1;
		for (Song s: playOrder) {
			if (s != song) {
				shuffle.add(s);
				s.setIndex(index);
				index += 1;
			}
		}
		playOrder = shuffle;
		for (Song s: playOrder) {
			System.out.println(s.getName());
		}
	}
	
	
	/**
	 * Returns the order in which to play the songs
	 * 
	 * @return a List of Songs to play
	 */
	public ArrayList<Song> getPlayOrder(){
		if (this.playOrder.size() == 0) {
			return this.songList;
		} else {
			return this.playOrder;
		}
	}
	
	public void resetPlayOrder(){
		this.playOrder = this.songList;
	}
	
	/**
	 * Takes in a differently sorted ArrayList<Song> 
	 */
	public void sortPlaylist(ArrayList<Song> list) {
		this.songList = list;
		this.playOrder = list;
	}
	
	/**
	 * Sorts the songLibrary alphabetically by title
	 */
	public void sortTitle(){
		
		ArrayList<String> titleList = new ArrayList<String>();
		
		for (Song song : songList) {
			titleList.add(song.getName());
		}
		Collections.sort(titleList);
		
		
		ArrayList<Song> sortedOrder = new ArrayList<Song>();
		for (String title : titleList) {
			for (Song song : songList) {
				if (song.getName() == title) {
					sortedOrder.add(song);
				}
			}
		}
		//playOrder = sortedOrder;
		songList = sortedOrder;
	}
	
	/**
	 * Sorts the songLibrary by artist
	 */
	public void sortArtist(){
		
		ArrayList<String> artistList = new ArrayList<String>();
		
		for (Song song : songList) {
			artistList.add(song.getArtist());
		}
		Collections.sort(artistList);
		
		
		ArrayList<Song> sortedOrder = new ArrayList<Song>();
		for (String artist : artistList) {
			for (Song song : songList) {
				if (song.getArtist() == artist) {
					sortedOrder.add(song);
				}
			}
		}
		//playOrder = sortedOrder;
		songList = sortedOrder;
	}
	
	/**
	 * Sorts the songLibrary by song release Date
	 */
	public void sortDate(){
		
		ArrayList<Integer> dateList = new ArrayList<Integer>();
		for (Song song : songList) {
			//dateList.add(song.getDate());
		}
		Collections.sort(dateList);
		
		
		ArrayList<Song> sortedOrder = new ArrayList<Song>();
		for (int date : dateList) {
			for (Song song : songList) {
				if (song.getDate() == date) {
					sortedOrder.add(song);
				}
			}
		}
		//playOrder = sortedOrder;
		songList = sortedOrder;
	}
	
	public void returnToOriginalOrder() {
		songList = new ArrayList<Song>();
		for (Song song: originalOrder) {
			songList.add(song);
		}
	}
	
	public ArrayList<Song> getOriginalOrder(){
		return originalOrder;
	}
	
	/**
	 * Increments the value of a key in a Map
	 * 
	 * @param <K>, Generic for keys 
	 * @param map, the Map
	 * @param key, the specific key whose value is to be incremented
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
	 * Decrements the value of a key in a Map
	 * 
	 * @param <K>, Generic for keys 
	 * @param map, the Map
	 * @param key, the specific key whose value is to be decremented
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
	
