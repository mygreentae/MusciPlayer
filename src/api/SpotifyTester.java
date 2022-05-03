package api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import utilities.SpotifyAPIInvalidStreamException;
import utilities.SpotifyAPIInvalidURLException;

public class SpotifyTester extends Application {

	public static void main(String[] args) {
		SpotifyAPI api = new SpotifyAPI();
		try {
			api.authenticate();
		} catch (SpotifyAPIInvalidURLException | SpotifyAPIInvalidStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			api.getToken();
		} catch (SpotifyAPIInvalidURLException | SpotifyAPIInvalidStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			api.getMetadata("the strokes", "last nite");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(SpotifyAPI.getSongDate("the strokes", "last nite"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("maybe done");
		//SpotifyBuilder sb = new SpotifyBuilder(); 
		//SpotifyBuilder.authenticate();
		//SpotifyBuilder.requestAccessToken(); // will set it in sb
		//SpotifyBuilder.requestAccessToken(); // will set it in sb
		//SpotifyBuilder.getTrackInfo("",  "");
		//launch(args);
//		String omega = "montero.mp3"; 
//		URL url;
//		try {
//			url = new URL("https://p.scdn.co/mp3-preview/abcdb00728469f85f0f83cecd368cd9acb704c48?cid=f8d521b06e3146e4ac81c0e72e4cf010");
//			testDL(url, "audios/iti.wav");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("some bullshit");
//			Platform.exit();
//		}
//		System.out.println("reached the end");
	}

	private static void testDL(URL url, String outputFileName) throws IOException {
		try (InputStream in = url.openStream();
	            ReadableByteChannel rbc = Channels.newChannel(in);
	            FileOutputStream fos = new FileOutputStream(outputFileName)) {
	            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        }
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String filename = "src/api/the_strokes_last_nite.wav";
        URL url = SpotifyTester.class.getResource(filename);
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/api/the_strokes_last_nite.wav").getAbsoluteFile());
          
        // create clip reference
        Clip clip = AudioSystem.getClip();
          
        // open audioInputStream to the clip
        clip.open(audioInputStream);
          
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
//		Media media = new Media("the_strokes_last_nite.wav");
//		MediaPlayer player = new MediaPlayer(media);
//		// Add a mediaView, to display the media. Its necessary !
//        // This mediaView is added to a Pane
//        MediaView mediaView = new MediaView(player);
        // Add to scene
        Group root = new Group();
        Scene scene = new Scene(root, 500, 200);

        // Show the stage
        primaryStage.setTitle("Media Player");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Play the media once the stage is shown
        //player.play();
	}

}
