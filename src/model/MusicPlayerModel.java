package model;

import java.util.ArrayList;

import song.Song;
import utilities.PlayList;

public class MusicPlayerModel {

	private Song curSong; 
	
	private Song[] songs; 
	
	private int numSongs; 
	
	// add currentQueue
	private PlayList[] currentPlaylists;
	private ArrayList<Song> favorites;
	private ArrayList<Song> recommended;

	public MusicPlayerModel() {
		
		
	}
	
	public void play() { // needs to be more rigorous 
		curSong.setPlaying();
		curSong.play();
	}
	
	public void stop() {
		curSong.stop();
		curSong.notPlaying();
	}
	
	public void addSong(Song toAdd) {
		songs[numSongs] = toAdd; 
		numSongs++;
	}
	
	public void next() {
		
	}
	
	
}
