package utilities;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import song.Song;



/**
 * @author Leighanna/Jackson
 * 	
 * Creates and stores all the Songs that will be used in the Music Player.
 * 
 * Accesses data.txt in order to get information to build Songs. 
 * Uses them to link them to .wav files in the Audio file.
 * 
 * Can throw an IOException, but is handled in the constructor.
 * IOException only occurs if Files don't exist. 
 * 
 * 
 * Properties: 
 * 
 * songLibrary:
 * An ArrayList of all the Songs created when this class is initialized. 
 */
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
		File dir2 = new File("Artwork");
		File[] paths2 = dir2.listFiles();
		System.out.println(paths.length);
		if (paths2 != null) {
			for (File p : paths2) {
				for (Song song : songLibrary) {
					if (p.toString().contains(song.getName())){
						song.setCover(p.toString());
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
	
	/**
	 * Returns the ArrayList of Songs
	 * 
	 * @return the ArrayList of Songs
	 */
	public ArrayList<Song> getSongs(){
		return songLibrary;
	}
}
