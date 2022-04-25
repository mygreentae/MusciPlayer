package controller;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;
import utilities.Queue;

public class MusicPlayerController {

	
	private MusicPlayerModel model; 
	
	/**
	 * ALL PLAY FUNCTIONS MIGHT haVE TO BE THREADS
	 * @param model
	 */
	public MusicPlayerController(MusicPlayerModel model) {
		this.model = model; 
	}
	
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
	
	public Song getCurSong() {
		return model.getCurSong();
	}
	
	public Queue getCurQueue() {
		return model.getCurQueue();
	}
	
	public PlayList getCurPlaylist() {
		return model.getCurPlaylist();
	}
	
	public ArrayList<PlayList> getAllPlaylists(){
		return model.getAllPlaylists();
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 */
	public PlayList getPlaylist(String name) throws IllegalArgumentException{
		PlayList playlist = model.getPlaylist(name);
		if (playlist != null) {
			return playlist;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public PlayList getFavorites(){
		return model.getFavorites();
	}
	
	public PlayList getRecommended(){
		return model.getRecommended();
	}
	
	public boolean isPlayingQueue() {
		return model.isPlayingQueue();
	}
	
	public boolean isPlayingPlaylist() {
		return model.isPlayingPlaylist();
	}
	
	
	
	/*
	 * All of the create/remove things methods
	 */
	
	public void createRecommended() {
		// literal ai
		System.out.println("horrible");
	}
	
	public void removePlaylist(PlayList playlist) {
		model.removePlaylist(playlist);;
	}
	
	public void addToFavorites(Song song) {
		model.addToFavorites(song);
	}
	
	public void removeFromFavorites(Song song) {
		model.removeFromFavorites(song);
	}
	
	
	/**
	 * make a playlist function, it will be a new playlist and be empty
	 */
	public void makePlaylist(String name) {
		PlayList newPlaylist = new PlayList(name);
		model.addPlaylist(newPlaylist);
		
	}
	
	/**
	 * 
	 * @param playlist
	 * @param song
	 */
	public void addToPlaylist(PlayList playlist, Song song) {
		model.addToPlaylist(playlist, song);
	}
	
	/**
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
	
	
	
	
	 
	//
	public void pause() {
		model.getCurSong().pause();
	}
	
	/**
	 * 
	 */
	public void resume() {
		model.getCurSong().play();
	}
	
	/**
	 * Skips the current Song
	 */
	public void skip() {
		model.skip();
	}
	
	
	
}
