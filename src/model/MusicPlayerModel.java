package model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import song.Song;
import utilities.PlayList;
import utilities.Queue;
import utilities.SongLibrary;

public class MusicPlayerModel {

	
	private SongLibrary songLibrary;
	
	private Song curSong; 
	
	private Queue currentQueue; 
	private PlayList currentPlaylist;
	
	//private Song[] songLibrary;
	private int numSongs; 
	
	//features maybe?
	private ArrayList<PlayList> allPlaylists;
	private PlayList favorites;
	private PlayList recommended;

	//model state
	private boolean playingQueue;
	private boolean playingPlaylist;
	private ArrayList<Thread> threads;
	
	/**
	 * Creates the MusicPlayerModel
	 */
	public MusicPlayerModel(SongLibrary songLibrary) {
		this.songLibrary = songLibrary;
		allPlaylists = new ArrayList<>();
		favorites = new PlayList("favorites");
		recommended = new PlayList("recommended");
		threads = new ArrayList<Thread>();
		
	}
	
	private class playQueue implements Runnable{
		Queue queue;
		Thread runner;
		
		public playQueue(Queue queue) {
			this.queue = queue;
			this.runner = new Thread(this);
			this.runner.start();
		}
		
		@Override
		public void run() {
			
		
		}
	}
		
	
	public void play(Song song) {
		//Runnable runnable = new playSong(song);
			    
			  
		//Thread thread = new Thread(runnable);
		//threads.add(thread);
		//System.out.println(song.getName());
		//thread.start();
		//System.out.println("shit");
	}
	
	/**
	 * Adds songs to current Queue or starts new Queue
	 * 
	 * @param song, the song to enqueue
	 */
	public void addToQueue(Song song) {
		if (currentQueue == null) {
			currentQueue = new Queue(song);
		} else {
			currentQueue.enqueue(song);
		}
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
		currentQueue = queue;
		playingQueue = true;
		playingPlaylist = false;
    	curSong = queue.getCur();
		Runnable runnable =
			    new Runnable(){
			        public void run(){
			        	while (curSong != null) {
			    			curSong.play();
			    			curSong.stop();
			    			queue.next();
			    			curSong = queue.getCur();
			    		}  
			        }
			    };
			  
		Thread thread = new Thread(runnable);
		threads.add(thread);
		System.out.println("yeet");
		thread.start();
		
		
		System.out.println("gonna figure it out later");
	}
	
	/**
	 * Starts playing a Playlist,
	 * 
	 * This function plays PlayLists and only PlayLists. Way easier than the queue one
	 * Even though the PlayList class is like 5x longer.
	 * 
	 * @param playlist, the PlayList to be played
	 */
	public void playPlaylist(PlayList playlist) {
		currentPlaylist = playlist;
		playingPlaylist = true;
		playingQueue = false;
		for (Song song : playlist.getPlayOrder()) {
			//plays song for however long it is
			curSong = song;
			curSong.play();
			curSong.stop();
		}
		System.out.println("gonna figure it out later");
	}
	
	/**
	 * Starts playing a Playlist from a song.
	 * 
	 * @param playlist, the PlayList to be played
	 */
	public void playPlaylist(PlayList playlist, Song song) {
		currentPlaylist = playlist;
		playingPlaylist = true;
		playingQueue = false;
		curSong = song;
		playlist.playFirst(song); //sets first song
		// plays entire playlist
		for (Song songs : playlist.getPlayOrder()) {
			//plays song for however long it is
			curSong = songs;
			curSong.play();
			curSong.stop();
		}
		System.out.println("gonna figure it out later");
	}
	
	
	public PlayList shufflePlayList(PlayList playlist) {
		playlist.shuffle();
		return playlist;
	}
	
	/**
	 * This method is used when a use selects a song in say like an
	 * explore page or something. 
	 * 
	 * If we use a list of Songs as our Song library, and our user just 
	 * clicks a Song, we call this method.
	 * 
	 * If they specifically want to play a PlayList or specifically 
	 * want to make a new Queue, we use the above two functions. 
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
		if (curSong != null) {
			curSong.stop();
		}
		// if in Playlist, and playlist is currently being played
		if (playingPlaylist && currentPlaylist.contains(song)) {
			curSong = song;
			currentPlaylist.playFirst(song);
			playPlaylist(currentPlaylist);
			
		} else {
			// starts new queue, basically this handles the 1st song pick
			playQueue(new Queue(song));
		}
		// if in PlayList, start playlist with this song:
	}	
	
	
	/* 
	 * all of the getters that will be needed for the controller to call to
     * give to GUI
	 */
	
	public Song getCurSong() {
		return curSong;
	}
	
	public Queue getCurQueue() {
		return currentQueue;
	}
	
	public PlayList getCurPlaylist() {
		return currentPlaylist;
	}
	
	public ArrayList<PlayList> getAllPlaylists(){
		return allPlaylists;
	}
	
	public PlayList getPlaylist(String name) {
		for (PlayList playlist : allPlaylists) {
			if (playlist.getName().toLowerCase().equals(name.toLowerCase())) {
				return playlist;
			}
		} 
		return null;
	}

	public PlayList getFavorites(){
		return favorites;
	}
	
	public PlayList getRecommended(){
		return recommended;
	}
	
	public boolean isPlayingQueue() {
		return playingQueue;
	}
	
	public boolean isPlayingPlaylist() {
		return playingPlaylist;
	}
	
	/*
	 * All of the create/remove things methods
	 */
	
	public void createRecommended() {
		// literal ai
		System.out.println("horrible");
	}
		
	public void addPlaylist(PlayList playlist) {
		allPlaylists.add(playlist);
	}
	
	public void removePlaylist(PlayList playlist) {
		if (allPlaylists.contains(playlist)) {
			allPlaylists.remove(playlist);
		}
	}
	
	public void addToFavorites(Song song) {
		song.makeFavorite();
		favorites.addSong(song);
	}
	
	public void removeFromFavorites(Song song) {
		if (favorites.contains(song)) {
			song.unFavorite();
			favorites.removeSong(song);
		}
	}
	
	/**
	 * Returns the song if its present in the Song Library
	 * 
	 * @param name, the name of the song the user wants
	 * @return null if Song is not found, otherwise returns the Song
	 */
	public Song search(String name) {
		//binary search song names?
		Song searchedSong = null;
		for (Song song : songLibrary.getSongs()) {
			if (song.getName().toLowerCase().equals(name.toLowerCase())) {
				searchedSong = song;
			}
		}
		// if song is not found, returns null
		if (searchedSong == null) {
			return null;
		} else {
			return searchedSong;
		}
	}
	
	public void addToPlaylist(PlayList playlist, Song song) {
		playlist.addSong(song);
	}
	
	/**
	 * unsure if we need this but ya never know, could be a cool feature
	 * @param playlist
	 * @return
	 */
	
	public PlayList copyPlaylist(PlayList playlist) {
		PlayList copy = new PlayList(playlist.getName());
		for (Song song: playlist.getSongList()) {
			copy.addSong(song);
		}
		return copy;
		
	}
	
	public void skip() {
		if (playingQueue) { 
			curSong.stop();
			if (currentQueue.getNext() != null) {
				curSong = currentQueue.getNext();
				Queue newQueue = new Queue(curSong);
				if (currentQueue.getNext().getNext() != null) {
					Song count = currentQueue.getNext().getNext();
					while (count != null) {
						newQueue.enqueue(count);
					}
				}
				playQueue(newQueue);
			}
			
		} else if (playingPlaylist) {
			return;
		} else {
			return;
		}
	}
	
}
