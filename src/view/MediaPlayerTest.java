package view;
import java.awt.Color;
import java.io.File;  

import javafx.application.Application;  
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;  
import javafx.stage.Stage;  

public class MediaPlayerTest extends Application{
	
	
	@Override
	public void start (Stage primaryStage) throws Exception {  
	        // TODO Auto-generated method stub  
	        //Initialising path of the media file, replace this with your file path   
	        String path = "/Users/jacksoncovey/Desktop/MusicPlayerSethLeighannaJacksonParis/Audios/Muse_-_Survival_THE_2ND_LAW_(getmp3.pro).mp3";  
	          
	        File file = new File(path);
	        String path2 = file.toURI().toString();
	        //Instantiating Media class  
	        Media media = new Media(path2);  
	          
	        //Instantiating MediaPlayer class   
	        

	        //MediaPlayer mediaPlayer = new MediaPlayer(media);  
	          
	        //Scene scene = new Scene();
	        //by setting this property to true, the audio will be played   
	        // mediaPlayer.setAutoPlay(true);  
	        primaryStage.setTitle("Playing Audio");  
	        primaryStage.show();  
	    } 
	
	public static void main(String[] args) {  
	    launch(args);  
	}  
	
	
	
}
