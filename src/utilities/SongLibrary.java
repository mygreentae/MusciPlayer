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
 * @author Leighanna/Jackson/Paris
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
		File dir = new File("Audios");
		File[] paths = dir.listFiles();
		if (paths != null) {
			for (File p : paths) {
				if (p.toString().endsWith(".wav")) {
					for (String data : dataList) {
						if (data.contains(p.toString())) {
							String[] info = data.split(",");
							Song song = new Song(info[0].trim(), info[1].trim(), info[2].trim(), info[3].trim(), info[4].trim(), info[5].trim());
							songLibrary.add(song);
							song.setCover(info[3].trim());
						} 
					}			
				}
			}
		} 
		
		// this works, arts have to have the name formatted to match the name 
		// of the song, including spaces and weird stuff
		File dir2 = new File("src/images");
		File[] paths2 = dir2.listFiles();
		if (paths2 != null) {
			for (File p : paths2) {
				for (Song song : songLibrary) {
					if (p.toString().contains(song.getName())){
						song.setCover(p.toString());
					} 
				}
			}
		}
	}
	
	/**
	 * Returns the ArrayList of Songs
	 * 
	 * @return the ArrayList of Songs
	 */
	public ArrayList<Song> getSongs(){
		return songLibrary;
	}
	
	/**
	 * Returns the ArrayList of Songs
	 * 
	 * @return the ArrayList of Songs
	 */
	public void setSongs(ArrayList<Song> list){
		songLibrary = list;
	}
	
	
	public void addSong(Song song) {
		songLibrary.add(song);
		File dir2 = new File("src/images");
		File[] paths2 = dir2.listFiles();
		if (paths2 != null) {
			for (File p : paths2) {
				if (p.toString().contains(song.getName())){
						song.setCover(p.toString());
				} 
			}
		}
	}
}
