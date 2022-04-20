package song;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class Song {

	private static Clip audio;
	private static AudioInputStream stream;
	private String name;
	
	private String artist; 
	
	private String cover; // temporary for now.
	
	private boolean isPlaying; 
	
	public Song(String name, String artist) {
		this.name = name;
		this.artist = artist; 
		isPlaying = false;
	}
	
	public void setPlaying() {
		isPlaying = true; 
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void notPlaying() {
		isPlaying = false; 
	}

	public void play() {
		try {
			stream = AudioSystem.getAudioInputStream(new File("yeat.wav").getAbsoluteFile());
			audio = AudioSystem.getClip();
			audio.open(stream);
			audio.start();
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void stop() {
		audio.stop();
	}
	
	
}

