package utilities;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import model.MusicPlayerModel;
import song.Song;


public class Tester {

	
	/**
	 * Test 1 tests the functionality of the Song class
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
		song.setAudioStream("Audios/yeat.wav");
		song.play();
		song.setPlaying();
		assertTrue(song.isPlaying());
		
		// stops song
		try {
			TimeUnit.SECONDS.sleep(1);
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
	
	
	/**
	 * Test 2 tests the functionality of the Playlist class
	 */
	@Test
	void test2() {
		Song song = new Song("yeet", "Seth", "the best");
		Song song2 = new Song("yawt", "Jackson", "the best");
		Song song3 = new Song("yurt", "Paris", "the best");
		Song song4 = new Song("yewt", "Lieghanna", "the best");
		Song song5 = new Song("yaat", "Rey", "the best");
		Song song6 = new Song("yoot", "Tyler", "the best");
		
		//tests the metadata for the Playlist
		PlayList defaultPlaylist = new PlayList("default");
		defaultPlaylist.removeSong(song);
		
		
		defaultPlaylist.addSong(song);
		Map<String, Integer> genres = defaultPlaylist.getGenres();
		assertEquals(genres.size(), 1);
		assertEquals(genres.get("the best"), 1);
		
		// tests when song isnt in playlist
		defaultPlaylist.removeSong(song3);
		
		defaultPlaylist.addSong(song2);
		defaultPlaylist.addSong(song3);
		defaultPlaylist.addSong(song4);
		defaultPlaylist.addSong(song5);
		defaultPlaylist.addSong(song6);
		genres = defaultPlaylist.getGenres();
		assertEquals(genres.size(), 1);
		assertEquals(genres.get("the best"), 6);
		
		assertEquals(defaultPlaylist.getName(), "default");
		assertEquals(defaultPlaylist.getSize(), 6);
		
		// adds different genre song
		Song song7 = new Song("Dicken", "Ben", "the goat");
		defaultPlaylist.addSong(song7);
		assertEquals(genres.size(), 2);
		assertEquals(genres.get("the goat"), 1);
		assertEquals(defaultPlaylist.getSize(), 7);
		
		// removes it and another song
		defaultPlaylist.removeSong(song7);
		assertEquals(genres.size(), 1);
		assertEquals(genres.get("the best"), 6);
		assertEquals(defaultPlaylist.getSize(), 6);
		defaultPlaylist.removeSong(song);
		assertEquals(genres.size(), 1);
		assertEquals(genres.get("the best"), 5);
		assertEquals(defaultPlaylist.getSize(), 5);
		
		// checks play order
		List<Song> order = new ArrayList<>();
		order.add(song2);
		order.add(song3);
		order.add(song4);
		order.add(song5);
		order.add(song6);
		assertEquals(order, defaultPlaylist.getPlayOrder());
		
		// shuffles 
		defaultPlaylist.shuffle();
		for (Song songs: defaultPlaylist.getPlayOrder()) {
			System.out.println(songs.getName());
		}
		System.out.println("shuffle2");
		defaultPlaylist.shuffle();
		for (Song songs: defaultPlaylist.getPlayOrder()) {
			System.out.println(songs.getName());
		}

		defaultPlaylist.playFirst(song4);
		defaultPlaylist.playFirst(song3);
		defaultPlaylist.playFirst(song2);
		defaultPlaylist.playFirst(song5);
		defaultPlaylist.playFirst(song4);
	}	
	
	
	/**
	 * Test 3 tests the functionality of the Queue class
	 * 
	 * do not, add the same song to a queue multiple times, it will not work unless 
	 * u clone it probably
	 */
	@Test
	void test3() {
		Song song = new Song("yeet", "Seth", "the best");
		Song song2 = new Song("yawt", "Jackson", "the best");
		Song song3 = new Song("yurt", "Paris", "the best");
		Song song4 = new Song("yewt", "Lieghanna", "the best");
		Song song5 = new Song("yaat", "Rey", "the best");
		Song song6 = new Song("yoot", "Tyler", "the best");
		
		//tests Linked List references 
		Queue q = new Queue(song);
		q.enqueue(song2);
		assertEquals(song.getNext(), song2);
		assertEquals(song.getPrev(), null);
		assertEquals(song2.getPrev(), song);
		assertEquals(song2.getNext(), null);
		
		q.enqueue(song3);
		q.enqueue(song4);
		q.enqueue(song5);
		q.enqueue(song6);
		assertEquals(song5.getNext(), song6);
		assertEquals(song5.getPrev(), song4);
		assertEquals(song6.getPrev(), song5);
		assertEquals(song6.getNext(), null);
		
		//tests moving along queue forw/back
		q.next();
		q.next();
		q.next();
		assertEquals(q.getCur(), song4);
		assertEquals(q.getNext(), song5);
		assertEquals(q.getPrev(), song3);
		q.back();
		assertEquals(q.getCur(), song3);
		assertEquals(q.getNext(), song4);
		assertEquals(q.getPrev(), song2);
		
		//tests modifying when prevs/nexts are null
		q = new Queue(song);
		Song s = q.getCur();
		q.next();
		q.next();
		assertEquals(q.getCur(), s);
		q.back();
		q.back();
		q.back();
		assertEquals(q.getCur(), s);
		
	}

	/**
	 * Test 4 tests adding an removing PlayLists using the MusicPlayerModel class.
	 * 
	 * We'll see how this goes.
	 */
	@Test
	void test4() {
		MusicPlayerModel model = new MusicPlayerModel();
		Song song1 = new Song("yeet", "Seth", "the best");
		Song song2 = new Song("yawt", "Jackson", "the best");
		Song song3 = new Song("yurt", "Paris", "the best");
		Song song4 = new Song("yewt", "Lieghanna", "the best");
		Song song5 = new Song("yaat", "Rey", "the best");
		Song song6 = new Song("yoot", "Tyler", "the best");
		
		PlayList p1 = new PlayList("test1");
		p1.addSong(song1);
		p1.addSong(song2);
		p1.addSong(song3);
		
		PlayList p2 = new PlayList("test2");
		p2.addSong(song4);
		p2.addSong(song5);
		p2.addSong(song6);
		
		// check adding/ removing playlists
		ArrayList<PlayList> all = new ArrayList<>();
		all.add(p1);
		model.addPlaylist(p1);
		assertEquals(model.getAllPlaylists(), all);
		model.addPlaylist(p2);	
		all.add(p2);
		assertEquals(model.getAllPlaylists(), all);
		
		model.removePlaylist(p1);
		model.removePlaylist(p2);
		assertEquals(model.getAllPlaylists(), new ArrayList<PlayList>());		
	}
	
	
	/*
	 * Test 5 plays 2 songs from a PlayList that was added to the model
	 * They're just super song and annoying to play 
	 */
	@Test
	void test5() {
		
		MusicPlayerModel model = new MusicPlayerModel();
		Song song1 = new Song("freeze", "Jackson", "k-pop");
		Song song2 = new Song("yeat", "Seth", "rap");
		//literally the most crucial thing ever, download wav/audio file into audios
		// assign them to songs based on name, ezpz
		song1.setAudioStream("Audios/Freeze.wav");
		song2.setAudioStream("Audios/yeat.wav");
				
		PlayList p1 = new PlayList("test1");
		p1.addSong(song1);
		p1.addSong(song2);
		model.addPlaylist(p1);
		System.out.println("added");
		
		//model.playPlaylist(p1);
	}
	
	
	/**
	 * Test 6 tests adding to the favorites PlayList in the MusicPlayerModel.
	 * It also tests playing the actual PlayList contents
	 * 
	 * We'll see how this goes.
	 */
	@Test
	void test6() {
		MusicPlayerModel model = new MusicPlayerModel();
		Song song1 = new Song("yeet", "Seth", "the best");
		Song song2 = new Song("yawt", "Jackson", "the best");
		Song song3 = new Song("yurt", "Paris", "the best");
		Song song4 = new Song("yewt", "Lieghanna", "the best");
		Song song5 = new Song("yaat", "Rey", "the best");
		Song song6 = new Song("yoot", "Tyler", "the best");
		
		PlayList p1 = new PlayList("test1");
		p1.addSong(song1);
		p1.addSong(song2);
		
		
		model.addToFavorites(song6);
		model.addToFavorites(song2);
		model.addToFavorites(song3);
		assertTrue(song6.isFavorite());
		
		model.removeFromFavorites(song6);
		model.removeFromFavorites(song5);
		assertFalse(song6.isFavorite());
		
	}
	
	//@Test
	void test7(){
		MusicPlayerModel model = new MusicPlayerModel();
		Song song1 = new Song("Deja Vu", "Ateez", "k-pop");
		Song song2 = new Song("Maniac", "Stray Kids", "k-pop");
		//literally the most crucial thing ever, download wav/audio file into audios
		// assign them to songs based on name, ezpz
		song1.setAudioStream("Audios/Deja-Vu.wav");
		song2.setAudioStream("Audios/Maniac.wav");
				
		PlayList p1 = new PlayList("test1");
		p1.addSong(song1);
		p1.addSong(song2);
		model.addPlaylist(p1);
		System.out.println("added");
		model.playPlaylist(p1, song2);
	}
	
	
	
	/**
	 * Testing Queues, single song
	 * 
	 * ChangeSong is gonna give us problems down the road cuz its gonna be tied 
	 * to an event listener. Thus we're going to have an issue ucz its breaking
	 * when i try to add songs after the queue was started
	 */
	
	//@Test
	void test8(){
		MusicPlayerModel model = new MusicPlayerModel();
		Song song1 = new Song("Industry Baby", "Lil Nas X", "pop");
		Song song2 = new Song("Montero", "Lil Nas X", "pop");
		//literally the most crucial thing ever, download wav/audio file into audios
		// assign them to songs based on name, ezpz
		song1.setAudioStream("Audios/Industry-Baby.wav");
		song2.setAudioStream("Audios/Montero.wav");
				

		model.changeSong(song1);
	}
	
	/*
	 * Same thing here, should be fine with multi song queue, but will have trouble 
	 * with queue that has stuff added after its started playing.
	 */
	@Test
	void test9() {
		MusicPlayerModel model = new MusicPlayerModel();
		Song song1 = new Song("Industry Baby", "Lil Nas X", "pop");
		Song song2 = new Song("Montero", "Lil Nas X", "pop");
		song1.setAudioStream("Audios/Industry-Baby.wav");
		song2.setAudioStream("Audios/Montero.wav");
		
		Song song3 = new Song("Deja Vu", "Ateez", "k-pop");
		Song song4 = new Song("Maniac", "Stray Kids", "k-pop");
		song3.setAudioStream("Audios/Deja-Vu.wav");
		song4.setAudioStream("Audios/Maniac.wav");
				

		model.addToQueue(song1);
		model.addToQueue(song4);
		model.addToQueue(song3);
		model.addToQueue(song2);
		model.playQueue(model.getCurQueue());
	}
	
	
	/**
	 * need to test playing songs when they are in multiple playlists and 
	 * in the queue so thatll be fun.
	 */
	
	
}
