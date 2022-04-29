package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import song.Song;
import utilities.PlayList;
import utilities.Queue;
import utilities.SongLibrary;







/**
 * @author Seth/Jackson
 * 	
 * This is the Model of the music player. It holds all of the 
 * User data regarding PlayLists, Queues, favorites, recommended
 * songs, and threads. 
 * 
 * 
 * Properties:
 * curSong: 
 * The Song currently playing, when Model is initialized, 
 * it is null.
 * 
 * currentQueue: 
 * The Queue currently playing, null when initialized/if
 * PlayList is playing.
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
 * playingQueue/playingPlaylist: 
 * Boolean values for if Queue/PlayList is playing, null when initialized.
 * 
 * threads: 
 * An ArrayList of Thread's that are responsible for playing audio.
 * Are interrupted()/stop()-ed when a new play....() function is called.
 * 
 * metadata:
 * A Map of Strings mapped to integers of genre's of Songs in a user's 
 * allPlaylists.
 *
 */
@SuppressWarnings("deprecation")
public class MusicPlayerModel extends Observable{
	
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
	
	private Map<String, Integer> metadata;
	
	/**
	 * Creates the MusicPlayerModel
	 */
	public MusicPlayerModel(SongLibrary songLibrary) {
		this.songLibrary = songLibrary;
		allPlaylists = new ArrayList<>();
		favorites = new PlayList("Favorites");
		recommended = new PlayList("Recommended");
		threads = new ArrayList<Thread>();
		metadata = new HashMap<String, Integer>();

		createRecommended(); //creates recommended songs
		
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
	 * Starts a Queue, used in changeSong(Song) to handle when not playing
	 * a Queue or a PlayList.
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
		//stops other songs playing
		stopThreads();
		if (curSong != null) {
			curSong.stop();
		}
		currentQueue = queue;
		currentPlaylist = null;
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
		setChanged();
		notifyObservers();
		
		System.out.println(curSong.getCover());
		System.out.println("gonna figure it out later");
	}
	
	public void resumeQueue(Queue queue) {
		//stops other songs playing
		stopThreads();
		currentQueue = queue;
		currentPlaylist = null;
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
		//setChanged();
		//notifyObservers();
		
		System.out.println(curSong.getCover());
		System.out.println("gonna figure it out later");
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
		//stops other songs playing
		stopThreads();
		if (curSong != null) {
			curSong.stop();
		}
		currentPlaylist = playlist;
		currentQueue = null;
		playingPlaylist = true;
		playingQueue = false;
		curSong = playlist.getPlayOrder().get(0);
		
		Runnable runnable =
			    new Runnable(){
			        public void run(){
			        	for (Song song : playlist.getPlayOrder()) {
			    			//plays song for however long it is
			    			curSong = song;
			    			System.out.println("playlist curSOng " + song.getName());
			    			curSong.play();
			    			curSong.stop();
			    		}
			        }
			    };
			  
		Thread thread = new Thread(runnable);
		threads.add(thread);
		thread.start();
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Starts playing a Playlist from a song. unsure if this works
	 * how intended
	 * 
	 * @param playlist, the PlayList to be played
	 */
	public void playPlaylist(PlayList playlist, Song song) {
		//stops other songs playing
		stopThreads();
		if (curSong != null) {
			curSong.stop();
		}
		currentPlaylist = playlist;
		currentQueue = null;
		playingPlaylist = true;
		playingQueue = false;
		curSong = song;
		
		//playlist.playFirst(song); //sets first song
		// plays entire playlist
		Runnable runnable =
			    new Runnable(){
			        public void run(){
			        	for (Song pSong : playlist.getPlayOrder()) {
			    			//plays song for however long it is
			        		if (pSong.getIndex() > song.getIndex()) {
				    			curSong = song;
				    			curSong.play();
				    			curSong.stop();
			        		}

			    		}
			        }
			    };
	
		System.out.println("gonna figure it out later");
		Thread thread = new Thread(runnable);
		threads.add(thread);
		thread.start();
		setChanged();
		notifyObservers();
	}
	
	
	/**
	 * Starts playing a Playlist from a song at a specific index.
	 * 
	 * @param playlist, the PlayList to be played
	 */
	public void playPlaylist(PlayList playlist, int index) {
		//stops other songs playing
		stopThreads();
		if (curSong != null) {
			curSong.stop();
		}
		currentPlaylist = playlist;
		currentQueue = null;
		playingPlaylist = true;
		playingQueue = false;
		curSong = playlist.getPlayOrder().get(index);
		
		Runnable runnable =
			    new Runnable(){
			        public void run(){
			        	int count = 0;
			    		for (Song song : playlist.getPlayOrder()) {
			    			//plays song for however long it is
			    			if (count >= index) {
			    				curSong = song;
			    				curSong.play();
			    				curSong.stop();
			    			}
			    			count += 1;
			    		}
			        }
			    };
		Thread thread = new Thread(runnable);
		threads.add(thread);
		thread.start();
		setChanged();
		notifyObservers();
	}
	
	
	public void resumePlaylist(PlayList playlist) {
		stopThreads();
		currentPlaylist = playlist;
		currentQueue = null;
		playingPlaylist = true;
		playingQueue = false;
		Song song = curSong;
		
		//playlist.playFirst(song); //sets first song
		// plays entire playlist
		Runnable runnable =
			    new Runnable(){
			        public void run(){
			        	for (Song pSong : playlist.getPlayOrder()) {
			    			//plays song for however long it is
			        		if (pSong.getIndex() >= song.getIndex()) {
				    			curSong = song;
				    			curSong.play();
				    			curSong.stop();
			        		}

			    		}
			        }
			    };
	
		System.out.println("gonna figure it out later");
		Thread thread = new Thread(runnable);
		threads.add(thread);
		thread.start();
		setChanged();
		notifyObservers();
	}
	
	
	/**
	 * Shuffles a PlayList 
	 * 
	 * @param playlist, the PlayList to be shuffled
	 * @return the PlayList after shuffling
	 */
	public PlayList shufflePlayList(PlayList playlist) {
		playlist.shuffle();
		return playlist;
	}
	
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
	
	/**
	 * Used to check if buttons can actually do anything in GUI
	 * For example, if isPlayingSong(), then the play button shouldnt 
	 * do anything, but if the song is paused, this should be false.
	 * 
	 * @return if a Song is playing
	 */
	public Boolean isPlayingSong() {
		return curSong.isPlaying();
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
	 * Returns the current Queue
	 * 
	 * @return the current Queue, null if not playing Queue
	 */
	public Queue getCurQueue() {
		return currentQueue;
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
	 * Returns true if Queue is Playing
	 * 
	 * @return true if Queue is Playing
	 */
	public boolean isPlayingQueue() {
		return playingQueue;
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
	 * If the model has no PlayLists, creates recommneded 
	 * 10 song PlayList
	 * 
	 * Else: it uses the metadata map 
	 */
	public void createRecommended() {
		recommended = new PlayList("Recommended");
		// gets metadata
		for (PlayList list: allPlaylists) {
			for (Song song: list.getSongList()) {
				if (metadata.containsKey(song.getGenre())){
					metadata.put(song.getGenre(), 1);
				} else {
					int value = metadata.get(song.getGenre());
					metadata.put(song.getGenre(), value + 1);
				}
			}
		}
		//
		//makes 10 songs
		int count = 10;
		if (allPlaylists.size() == 0) {
			ArrayList<Song> shuffle = new ArrayList<>();
			
			for (Song song : songLibrary.getSongs()) {
				shuffle.add(song);
			}
			Random random = new Random();
			while (count > 0) {
				System.out.println(shuffle.size());
				int i = random.nextInt(shuffle.size());
				Song song = shuffle.get(i);
				recommended.addSong(song);
				shuffle.remove(i);
				count--;
			}		
		} else {
			System.out.println("Nope");
		}
		
		
		
		// literal ai
		System.out.println("horrible");
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
	
	/**
	 * unsure if we need this but ya never know, could be a cool feature
	 * 
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
	
	
	/*
	 * play, pause skip, all need to be handled differently based on threading for 
	 * Playlists and Queues, so thats a tomorrow problem, skip works tho
	 */
	 
	/**
	 * Doesn't call the Model's function, it just handles it. 
	 * Might be bad design
	 */
	public void pause() {
		stopThreads();
		curSong.pause();
	}
	
	
	
	/**
	 * Doesn't call the Model's function, it just handles it. 
	 * Might be bad design it defintely was bad design but for different 
	 * reasons.
	 * 
	 */
	public void resume() {
		if (playingQueue) {
			Queue newQueue = new Queue(curSong, true);
			resumeQueue(newQueue);
		} else if(playingPlaylist) {
			resumePlaylist(currentPlaylist);
		}
	}
	
	/**
	 * Skips a song
	 */
	public void skip() {
		if (playingQueue) { 
			curSong.stop();
			System.out.println("skipped");
			//arbitrary time delay
			try {
				TimeUnit.SECONDS.sleep((long) 0.3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			//if playingQueue, otherwise if playingPlaylist
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
			} else {
				setChanged();
				notifyObservers();
			}
			
		} else if (playingPlaylist) {
			curSong.stop();
			int skipIndex = curSong.getIndex() + 1;
			if (skipIndex >= currentPlaylist.getSize()) {
				return;
			}
			playPlaylist(currentPlaylist, skipIndex);
		} else {
			return;
		}
	}

	
	public void restart() {
		if (playingQueue) { 
			curSong.stop();
			Queue q = new Queue(curSong, true);
			resumeQueue(q);
			
		} else if (playingPlaylist) {
			curSong.stop();
			int index = curSong.getIndex();
			playPlaylist(currentPlaylist, index);
		} else {
			return;
		}
	}
	
	/**
	 * Sorts either the songLibrary or a PlayList by Artist
	 */
	public void sortListByArtist() {
		if (playingPlaylist) {
			ArrayList<Song> list = currentPlaylist.getSongList();
			//songLibrary.sortArtists();
		} else {
			ArrayList<Song> list = songLibrary.getSongs();
			//do the thing
			songLibrary.setSongs(list);
		}
	}
	
	/**
	 * Sorts either the songLibrary or a PlayList by Title
	 */
	public void sortListByTitle() {
		if (playingPlaylist) {
			ArrayList<Song> songList = currentPlaylist.getSongList();
			songList = sortTitle(songList);
			currentPlaylist.sortPlaylist(songList);
		} else {
			ArrayList<Song> songList = songLibrary.getSongs();
			songList = sortTitle(songList);
			songLibrary.setSongs(songList);	
		}
	}

	/**
	 * Sorts either the songLibrary or a PlayList by Date
	 */
	public void sortListByDate() {
		if (playingPlaylist) {
			ArrayList<Song> list = currentPlaylist.getSongList();
			//do the thing
			currentPlaylist.sortPlaylist(list);
			
		} else {
			ArrayList<Song> list = songLibrary.getSongs();
			// do the thing
			songLibrary.setSongs(list);	
			
		}
	}
	
	/**
	 * Stops any running threads
	 */
	private void stopThreads() {
		for (Thread thread : threads) {
			thread.stop();
		}
	}
	
	/**
	 * Sorts the songLibrary by song Name
	 */
	public ArrayList<Song> sortTitle(ArrayList<Song> songList){
		
		ArrayList<Song> tempList = new ArrayList<Song>();
		tempList.add(songList.get(0));
		//insertion sort
		for(int i = 1; i < songList.size(); i++) {
			for(int j = 0; j < tempList.size(); j++) {
				try {
					if(tempList.get(j).getName().compareTo(songList.get(i).getName()) < 0) {
						tempList.add(j + 1, songList.get(i));
					}
				} catch(Exception IndexOutOfBoundsException) {
					tempList.add(songList.get(i));
				}
			}
		}
		return tempList;
	}
	
	/**
	 * Sorts the songLibrary by song Artist
	 */
	public ArrayList<Song> sortArtist(ArrayList<Song> songList){
		
		ArrayList<Song> tempList = new ArrayList<Song>();
		tempList.add(songList.get(0));
		//insertion sort
		for(int i = 1; i < songList.size(); i++) {
			for(int j = 0; j < tempList.size(); j++) {
				try {
					if(tempList.get(j).getArtist().compareTo(songList.get(i).getArtist()) < 0) {
						tempList.add(j + 1, songList.get(i));
					}
				} catch(Exception IndexOutOfBoundsException) {
					tempList.add(songList.get(i));
				}
			}
		}
		return tempList;
	}
}
