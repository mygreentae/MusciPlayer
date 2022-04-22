package utilities;

import java.util.ArrayList;

import song.Song;


/**
 * 
 * @author Jackson
 *
 *	Uses the Linked List Compatibility of Song objects
 *	Allows for a user to go forward and backward through
 *	a queue of songs that can be added to while other songs 
 *	are being played. (hopefullly)
 *
 */
public class Queue {

	private Song curSong;
	private Song nextSong;
	private Song prevSong;
	private Song lastSong;
	
	/**
	 * Creates the First song in the Queue
	 * 
	 * Thus, Queues cannot be empty. It will always have 
	 * a length of at least 1.
	 * @param song, the first Song
	 */
	public Queue(Song song){
		this.curSong = song;
		song.setNext(null); // ensure references
		song.setPrev(null); // are correct
		this.nextSong = song.getNext();
		this.prevSong = song.getPrev();
		this.lastSong = song;
	}
	
	/**
	 * Adds a song to the back of the Queue
	 * 
	 * @param song, the Song to enqueue
	 */
	public void enqueue(Song song) {		
		lastSong.setNext(song);
		song.setPrev(lastSong);
		lastSong = song;
	}
	
	/**
	 * Gets the next Song
	 * 
	 * @return the next Song
	 */
	public Song getNext() {
		nextSong = curSong.getNext();
		return nextSong;
	}
	
	/**
	 * Gets the previous Song
	 * 
	 * @return the previous Song
	 */
	public Song getPrev() {
		prevSong = curSong.getPrev();
		return prevSong;
	}
	
	/**
	 * Gets the current Song
	 * 
	 * @return the current Song
	 */
	public Song getCur() {
		return curSong;
	}
	
	/**
	 * Changes to the next Song
	 */
	public void next() {
		if (curSong.getNext() != null) {
			curSong = curSong.getNext();
		}
	}
	
	/**
	 * Changes to the previous Song
	 */
	public void back() {
		if (curSong.getPrev() != null) {
			curSong = curSong.getPrev();
		}
	}
}
