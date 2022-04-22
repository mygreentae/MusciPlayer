package utilities;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import song.Song;


public class Tester {

	
	/**

	 * Tests 1 tests the functionality of the Song class

	 */
	@Test
	void test1() {
		// creates song, asserts everything works
		Song song = new Song("yeet", "Seth", "the best");
		assertFalse(song.isPlaying());
		assertEquals(song.getName(), "yeet");
		assertEquals(song.getArtist(), "Seth");
		assertEquals(song.getGenre(), "the best");

		// plays song
		song.play();
		song.setPlaying();
		assertTrue(song.isPlaying());
		
		// stops song
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		song.stop();
		song.notPlaying();
		assertFalse(song.isPlaying());
		
		// checks favorite
		assertFalse(song.isFavorite());
		song.makeFavorite();
		assertTrue(song.isFavorite());
		song.unFavorite();
		assertFalse(song.isFavorite());
	
	}
	
	@Test
	void test2() {
		Song song = new Song("yeet", "Seth", "the best");
	}	

}
