package view;

import controller.MusicPlayerController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.MusicPlayerModel;
import song.Song;

public class MusicPlayerView extends Application {
	
	private static MusicPlayerController controller;
	private static MusicPlayerModel model; 

	@Override
	public void start(Stage stage) {
		Song start = new Song("dub", "Yeat");
		model = new MusicPlayerModel(start);
		controller = new MusicPlayerController(model);
		GridPane pane = new GridPane();
		Button playPause = new Button("Play");
		Button stop = new Button("Stop");
		playPause.setAlignment(Pos.CENTER);
		stop.setAlignment(Pos.CENTER);
		pane.add(playPause, 0, 0);
		pane.add(stop, 1, 0);
		pane.setAlignment(Pos.BASELINE_CENTER);
		VBox vbox = new VBox(pane);
		Scene mainScene = new Scene(vbox);
		stage.setWidth(500);
		stage.setHeight(500);
		stage.setScene(mainScene);
		stage.setTitle("Music Player");
		stage.show();
		
		
		
		
		// lambdas
		playPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				controller.play();
				
			}
			
		});
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				controller.stop();
				
			}
			
		});
	}
	
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
