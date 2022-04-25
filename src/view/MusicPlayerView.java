package view;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import controller.MusicPlayerController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;

import utilities.SongLibrary;

public class MusicPlayerView extends Application {
	
	private static MusicPlayerController controller;
	private static MusicPlayerModel model; 
	private static SongLibrary songLibrary;
	
	private MediaPlayer player;
	private static final long JUMP_BY = 5000; // this is in milli secs

	
	private static final int TILE_HEIGHT = 50;
	private static final int TILE_WIDTH = 100;
	private static final int TITLE_FONT_SIZE = 20;
	private static final int CUR_TITLE_SIZE = 30;
	private static final int CUR_ARTIST_SIZE = 10;
	private static final int ARTIST_FONT_SIZE = 10;
	private static final int SCROLL_MAX_HEIGHT = 350;
	private static final int SCROLL_MAX_WIDTH = 250;
	
	
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage stage) throws IOException {
		songLibrary = new SongLibrary();
		model = new MusicPlayerModel(songLibrary);
		controller = new MusicPlayerController(model);
		
		Song song = controller.search("400km");
		controller.changeSong(song);

		VBox root = new VBox();
		HBox hbox = new HBox();
		
		// get and set cover art
//		ImageView imageView = new ImageView();
//		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
//		imageView.setFitHeight(400);
//		imageView.setFitWidth(400);
		
		ImageView image = setAlbumArt(controller.getCurSong());
		
		hbox.setPadding(new Insets(10, 10, 10, 10));
		
		ScrollPane songView = playListView();
		songView.setPrefViewportWidth(SCROLL_MAX_WIDTH);
		songView.setPrefViewportHeight(SCROLL_MAX_HEIGHT);
		
		hbox.getChildren().addAll(image, songView);
		
		VBox curSongView = showCurSong();
		GridPane controls = setButtons();
		
		curSongView.setAlignment(Pos.CENTER);
		controls.setAlignment(Pos.CENTER);
		root.getChildren().addAll(hbox, curSongView, controls);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Music Player");
		stage.show();
		
//		handleEvents(playPause, stop);
		
	}
	
	private VBox showCurSong() {
		VBox vbox = new VBox();
		
		Song song = controller.getCurSong();
		String title = song.getName();
		String artist = song.getArtist();
		
		Text titleText = new Text();
		Text artistText = new Text();
		
		titleText.setText(title);
		artistText.setText(artist);
		
		titleText.setFont(new Font(CUR_TITLE_SIZE));
		titleText.setFill(Color.BLACK);
		titleText.setStyle("-fx-font-weight: bold");
		
		artistText.setFont(new Font(CUR_ARTIST_SIZE));
		artistText.setFill(Color.GRAY);
		artistText.setStyle("-fx-font-weight: bold");
		
		
		vbox.getChildren().addAll(titleText, artistText);
		
		
		return vbox;
	}
	
	private GridPane setButtons() {
		GridPane gp = new GridPane();
        gp.setHgap(10);

        Button play = new Button("Play");
        GridPane.setConstraints(play, 0,0);
        play.setOnAction(event->  playAudio());

        Button pause = new Button("Pause");
        GridPane.setConstraints(pause, 1,0);
        pause.setOnAction(event -> pauseAudio());

        Button resume = new Button("Resume");
        GridPane.setConstraints(resume, 2,0);
        resume.setOnAction(event -> resumeAudio());

        Button stop = new Button("Stop");
        GridPane.setConstraints(stop, 3,0);
        stop.setOnAction(event ->  stopAudio());

        Button restart = new Button("Restart");
        GridPane.setConstraints(restart, 4,0);
        restart.setOnAction(event ->  restartAudio());

        Button jump = new Button("Jump >");
        GridPane.setConstraints(jump, 5,0);
        jump.setOnAction(event ->  jump(JUMP_BY));
        
        
        // figure out!
//        Label time = new Label();
//        GridPane.setConstraints(time, 6,0);
//        time.textProperty().bind(player.currentTimeProperty().asString("%.4s") );

        gp.getChildren().addAll(play, pause, resume, stop, restart, jump);
        
        return gp;
	}
	
	//play audio 
    public void playAudio() {
        player.play();
    }

    //pause audio
    public  void pauseAudio() {
        if (player.getStatus().equals(Status.PAUSED)) {
            System.out.println("audio is already paused");
            return;
        }
        player.pause();
    }

    //resume audio
    public void resumeAudio()
    {
        if (player.getStatus().equals(Status.PLAYING))
        {
            System.out.println("Audio is already playing");
            return;
        }
        playAudio();
    }

    //restart audio
    public void restartAudio() {
        player.seek(Duration.ZERO);
        playAudio();
    }

    // stop audio
    public void stopAudio() {
       player.stop();
    }

    //jump by c millis 
    public void jump(long c) {
        player.seek(player.getCurrentTime().add(Duration.millis(c)));
    }

	private ScrollPane playListView() {
		ScrollPane scroller = new ScrollPane();
		GridPane songView = new GridPane();
		songView.setPadding(new Insets(10, 10, 10, 20));
		
		//change to current song queue
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
	
//	private void handleEvents(Button playPause, Button stop) {
//		playPause.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent ae) {
//				controller.play();
//				
//			}
//			
//		});
//		stop.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent ae) {
//				controller.stop();
//				
//			}
//			
//		});
//	}
	

	private ImageView setAlbumArt(Song curSong) {
    	ImageView imageView = new ImageView();
    	if (curSong.getCover() == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	} 
    	else {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg")); // change
    	}
    	
    	imageView.setFitHeight(400);
		imageView.setFitWidth(400);
		
    	return imageView;
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
			
			artist.setFont(new Font(ARTIST_FONT_SIZE));
			artist.setFill(Color.GRAY);
			
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
