package model;

import song.Song;

public class MusicPlayerModel {

	private Song curSong; 
	
	private Song[] songs; 
	
	private int numSongs; 
	
	public MusicPlayerModel(Song start) {
		curSong = start; 
		songs = new Song[10];
		songs[0] = start; 
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
