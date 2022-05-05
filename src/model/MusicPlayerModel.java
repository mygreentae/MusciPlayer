package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

import song.Song;
import utilities.PlayList;
import utilities.SongLibrary;

/**
 * This is the Model of the music player. It holds all of the 
 * User data regarding PlayLists, Queues, favorites, and recommended
 * songs
 * 
 * 
 * Properties:
 * curSong: 
 * The Song currently playing, when Model is initialized, 
 * it is null.
 * 
 * 
 * currentPlayList: 
 * The PlayList currently playing, null when initialized/
 * if Queue is playing.
 * 
 * allPlaylists: 
 * an ArrayList of all made PlayLists in the User's library,
 * Empty ArrayList when initialized until makePlaylist() function 
 * is called. 
 * 
 * favorites:
 * A PlayList of favorite songs, Empty ArrayList when initialized.
 * 
 * playingPlaylist: 
 * Boolean value for if PlayList is playing, null when initialized.
 * 
 * 
 * metadata:
 * A Map of Strings mapped to integers of genre's of Songs in a user's 
 * allPlaylists.
 * 
 * @author Seth/Jackson/Paris/Leighanna
 *
 */
@SuppressWarnings("deprecation")
public class MusicPlayerModel extends Observable{
	
	private SongLibrary songLibrary;
	
	private Song curSong; 
	
	private PlayList currentPlaylist;
	
	//features maybe?
	private ArrayList<PlayList> allPlaylists; 
	private PlayList favorites; 
	private PlayList recommended;

	//model state
	private boolean playingPlaylist;
	
	private Map<String, Integer> metadata;
	
	/**
	 * Creates the MusicPlayerModel
	 * 
	 * @param songLibrary, the songLibrary that saves Song data
	 */
	public MusicPlayerModel(SongLibrary songLibrary) {
		this.songLibrary = songLibrary;
		allPlaylists = new ArrayList<>();
		favorites = new PlayList("Favorites");
		recommended = new PlayList("Recommended");
		metadata = new HashMap<String, Integer>();
		
		// create playlist using songlibrary
		PlayList playlist = new PlayList(songLibrary.getSongs());
		allPlaylists.add(favorites);
		allPlaylists.add(playlist);
		

		//loads playlists from txt file
		ArrayList<PlayList> loadedPlaylists = songLibrary.getPlaylists();
		for (PlayList list : loadedPlaylists) {
			if (list.getName().equals("Favorites")) {
				for (Song song : list.getSongList()) {
					favorites.addSong(song);;
				}
			} else {
				allPlaylists.add(list);
			}
		}
			
	}


	
	/**
	 * Starts playing a PlayList,
	 * 
	 * This function plays PlayLists and only PlayLists. Way easier than the queue one
	 * Even though the PlayList class is like 5x longer.
	 * 
	 * @param playlist, the PlayList to be played
	 */
	public void playPlaylist(PlayList playlist) {
		currentPlaylist = playlist;
		playingPlaylist = true;
		curSong = playlist.getPlayOrder().get(0);
		

		setChanged();
		notifyObservers();
	}
	
	/**
	 * Starts playing a Playlist from a song. unsure if this works
	 * how intended
	 * 
	 * @param playlist, the PlayList to be played
	 * @param song, the Song to play in the PlayList
	 */
	public void playPlaylist(PlayList playlist, Song song) {
		currentPlaylist = playlist;
		playingPlaylist = true;
		curSong = song;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Plays a PlayList that is shuffled from a specific song
	 * 
	 * @param playlist, the PlayList to be played
	 * @param Shuffle, if the PlayList is shuffled
	 * @param song, the Song to start with
	 */
	public void playPlaylist(PlayList playlist, boolean Shuffle, Song song) {
		//stops other songs playing
		currentPlaylist = playlist;
		playingPlaylist = true;
		curSong = song;
		playlist.playFirst(song); //sets first song
		// plays entire playlist
		setChanged();
		notifyObservers();
	}
	/**
	 * Shuffles a PlayList 
	 * 
	 * @param playlist, the PlayList to be shuffled
	 */
	public void shufflePlayList(PlayList playlist) {
		playlist.shuffle();
	}
	
	/**
	 * Un-Shuffles a PlayList
	 * 
	 * @param playlist, the PlayList to be unshuffled
	 */
	public void unShuffle(PlayList playlist) {
		playlist.unshuffle();
	}
	
	/**
	 * This method is used when a use selects a song in say like an
	 * explore page or something. 
	 * 
	 * If we use a list of Songs as our Song library, and our user just 
	 * clicks a Song, we call this method.
	 * 
	 * If they specifically want to play a PlayList we use the above two functions. 
	 * 
	 * The issue I run into is if they want to click a song in a PlayList
	 * but its the first song they're picking. So the user wants to start 
	 * the PlayList at the song they're clicking. Thus I think what we might 
	 * have to do is handle it in the controller.
	 * 
	 * @param song, the Song we want to change to.
	 */
	public void changeSong(Song song) {
		
		// if a song is playing, stop it
		// if in Playlist, and playlist is currently being played
		if (playingPlaylist && currentPlaylist.contains(song)) {
			curSong = song;
			playPlaylist(currentPlaylist, song);
			
		} 
	}	
	
	
	/* 
	 * all of the getters that will be needed for the controller to call to
     * give to GUI
	 */
	
	/**
	 * Used to check if buttons can actually do anything in GUI
	 * For example, if isPlayingSong(), then the play button shouldnt 
	 * do anything, but if the song is paused, this should be false.
	 * 
	 * @return if a Song is playing
	 */
	public Boolean isPlayingSong() {
		if (curSong == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the current Song
	 * 
	 * @return the current Song
	 */
	public Song getCurSong() {
		return curSong;
	}
	
	/**
	 * Returns the current PlayList
	 * 
	 * @return the current PlayList, null if not playing PlayList
	 */
	public PlayList getCurPlaylist() {
		return currentPlaylist;
	}
	
	/**
	 * Returns all PlayList Objects in model
	 * 
	 * @return an ArrayList of all PlayLists
	 */
	public ArrayList<PlayList> getAllPlaylists(){
		return allPlaylists;
	}
	
	/**
	 * Returns the specific PlayList based on the name
	 * 
	 * @param name, the name of the desired PlayList
	 * @return the corresponding PlayList
	 */
	public PlayList getPlaylist(String name) {
		for (PlayList playlist : allPlaylists) {
			if (playlist.getName().toLowerCase().equals(name.toLowerCase())) {
				return playlist;
			}
		} 
		return null;
	}

	/**
	 * Returns a PlayList of Favorite Songs
	 * 
	 * @return a PlayList of Favorite Songs
	 */
	public PlayList getFavorites(){
		return favorites;
	}
	
	/**
	 * Returns a PlayList of Recommended Songs.
	 * 
	 * @return the PlayList of Recommended Songs.
	 */
	public PlayList getRecommended(){
		return recommended;
	}
	
	/**
	 * Returns true if a PlayList is Playing
	 * 
	 * @return true if a PlayList is Playing
	 */
	public boolean isPlayingPlaylist() {
		return playingPlaylist;
	}
		
	/**
	 * Adds a PlayList to the model
	 * 
	 * @param playlist, the PlayList to be added
	 */
	public void addPlaylist(PlayList playlist) {
		allPlaylists.add(playlist);
	}
	
	/**
	 * Removes a PlayList to the model
	 * 
	 * @param playlist, the PlayList to be removed
	 */
	public void removePlaylist(PlayList playlist) {
		if (allPlaylists.contains(playlist)) {
			allPlaylists.remove(playlist);
		}
	}
	
	/**
	 * Adds a Song to the favorites PlayList
	 * 
	 * @param song, the Song to be added
	 */
	public void addToFavorites(Song song) {
		song.makeFavorite();
		favorites.addSong(song);
	}
	
	/**
	 * Removes a Song to the favorites PlayList
	 * 
	 * @param song, the Song to be removed
	 */
	public void removeFromFavorites(Song song) {
		if (favorites.contains(song)) {
			song.unFavorite();
			favorites.removeSong(song);
		}
	}
	
	/**
	 * Adds a song to a PlayList
	 * 
	 * @param playlist, selected PlayList
	 * @param song, Song to be added
	 */
	public void addToPlaylist(PlayList playlist, Song song) {
		playlist.addSong(song);
	}

	/**
	 * Sorting Playlists functions
	 */

	/**
	 * Sorts the songLibrary by song Name
	 * 
	 * @param playlist, the PlayList to be sorted
	 */
	public void sortTitle(PlayList playlist){
		playlist.sortTitle();
	}		
	
	/**
	 * Sorts the songLibrary by song Artist
	 * 
	 * @param playlist, the PlayList to be sorted
	 */
	public void sortArtist(PlayList playlist){
		
		playlist.sortArtist();
	}
	
	/**
	 * Sorts the songLibrary by song release Date
	 * 
	 * @param playlist, the PlayList to be sorted
	 */
	public void sortDate(PlayList playlist){
		
		playlist.sortDate();
	}
	
	/**
	 * Returns a string of all the playlists names
	 * 
	 * @return the string that has all the playlists names
	 */
	public String getAllPlaylistsAsString() {
		StringBuilder sb = new StringBuilder();
		
		for (PlayList playlist : allPlaylists) {
			sb.append("-");
			sb.append(playlist.getName());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
