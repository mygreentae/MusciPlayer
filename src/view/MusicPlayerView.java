package view;

import java.io.IOException;
import java.util.ArrayList;

import controller.MusicPlayerController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;
import utilities.SongLibrary;

public class MusicPlayerView extends Application {
	
	private static MusicPlayerController controller;
	private static MusicPlayerModel model; 
	private static SongLibrary songLibrary;
	
	private static final int TILE_HEIGHT = 50;
	private static final int TILE_WIDTH = 100;
	private static final int TITLE_FONT_SIZE = 20;
	private static final int ARTIST_FONT_SIZE = 10;
	private static final int SCROLL_MAX_HEIGHT = 400;
	private static final int SCROLL_MAX_WIDTH = 250;

	@Override
	public void start(Stage stage) throws IOException {
		songLibrary = new SongLibrary();
		model = new MusicPlayerModel(songLibrary);
		controller = new MusicPlayerController(model);
//		BorderPane border = new BorderPane();
//		border.setPadding(new Insets(50, 10, 50, 50));
		
		VBox root = new VBox();
		HBox hbox = new HBox();
		
		ImageView imageView = new ImageView();
		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
		imageView.setFitHeight(400);
		imageView.setFitWidth(400);
		
		hbox.setPadding(new Insets(10, 10, 10, 10));
		
		ScrollPane songView = playListView();
		songView.setPrefViewportWidth(SCROLL_MAX_WIDTH);
		songView.setPrefViewportHeight(SCROLL_MAX_HEIGHT);
		
		hbox.getChildren().addAll(imageView, songView);
		root.getChildren().addAll(hbox);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Music Player");
		stage.show();
		
//		handleEvents(playPause, stop);
		
	}
	
	private ScrollPane playListView() {
		ScrollPane scroller = new ScrollPane();
		GridPane songView = new GridPane();
		songView.setPadding(new Insets(10, 10, 10, 20));
		
		ArrayList<Song> songList = songLibrary.getSongs();
		for (int i = 0; i < songList.size(); i++) {
			SongTile songTile = new SongTile();
			songTile.getIndex().setText(Integer.toString(i+1));
			songTile.getTitle().setText(songList.get(i).getName());
			songTile.getArtist().setText(songList.get(i).getArtist());
			
			songView.add(songTile, 1, i);
		}
		scroller.setContent(songView);
		return scroller;
	}
	
	private void handleEvents(Button playPause, Button stop) {
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
	
	private ImageView setAlbumArt(Song curSong) {
    	ImageView imageView = new ImageView();
    	if (curSong.getCover() == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	} 
    	else {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg")); // change
    	}
    	return imageView;
    }
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private class SongTile extends BorderPane {
		
		private Text index;
		private Text title;
		private Text artist;
		
		private BorderPane border;
		private Rectangle indexRect;
		private Rectangle titleRect;
		private Rectangle artistRect;
		
		private StackPane indexStack;
		private StackPane titleStack;
		private StackPane artistStack;
		
		private SongTile() {
			index = new Text();
			title = new Text();
			artist = new Text();
			
			border = new BorderPane();
			indexRect = new Rectangle();
			titleRect = new Rectangle();
			artistRect = new Rectangle();
			
			indexStack = new StackPane();
			titleStack = new StackPane();
			artistStack = new StackPane();
			
			indexStack.getChildren().addAll(indexRect, index);
			titleStack.getChildren().addAll(titleRect, title);
			artistStack.getChildren().addAll(artistRect, artist);
			
			index.setFont(new Font(TITLE_FONT_SIZE));
			index.setStyle("-fx-font-weight: bold");
			index.setFill(Color.GRAY);
			
			title.setFont(new Font(TITLE_FONT_SIZE));
			title.setStyle("-fx-font-weight: bold");
			
			artist.setFont(new Font(ARTIST_FONT_SIZE));
			artist.setStyle("-fx-font-weight: bold");
			artist.setFill(Color.GRAY);
			
//			borderRectangle.setFill(null);
//			borderRectangle.setStroke(Color.rgb(216,216,216));
//			borderRectangle.setWidth(TILE_WIDTH); 
//			borderRectangle.setHeight(TILE_HEIGHT);
//			borderRectangle.setArcHeight(10.0d);
//	        borderRectangle.setArcWidth(10.0d);
			
			setAlignment(title, Pos.TOP_RIGHT);
			setAlignment(artist, Pos.BOTTOM_RIGHT);
			
			setMargin(border, new Insets(5, 5, 20, 5));
			setMargin(index, new Insets(10, 5, 5, 5));
			
			border.setTop(title);
			border.setBottom(artist);
			
			setLeft(index);
			setRight(border);
			
			setStyle("-fx-border-color: black; -fx-border-style: solid hidden solid hidden;");
		}
		
		/**
		 * Returns the index object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The index object that appears in the center of the
		 * 		rectangle
		 */
		private Text getIndex() {
			return index;
		}
		
		/**
		 * Returns the title object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The title object that appears in the center of the
		 * 		rectangle
		 */
		private Text getTitle() {
			return title;
		}
		
		/**
		 * Returns the artist object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The artist object that appears in the center of the
		 * 		rectangle
		 */
		private Text getArtist() {
			return artist;
		}
	}

}