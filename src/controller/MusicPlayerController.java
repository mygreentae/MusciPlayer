package controller;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;



/**
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
 * favorite used in model as special playlist
 * genre used as recommended songs metadata
 * 
 * @author Jackson/Seth/Leighanna
 *
 */
public class MusicPlayerController {

	
	private MusicPlayerModel model; 
	
	/**
	 * Creates a model
	 * 
	 * @param model, the model to be controlled. 
	 */
	public MusicPlayerController(MusicPlayerModel model) {
		this.model = model; 
	}
			       	
	/**
	 * Starts playing a Playlist,
	 * 
	 * This function plays PlayLists and only PlayLists. song will be 
	 * default null unless the User wants to start playing a playlist 
	 * from a specific song. This method will be used every time the
	 * User clicks a song in the playlist. 
	 * 
	 * to shuffle after playing a specific song, do model.shuffle
	 * 
	 * @param playlist, the PlayList to be played
	 * @param shuffle, if the user wants to shuffle the PlayList
	 * @param song, The Song a user wants to start with
	 */
	public void playPlaylist(PlayList playlist, Boolean shuffle, Song song) {
		if(shuffle) {
			model.shufflePlayList(playlist);
		} else {
			model.unShuffle(playlist);
		}
		
		if (song == null) {
			model.playPlaylist(playlist);
		} else if (shuffle){
			model.playPlaylist(playlist, shuffle, song);
		} else {
			model.playPlaylist(playlist, song);
		}
	}
	
	
	/**
	 * This method is used when a use selects a song in say like an
	 * explore page or something. 
	 * 
	 * If we use a list of Songs as our Song library, and our user just 
	 * clicks a Song, we call this method.
	 * 
	 * THIS IS BASICALLY JUST CONTROLLER.PLAY()
	 * 
	 * But it is dual function. You can use it to play a single song,
	 * or to change to a specific song while already playing a PlayList
	 * 
	 * If they specifically want to play a PlayList we use the above function. 
	 * 
	 * The issue I run into is if they want to click a song in a PlayList
	 * but its the first song they're picking. So the user wants to start 
	 * the PlayList at the song they're clicking. Thus I think what we might 
	 * have to do is handle it in the controller.
	 * 
	 * @param song, the Song we want to change to.
	 */
	public void changeSong(Song song) {
		model.changeSong(song);
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
		return model.isPlayingSong();
	}
	
	
	/**
	 * Returns the current Song
	 * 
	 * @return the current Song
	 */
	public Song getCurSong() {
		return model.getCurSong();
	}
	
	/**
	 * Returns the current PlayList
	 * 
	 * @return the current PlayList, null if not playing PlayList
	 */
	public PlayList getCurPlaylist() {
		return model.getCurPlaylist();
	}
	
	/**
	 * Returns all PlayList Objects in model
	 * 
	 * @return an ArrayList of all PlayLists
	 */
	public ArrayList<PlayList> getAllPlaylists(){
		return model.getAllPlaylists();
	}
	
	/**
	 * Returns the specific PlayList based on the name
	 * 
	 * @param name, the name of the desired PlayList
	 * @return the corresponding PlayList
	 */
	public PlayList getPlaylist(String name) throws IllegalArgumentException{
		PlayList playlist = model.getPlaylist(name);
		if (playlist != null) {
			return playlist;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Returns a list of all the names of the playlists as a string
	 * @return a string of the list of plaulists
	 */
	public String getAllPlaylistsAsString() {
		return model.getAllPlaylistsAsString();
	}

	/**
	 * Returns a PlayList of Favorite Songs
	 * 
	 * @return a PlayList of Favorite Songs
	 */
	public PlayList getFavorites(){
		return model.getFavorites();
	}
	
	/**
	 * Returns a PlayList of Recommended Songs.
	 * 
	 * @return the PlayList of Recommended Songs.
	 */
	public PlayList getRecommended(){
		return model.getRecommended();
	}
	
	/**
	 * Returns true if a PlayList is Playing
	 * 
	 * @return true if a PlayList is Playing
	 */
	public boolean isPlayingPlaylist() {
		return model.isPlayingPlaylist();
	}
	
	
	
	/**
	 * Creates a PlayList with name, name.
	 * 
	 * @param name, the name of the PlayList
	 */
	public void makePlaylist(String name) {
		PlayList newPlaylist = new PlayList(name);
		model.addPlaylist(newPlaylist);
	}
	
	/**
	 * Removes a PlayList to the model
	 * 
	 * @param playlist, the PlayList to be removed
	 */
	public void removePlaylist(PlayList playlist) {
		model.removePlaylist(playlist);;
	}
	
	/**
	 * Adds a Song to the favorites PlayList
	 * 
	 * @param song, the Song to be added
	 */
	public void addToFavorites(Song song) {
		model.addToFavorites(song);
	}
	
	/**
	 * Removes a Song to the favorites PlayList
	 * 
	 * @param song, the Song to be removed
	 */
	public void removeFromFavorites(Song song) {
		model.removeFromFavorites(song);
	}
	
	/**
	 * Adds a song to a PlayList
	 * 
	 * @param playlist, selected PlayList
	 * @param song, Song to be added
	 */
	public void addToPlaylist(PlayList playlist, Song song) {
		model.addToPlaylist(playlist, song);
	}
	
	/*
	 * The sorting functions for PlayLists
	 */
	
	/**
	 * Sorts the specified playlist by title
	 * @param playlist
	 * 		this is the specified playlist to be sorted
	 */
	public void sortTitle(PlayList playlist){
		model.sortTitle(playlist);
	}
	
	/**
	 * Sorts the specified playlist by artist
	 * @param playlist 
	 * 		this is the specified playlist to be sorted
	 */
	public void sortArtist(PlayList playlist){
		model.sortArtist(playlist);
	}
	
	
}
