package controller;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;
import utilities.Queue;



/**
 * @author Seth/Jackson
 * 	
 * Holds a Song and all relevant information relating to it. Has dual functions 
 * mentioned below. ArrayList compatible, Linked List compatible, 
 * 
 * Properties:
 * name, artist, coverImage, duration, lyrics?, audio file,
 * 
 * Linked List Compatibility:
 * Utilizes .prev and .next in order to use a "live" Queue System
 * 
 * ArrayList Compatibility:
 * Utilizes an index so that it is usable in Lists.
 * 
 * Metadata: 
 * favorite used in model as special playlist
 * genre used as recommended songs metadata
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
	 * Adds a Song to the Queue
	 * 
	 * @param song, the Song to be added
	 */
	public void addToQueue(Song song) {
		model.addToQueue(song);
	}
	
	/**
	 * Starts a Queue, default if a user clicks on a single song. 
	 * 
	 * Thus, if a user clicks on the first song,
	 * it must first be added to The Queue, and then this method is called.
	 * 
	 * The annoying part about that is if they click on another song, it must
	 * use that Song as the next Song in the queue, set references accordingly and 
	 * play it immediately upon click. ew, might use changeSong for that but idk.
	 * 
	 * @param queue, the Queue to be played
	 */
	public void playQueue(Queue queue) { 
		model.playQueue(queue);
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
	 * or Queue.
	 * 
	 * If they specifically want to play a PlayList or specifically 
	 * want to make a new Queue, we use the above two functions. 
	 * 
	 * The issue I run into is if they want to click a song in a PlayList
	 * but its the first song they're picking. So the user wants to start 
	 * the PlayList at the song they're clicking. Thus I think what we might 
	 * have to do is handle it in the controller.
	 * 
	 * 100% needs to be on an eventlistener Thread
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
	 * Returns the current Queue
	 * 
	 * @return the current Queue, null if not playing Queue
	 */
	public Queue getCurQueue() {
		return model.getCurQueue();
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
	 * Returns true if Queue is Playing
	 * 
	 * @return true if Queue is Playing
	 */
	public boolean isPlayingQueue() {
		return model.isPlayingQueue();
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
	
	/**
	 * Searches for a specific Song based on a passed String name.
	 *
	 * @param name, the name of the Song you want
	 * @return returns the song if its found
	 * @throws IllegalArgumentException if song is not found
	 */
	public Song search(String name) throws IllegalArgumentException {
		Song song = model.search(name);
		if (song == null) {
			throw new IllegalArgumentException();
		}
		return song;
		
	}
	
	
	/*
	 * play, pause skip, all need to be handled differently based on threading for 
	 * Playlists and Queues, so thats a tomorrow problem, skip works tho
	 */
	 
	/**
	 * Doesn't call the Model's function, it just handles it. 
	 * Might be bad design
	 */
	public void pause() {
		model.pause();
	}
	
	/**
	 * Doesn't call the Model's function, it just handles it. 
	 * Might be bad design
	 */
	public void resume() {
		model.resume();
	}
	
	/**
	 * Skips a song
	 */
	public void skip() {
		model.skip();
	}
	
	public void restart() {
		model.restart();
	}
	
	
	public ArrayList<Song> sortTitle(ArrayList<Song> songList){
		return model.sortTitle(songList);
	}
	public ArrayList<Song> sortArtist(ArrayList<Song> songList){
		return model.sortArtist(songList);
	}
	
	
}
