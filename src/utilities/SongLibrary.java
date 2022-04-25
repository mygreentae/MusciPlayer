package utilities;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import song.Song;

public class SongLibrary {
	ArrayList<Song> songLibrary;
	
	

	public SongLibrary() {
		songLibrary = new ArrayList<>();
		try {
			addSongs();
		} catch (IOException e) {
			System.out.println("Error adding songs from Song Library");
		}
	}

	private void addSongs() throws IOException {
		List<String> dataList = Files.readAllLines(Paths.get("data.txt"), StandardCharsets.UTF_8);
		
		System.out.println(dataList.toString());
		File dir = new File("Audios");
		File[] paths = dir.listFiles();
		System.out.println(paths.length);
		if (paths != null) {
			for (File p : paths) {
				if (p.toString().endsWith(".wav")) {
					for (String data : dataList) {
						if (data.endsWith(p.toString())) {
							String[] info = data.split(",");
							Song song = new Song(info[0].trim(), info[1].trim(), info[2].trim());
							song.setAudioStream(info[3].trim());
							songLibrary.add(song);
						}
					}
					
				}
			}
		}
//		//create songs
//		Song industryBaby = new Song("Industry Baby", "Lil Nas X", "pop");
//		industryBaby.setAudioStream("Audios/Industry-Baby.wav");
//		
//		Song montero = new Song("Montero", "Lil Nas X", "pop");
//		montero.setAudioStream("Audios/Montero.wav");
//		
//		Song dejaVu = new Song("Deja Vu", "Ateez", "k-pop");
//		dejaVu.setAudioStream("Audios/Deja-Vu.wav");
//		
//		Song maniac = new Song("Maniac", "Stray Kids", "k-pop");
//		maniac.setAudioStream("Audios/Maniac.wav");
//		
//		Song freeze = new Song("Freeze", "Stray Kids", "k-pop");
//		freeze.setAudioStream("Audios/Freeze.wav");
//		
//		Song yeat = new Song("yeat", "Seth", "rap");
//		yeat.setAudioStream("Audios/yeat.wav");
//		
//		Song km = new Song("400km", "Han Yo Han", "k-hiphop");
//		
//		//adds songs to library
//		songLibrary.add(industryBaby);
//		songLibrary.add(montero);
//		songLibrary.add(dejaVu);
//		songLibrary.add(maniac);
//		songLibrary.add(freeze);
//		//songLibrary.add(yeat);

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
