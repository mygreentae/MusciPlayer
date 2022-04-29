package view;
import java.io.File;  

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage; 

public class MediaPlayerTest extends Application{
	



     MediaPlayer mediaPlayer; 
       
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
	        

	        mediaPlayer = new MediaPlayer(media);  
	          
	        //by setting this property to true, the audio will be played   
	        mediaPlayer.setAutoPlay(true); 
	        
	        //VBox v = new VBox();
			
	       // Button pause = new Button("Play/Pause");
	       // pause.setOnAction(event -> playAudio());

	        
	       // v.getChildren().add(pause);
	        
	        
	        
	        primaryStage.setTitle("Playing Audio");  
	        primaryStage.show();  
	        
	    } 
	
	
	public void playAudio() {
		mediaPlayer.play();
	}
	
	public static void main(String[] args) {  
	    launch(args);  
	}  
	
	
	
}
