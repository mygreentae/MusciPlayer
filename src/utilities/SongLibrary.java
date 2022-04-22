package utilities;

import java.util.ArrayList;

import song.Song;

public class SongLibrary {
	ArrayList<Song> songLibrary;
	
	
	
	public SongLibrary() {
		songLibrary = new ArrayList<>();
		addSongs();
	}
	
	
	private void addSongs() {
		//create songs
		Song industryBaby = new Song("Industry Baby", "Lil Nas X", "pop");
		industryBaby.setAudioStream("Audios/Industry-Baby.wav");
		
		Song montero = new Song("Montero", "Lil Nas X", "pop");
		montero.setAudioStream("Audios/Montero.wav");
		
		Song dejaVu = new Song("Deja Vu", "Ateez", "k-pop");
		dejaVu.setAudioStream("Audios/Deja-Vu.wav");
		
		Song maniac = new Song("Maniac", "Stray Kids", "k-pop");
		maniac.setAudioStream("Audios/Maniac.wav");
		
		//adds songs to library
		songLibrary.add(industryBaby);
		songLibrary.add(montero);
		songLibrary.add(dejaVu);
		songLibrary.add(maniac);
	}
	
	private void addSongs2() {
		
	}
	
	private void sortLibrary() {
		for (Song song: songLibrary) {
			
		}
	}
	
	public ArrayList<Song> getSongs(){
		return songLibrary;
	}
}
