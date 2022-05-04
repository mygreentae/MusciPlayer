package view;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import api.SpotifyAPI;
import controller.MusicPlayerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;
import javafx.scene.control.ProgressBar;


import utilities.SongLibrary;
import utilities.SpotifyAPIInvalidStreamException;
import utilities.SpotifyAPIInvalidURLException;

/**
 * Is the GUI of the music player. It displays all the components so the user can
 * play music, shuffle music, and create PlayLists.
 * 
 * All exceptions are caught in this class so if the user does something that would
 * crash the music player, there would be a window telling them about the action
 * they cannot do.
 * 
 * @author Leighanna/Jackson/Seth
 *
 */
public class MusicPlayerView extends Application implements Observer {
	private static MusicPlayerModel model;
	private static MusicPlayerController controller;
	private static SongLibrary songLibrary;
	
	// GUI Components
	private ControlMenu controls;
	private MediaBar mediaBar;
	
	//media player stuff

	private ArrayList<MediaPlayer> mediaPlayers;
	private Song CURRENT_SONG;
	private PlayList SHOW_PLAYLIST;
	private MediaPlayer CURRENT_PLAYER;
	private boolean shuffle = false;
	
	private static final int TILE_HEIGHT = 50;
	private static final int TILE_WIDTH = 100;
	private static final int TITLE_FONT_SIZE = 18;
	private static final int CUR_TITLE_SIZE = 30;
	private static final int CUR_ARTIST_SIZE = 10;
	private static final int ARTIST_FONT_SIZE = 10;
	private static final int SCROLL_MAX_HEIGHT = 400;
	private static final int SCROLL_MAX_WIDTH = 280;
	private static final int BUTTON_SIZE_1 = 60;
	private static final int BUTTON_SIZE_2 = 45;
	private static final int BUTTON_SIZE_3 = 30;
	private static Stage mainStage;
	
	
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage stage) throws IOException, URISyntaxException {
		mainStage = stage;
		songLibrary = new SongLibrary();
		model = new MusicPlayerModel(songLibrary);
		controller = new MusicPlayerController(model);

		mediaPlayers = new ArrayList<>();

		model.addObserver(this);

		
		VBox root = new VBox();
		HBox hbox = new HBox();
		
		ImageView image = setAlbumArt(controller.getCurSong());
		
		hbox.setPadding(new Insets(10, 10, 10, 10));
		
		VBox UI = new VBox();
		BorderPane menu = new Menu();
		
		ScrollPane songView = playListView();
		songView.setPrefViewportWidth(SCROLL_MAX_WIDTH);
		songView.setPrefViewportHeight(SCROLL_MAX_HEIGHT);
		
		UI.getChildren().addAll(songView);
		hbox.getChildren().addAll(image, UI);
		
		VBox curSongView = showCurSong();
		
		controls = new ControlMenu(mediaPlayers);
		curSongView.setAlignment(Pos.CENTER);

		controls.setAlignment(Pos.CENTER);
		root.getChildren().addAll(menu, hbox, curSongView, controls);

		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Music Player - No song playing...");
		stage.show();
	}
	
	private VBox showCurSong() {
		VBox vbox = new VBox();
		
		String title = "";
		String artist = "";
		Song song = CURRENT_SONG;
		if (song != null) {
			title = song.getName();
			artist = song.getArtist();
		}
		
		Text titleText = new Text();
		Text artistText = new Text();
		Text typeText = new Text();
		
		titleText.setText(title);
		artistText.setText(artist);
		
		titleText.setFont(new Font(CUR_TITLE_SIZE));
		titleText.setFill(Color.BLACK);
		titleText.setStyle("-fx-font-weight: bold");
		
		artistText.setFont(new Font(CUR_ARTIST_SIZE));
		artistText.setFill(Color.GRAY);
		artistText.setStyle("-fx-font-weight: bold");
		
		typeText.setFont(new Font(CUR_ARTIST_SIZE));
		typeText.setFill(Color.GRAY);
		typeText.setStyle("-fx-font-weight: bold");
		
		if (controller.isPlayingPlaylist()) {
			String playlist = controller.getCurPlaylist().getName();
			typeText.setText("Playing: " + playlist);
		} 
		
		
		vbox.setPadding(new Insets(0, 0, 20, 0));
		vbox.getChildren().addAll(titleText, artistText, typeText);
		
		return vbox;
	}

	private ScrollPane playListView() {
		ScrollPane scroller = new ScrollPane();
		GridPane songView = new GridPane();
		songView.setPadding(new Insets(5, 10, 0, 20));
		scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		
		ArrayList<Song> songList = songLibrary.getSongs();
		PlayList playlist = SHOW_PLAYLIST;
		if (playlist != null) {
			songList = playlist.getSongList();
		} 
		
		EventHandler<MouseEvent> playSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mediaPlayers.size() > 0) {
					for (MediaPlayer p : mediaPlayers) {
						p.stop();
					}
				}
				mediaPlayers = new ArrayList<>();
				Node source = (Node)mouseEvent.getTarget();
				Node p = source.getParent(); //idk why SongTile is double parent.
				Song song = ((SongTile)p).getSong();
				CURRENT_SONG = song;
				
				Media file = new Media(new File(song.getAudioPath()).toURI().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(file);
				mediaPlayers.add(mediaPlayer);
				
				mediaPlayer.setAutoPlay(true);
				
				if (!controller.isPlayingSong() || controller.getCurSong() == null) {
					controller.playPlaylist(controller.getPlaylist("Song Library"), shuffle, song);
					SHOW_PLAYLIST = controller.getCurPlaylist();
					controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
				} else {
					PlayList playlist = SHOW_PLAYLIST;
					controller.playPlaylist(playlist, shuffle, song);
					controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
					SHOW_PLAYLIST = controller.getCurPlaylist();
				}
				CURRENT_PLAYER = mediaPlayer;
				CURRENT_PLAYER.setOnEndOfMedia(() -> playNextSong(controller.getCurPlaylist(), controller.getCurSong()));
				
			}
		};

		EventHandler<MouseEvent> highlightSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Node source = (Node)mouseEvent.getTarget();
				SongTile t = (SongTile)source;
				Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
				t.setBackground(highlight);
				t.getPlayButton().setVisible(true);
	
				//controller.changeSong(song);
			}
		};
		
		EventHandler<MouseEvent> unhighlightSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Node source = (Node)mouseEvent.getTarget();
				SongTile t = (SongTile)source;
				Background highlight = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY));
				t.setBackground(highlight);
				t.getPlayButton().setVisible(false);
				
				if (t.getSong() == CURRENT_SONG) {
					Background highlight2 = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
					t.setBackground(highlight2);
				};			
				//controller.changeSong(song);
			}
		};
		
		for (int i = 0; i < songList.size(); i++) {
			SongTile songTile = new SongTile(songList.get(i));
			songTile.getTitle().setText(songList.get(i).getName());
			songTile.getArtist().setText(songList.get(i).getArtist());


			songTile.setOnMouseEntered(highlightSong);
			songTile.setOnMouseExited(unhighlightSong);
			songTile.getPlayButton().setOnMouseClicked(playSong);
			songTile.setPrefWidth(250);
			songView.add(songTile, 1, i);
			songTile.border.autosize();
		}
		//songView.getColumnConstraints().add(new ColumnConstraints(75));
		scroller.setContent(songView);
		return scroller;
	}

	private ImageView setAlbumArt(Song curSong) {
		
    	ImageView imageView = new ImageView();
    	if (CURRENT_SONG == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	}
    	else if (CURRENT_SONG.getCover() == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	} 
    	else {
    		try {
    			Image i = new Image(CURRENT_SONG.getCover().substring(4).strip());
    			imageView.setImage(i);
    		} catch (IllegalArgumentException e) {
    			imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    		}
    	}
    	
    	imageView.setFitHeight(400);
		imageView.setFitWidth(400);
		
    	return imageView;
    }
	
	/**
	 * This class encapsulates an entire Song object and displays it visually.
	 * It highlights the song when hovered over, highlights it while it is 
	 * playing, and is able to play it when clicking the playButton that is
	 * revealed when SongTile is hovered over with the mouse.
	 * 
	 * @author Leighanna/Jackson
	 *
	 */
	private class SongTile extends BorderPane {
		private static final int PLAY_BUTTON_SIZE = 25;
		
		private Button playButton;
		private Text title;
		private Text artist;
		
		public BorderPane border;
		private Rectangle titleRect;
		private Rectangle artistRect;
		
		private StackPane titleStack;
		private StackPane artistStack;
		
		private Song song;
		
		private SongTile(Song song) {
			this.song = song;
			playButton = new Button();
			title = new Text();
			artist = new Text();
			
			border = new BorderPane();
			
			if (song == CURRENT_SONG) {
				Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, 
						new CornerRadii(0), Insets.EMPTY));
				title.setFill(Color.AQUA);
				this.setBackground(highlight);
			};
			
			titleRect = new Rectangle();
			artistRect = new Rectangle();
			titleStack = new StackPane();
			artistStack = new StackPane();
			titleStack.getChildren().addAll(titleRect, title);
			artistStack.getChildren().addAll(artistRect, artist);
			
			title.setFont(new Font(TITLE_FONT_SIZE));
			artist.setFont(new Font(ARTIST_FONT_SIZE));
			artist.setFill(Color.GRAY);
			
			setAlignment(title, Pos.TOP_RIGHT);
			setAlignment(artist, Pos.BOTTOM_RIGHT);
			
			setMargin(border, new Insets(5, 5, 20, 30));

			//setMargin(border, new Insets(5, 5, 20, SCROLL_MAX_WIDTH/5));

			setMargin(playButton, new Insets(15, 5, 5, 5));
			
			border.setTop(title);
			border.setBottom(artist);
	
			
	        playButton = new Button();
			playButton.setVisible(false);
			playButton.setShape(new Circle(10));
			
			// has to be this order or the button will be messed up
			// idk why lol
			ImageView imageView = new ImageView(new Image ("utilities/buttons/play.png"));
	        imageView.setFitHeight(PLAY_BUTTON_SIZE);
	        imageView.setFitWidth(PLAY_BUTTON_SIZE);
	        
	        imageView.setPreserveRatio(true);
	        playButton.setGraphic(imageView);
	        playButton.setMaxWidth(Double.MAX_VALUE);    
	        playButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

	        
			//playButton.setOnAction(new EventHandler<ActionEvent> () {};

	        
			setMargin(playButton, new Insets(10, 10, 10, 5));
			
			setLeft(playButton);
			playButton.setAlignment(Pos.CENTER);
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
		private Button getButton() {
			return playButton;
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
		
		private Button getPlayButton() {
			return playButton;
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
		
		public Song getSong() {
			return song;
		}
	
	}
	

	/**
	 * The class to contain the control menu which are the buttons
	 * that allow the user to toggle fast forward/backward, skip/go back 
	 * songs, pause/play, and shuffle
	 * 
	 * @author Leighanna/Jackson
	 *
	 */
	private class ControlMenu extends HBox {
		
		
		private Button playPauseButton;
		private Button shuffleButton;
		private Button fastForwardButton;
		private Button backwardButton;
		private Button prevSongButton;
		private Button nextSongButton;
		private MediaPlayer player;
	    private List<MediaPlayer> players;
		
		private ControlMenu(ArrayList<MediaPlayer> players) {
			this.players = players;
			
			if (controller.getCurSong() != null && players.size() > 0) {
		        player = players.get(0);
			}
		
	        
			playPauseButton = new Button();
			shuffleButton = new Button();
			shuffleButton.setMaxSize(5, 5);
			fastForwardButton = new Button();
			backwardButton = new Button();
			prevSongButton = new Button();
			nextSongButton = new Button();
			
			setPausePlayButton();
			setShuffleButton();
			setFastForwardButton();
			setBackwardButton();
			setPrevSongButton();
			setNextSongButton();
			
			getChildren().addAll(prevSongButton, backwardButton, playPauseButton, fastForwardButton, nextSongButton, shuffleButton);
			setAlignment(Pos.CENTER);
		}
		
		/**
		 * Sets the image and size of the specified button.
		 * 
		 * @param b
		 * 		The button to set the image and size of
		 * @param filename
		 * 		the name and location of were the image of the image is
		 * @param size
		 * 		the size of the button
		 */
		private void setImage(Button b, String filename, int size) {
			b.setShape(new Circle(10));
			
			ImageView imageView = new ImageView(new Image ("utilities/buttons/" + filename));
	        imageView.setFitHeight(size);
	        imageView.setFitWidth(size);
	        imageView.setPreserveRatio(true);
	        b.setGraphic(imageView);
	        b.setMaxWidth(Double.MAX_VALUE);    
	        b.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			
		}
		
		/**
		 * Sets the functionality of the Pause and Play button into the meida player
		 */
		private void setPausePlayButton() {
			setImage(playPauseButton, "play.png", BUTTON_SIZE_1);
	        
			// Adding Functionality
	        // to play the media player
	        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent e)
	            {
	            	if (controller.isPlayingSong() || controller.isPlayingPlaylist()) {
	            		setImage(playPauseButton, "pause.png", BUTTON_SIZE_1);
	            	} else {
	            		setImage(playPauseButton, "play.png", BUTTON_SIZE_1);
	            	}
	            	
	            	if (controller.getCurSong() == null) {   
				        Platform.runLater(() -> {
					        Alert dialog = new Alert(AlertType.INFORMATION, "Please select song to play!", ButtonType.OK);
					        dialog.show();
					    });
				        return;
					} 
	            	
	            	if (player != null) {
	            		 Status status = player.getStatus(); // To get the status of Player
	 	                if (status == Status.PLAYING) {
	 	                    // If the status is Video playing
	 	                    if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
	 	                        // If the player is at the end of video
	 	                        player.seek(player.getStartTime()); // Restart the video
	 	                        player.play();
	 	                    }
	 	                    else {
	 	                        // Pausing the player
	 	                        player.pause();
	 	                      setImage(playPauseButton, "play.png", BUTTON_SIZE_1);
	 	                    }
	 	                } // If the video is stopped, halted or paused
	 	                if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) {
	 	                    player.play(); // Start the video
	 	                   setImage(playPauseButton, "pause.png", BUTTON_SIZE_1);
	 	                }
	            	} else {
	            		return;
	            	}
	            }
	            
	        });
		}
	        
		
		/**
		 * Sets the functionality of the Fast Forward button in the media player
		 */
		private void setFastForwardButton() {
			setImage(fastForwardButton, "forward.png", BUTTON_SIZE_2);
	        
	        fastForwardButton.setOnMousePressed(new EventHandler<MouseEvent> (){
	        	@Override
				public void handle(MouseEvent arg0) {
					if (player != null) {
						player.seek(player.getCurrentTime().add(Duration.seconds(10)));
					}
				}
			}) ;
		}
		
		/**
		 * Sets the functionality of the backward button in the media player.
		 */
		private void setBackwardButton() {
			setImage(backwardButton, "backward.png", BUTTON_SIZE_2);
	        
	        backwardButton.setOnMousePressed(new EventHandler<MouseEvent> (){
	        	@Override
				public void handle(MouseEvent arg0) {
					if (player != null) {
						player.seek(player.getCurrentTime().add(Duration.seconds(-10)));
					}
				}
			}) ;
		}
		
		/**
		 * Sets the functionality of the skip button in the media player.
		 */
		private void setNextSongButton() {
			setImage(nextSongButton, "next-song.png", BUTTON_SIZE_2);
	        
			nextSongButton.setOnAction(new EventHandler<ActionEvent> () {
				
				@Override
				public void handle(ActionEvent arg0) {
					if (player != null) {
						player.seek(player.getTotalDuration());
					}
				}
			});
		}
		
		/**
		 * Sets the functionality of the previous song button in the music player.
		 */
		private void setPrevSongButton() {
			setImage(prevSongButton, "prev-song.png", BUTTON_SIZE_2);
	        
			prevSongButton.setOnAction(new EventHandler<ActionEvent> () {

				@Override
				public void handle(ActionEvent arg0) {
					if (player != null) {
						Song s = controller.getCurSong();
						if (s.getIndex() == 0) {
							return;
						}
						Song prev = s;
						for (Song song : controller.getCurPlaylist().getPlayOrder()) {
							if (s.getIndex()== song.getIndex() + 1) {
								prev = song;
								break;
							}
						}
						
						if (mediaPlayers.size() > 0) {
							for (MediaPlayer p : mediaPlayers) {
								p.stop();
							}
						}
						mediaPlayers = new ArrayList<>();
						CURRENT_SONG = prev;
						
						Media file = new Media(new File(prev.getAudioPath()).toURI().toString());
						MediaPlayer mediaPlayer = new MediaPlayer(file);
						mediaPlayers.add(mediaPlayer);
						
						mediaPlayer.setAutoPlay(true);
						player = mediaPlayer;
						controller.changeSong(prev);
						mediaPlayer.setOnEndOfMedia(() -> playNextSong(controller.getCurPlaylist(), CURRENT_SONG));
						
					}
				}
			}) ;
		}
		
		/**
		 * Sets the functionality of the shuffle button in the music player.
		 */
		private void setShuffleButton() {
			setImage(shuffleButton, "shuffle.png", BUTTON_SIZE_3);
			shuffleButton.setMaxSize(500, 5);
			if (shuffle) {
				setImage(shuffleButton, "shuffleActive.png", BUTTON_SIZE_3);
			} else {
				setImage(shuffleButton, "shuffle.png", BUTTON_SIZE_3);
			}
			
			
			shuffleButton.setOnAction(new EventHandler<ActionEvent> () {

				@Override
				public void handle(ActionEvent arg0) {
					
					if (shuffle) {
						shuffle = false;
						update(model, null);
						return;
					} 	
					shuffle = true;	
		
					if (mediaPlayers.size() > 0) {
						for (MediaPlayer p : mediaPlayers) {
							p.stop();
						}
					}
					
					if (controller.getCurPlaylist() == null || SHOW_PLAYLIST == controller.getPlaylist("Song Library")) {
						controller.playPlaylist(controller.getPlaylist("Song Library"), true, null);
						controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
						SHOW_PLAYLIST = controller.getCurPlaylist();
					} else {
						PlayList playlist = SHOW_PLAYLIST;
						controller.playPlaylist(playlist, true, null);
						controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
						SHOW_PLAYLIST = controller.getCurPlaylist();
					}	
					Song song = controller.getCurPlaylist().getPlayOrder().get(0);
					
					mediaPlayers = new ArrayList<>();
					CURRENT_SONG = song;
					
					Media file = new Media(new File(song.getAudioPath()).toURI().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(file);
					mediaPlayers.add(mediaPlayer);
					
					mediaPlayer.setAutoPlay(true);
					player = mediaPlayer;
					
					controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
					
					controller.changeSong(song);
					
					mediaPlayer.setOnEndOfMedia(() -> playNextSong(controller.getCurPlaylist(), controller.getCurSong()));
				}
			}) ;
		}
	}

	/**
	 * Creates the top menu bar so the user can sort the songs by title/artist/release date, create
	 * and switch PlayLists, search for songs, and go back.
	 * 
	 * @author Leighanna/Jackson
	 *
	 */
	private class Menu extends BorderPane {
	
	private GridPane menu;
	private Button artistButton;
	private Button titleButton;
	private Button createPlaylistButton;
	private Button switchPlaylistButton;
	private Button searchButton;
	private Button dateButton;
	private Button backButton;
	
	private Menu() {
		createPlaylistButton = new Button("Create New Playlist");
		switchPlaylistButton = new Button("Switch Playlist");
		artistButton = new Button("Sort by Artist");
		titleButton = new Button("Sort by Title");
		dateButton = new Button("Sort by Date");
		backButton = new Button("Back");
		searchButton = new Button("Search");
		menu = new GridPane();
		
		// where the buttons are going to be placed in the GridPane
		GridPane.setConstraints(createPlaylistButton, 1, 0);
		GridPane.setConstraints(switchPlaylistButton, 2, 0);
		GridPane.setConstraints(artistButton, 3, 0);
		GridPane.setConstraints(titleButton, 4, 0);
		GridPane.setConstraints(dateButton, 5, 0);
		GridPane.setConstraints(backButton, 6, 0);
		GridPane.setConstraints(searchButton, 7, 0);
		menu.getChildren().addAll(createPlaylistButton, switchPlaylistButton, artistButton, titleButton, searchButton, dateButton, backButton);
		
		menu.setHgap(10);
        menu.setVgap(10);
		
		Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
		this.setBackground(highlight);
		addEventHandlers();
		setCenter(menu);
		
		
	}
	
	/**
	 * Add the event handlers to each button in the menu bar
	 */
	private void addEventHandlers() {	
		EventHandler<MouseEvent> createPlaylist = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Create Playlist");
				dialog.setHeaderText("Name Playlist");
				dialog.showAndWait().ifPresent(string -> 
			    {
			    	try {
			    	PlayList toAddto = controller.getPlaylist(string);   
				        Platform.runLater(() -> {
					        Alert error = new Alert(AlertType.INFORMATION, "The playlist " + toAddto.getName() + " already exists", ButtonType.OK);
					        error.show();
					    });

			    	}
			    	catch (IllegalArgumentException e) {
			    		controller.makePlaylist(string);
			    	}
				    	
			    });
				 
			}
			
		};
		
		EventHandler<MouseEvent> switchPlaylist = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				if (controller.getAllPlaylists().size() == 0) {
					Platform.runLater(() -> {
				        Alert error = new Alert(AlertType.INFORMATION, "You do not have any playlists!", ButtonType.OK);
				        error.show();
				    });
			        return;
				}
				
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Switch Playlist");
				dialog.setHeaderText("Which playlist would you like to switch to?");
				dialog.setContentText("These are the playlists you have:\n" + controller.getAllPlaylistsAsString() + "Total Playlists: " + controller.getAllPlaylists().size());
				dialog.showAndWait().ifPresent(string -> 
			    {
			    	try {
			    		PlayList isPresent = controller.getPlaylist(string);
			    	} catch (IllegalArgumentException e) {
			    		 Platform.runLater(() -> {
						        Alert error = new Alert(AlertType.INFORMATION, "That playlist does not exist!", ButtonType.OK);
						        error.show();
						    });
					        return;
			    	}
			    	
			    	PlayList toPlay = controller.getPlaylist(string);
			    	
			    	if (toPlay.getSize() > 0) {
			    		SHOW_PLAYLIST = toPlay;
			    		update(model, null);
			    	}
			    	else {
			    		 Platform.runLater(() -> {
						        Alert error = new Alert(AlertType.INFORMATION, "This playlist is empty!", ButtonType.OK);
						        error.show();
						    });
					        return;
			    	}
				    	
			    });
				 
			}
			
		};
		
		
		EventHandler<MouseEvent> sortPlaylistbyArtist = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				PlayList curPlaylist = SHOW_PLAYLIST;
				
				PlayList songs = new PlayList(songLibrary.getSongs());
				if (curPlaylist != null){
					songs = curPlaylist;
					controller.sortArtist(songs);			
				} else {
					controller.sortArtist(songs);
				}
				update(model, null);
			}
		};
		
		
		EventHandler<MouseEvent> sortPlaylistbyTitle = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				PlayList curPlaylist = SHOW_PLAYLIST;
				
				PlayList songs = new PlayList(songLibrary.getSongs());
				if (curPlaylist != null){
					songs = curPlaylist;
					controller.sortTitle(songs);
				} else {
					controller.sortTitle(songs);
				}
				update(model, null);
			}
		};
		
		EventHandler<MouseEvent> sortPlaylistbyDate = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				PlayList curPlaylist = SHOW_PLAYLIST;
				
				PlayList songs = new PlayList(songLibrary.getSongs());
				if (curPlaylist != null){
					songs = curPlaylist;
					controller.sortDate(songs);
				} else {
					controller.sortDate(songs);
				}
				update(model, null);
			}
		};
		
		
		EventHandler<MouseEvent> back = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				SHOW_PLAYLIST = controller.getCurPlaylist();
				if (SHOW_PLAYLIST == null) {
					SHOW_PLAYLIST = new PlayList(songLibrary.getSongs());
				}
				else if (controller.getCurPlaylist().getSongList() 
						== controller.getCurPlaylist().getOriginalOrder()){
					System.out.println("yeet2");
					SHOW_PLAYLIST = new PlayList(songLibrary.getSongs());
				} else {	
					System.out.println("yeet3");
					controller.getCurPlaylist().returnToOriginalOrder();
					SHOW_PLAYLIST = new PlayList(songLibrary.getSongs());
				}
				update(model, null);
			}
		};
		
		EventHandler<MouseEvent> search = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Search for Song");
				dialog.setHeaderText("Type in a song to search");
//				dialog.setContentText("These are the playlists you have:\n" + controller.getAllPlaylistsAsString() + "Total Playlists: " + controller.getAllPlaylists().size());
				dialog.showAndWait().ifPresent(string -> 
			    {
			    	String toSearch[] = string.split(",");
			    	if ( toSearch.length != 2 || toSearch == null) {
			    		Platform.runLater(() -> {
					        Alert error = new Alert(AlertType.INFORMATION, "Please enter only artist, song in that format!", ButtonType.OK);
					        error.show();
					    });
				        return;
			    	}
			    	else {
			    		try {
							SpotifyAPI.authenticate();
						} catch (SpotifyAPIInvalidURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SpotifyAPIInvalidStreamException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    		try {
							SpotifyAPI.getToken();
						} catch (SpotifyAPIInvalidURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SpotifyAPIInvalidStreamException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    		try {
							Song toAdd = SpotifyAPI.getMetadata(toSearch[0], toSearch[1]);
							songLibrary.addSong(toAdd);
							PlayList songLib = controller.getPlaylist("Song Library");
							songLib.addSong(toAdd);
							update(model, null);
						} catch (Exception e) {
							Platform.runLater(() -> {
						        Alert error = new Alert(AlertType.INFORMATION, "Oops! Something went wrong!", ButtonType.OK);
						        error.show();
						    });
							e.printStackTrace();
						}

			    		
			    	}
				    	
			    });
	
			}
		};
		
		createPlaylistButton.addEventFilter(MouseEvent.MOUSE_CLICKED, createPlaylist);
		switchPlaylistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, switchPlaylist);
		artistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyArtist);
		titleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyTitle);
		dateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyDate);
		backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, back);
		searchButton.addEventHandler(MouseEvent.MOUSE_CLICKED, search);
		}
	}
	
	/**
	 * The menu that pops up when a song is playing. This menu allows the user
	 * to add that selected song to a playlist or favorite the song.
	 * 
	 * @author Leighanna
	 *
	 */
	public class SongMenu extends BorderPane{
		private GridPane menu;
		Button addToPlaylistButton;
		Button favoriteButton;
		
		public SongMenu() {
			menu = new GridPane();
			
			addToPlaylistButton = new Button("Add Song to PlayList");
			favoriteButton = new Button();
			
			if (controller.getCurSong()!= null) {
				if (controller.getCurSong().isFavorite()) {
					favoriteButton.setText("Unfavorite Song");
				} else {
					favoriteButton.setText("Favorite Song ");
				}
			} else {
				favoriteButton.setText("Favorite Song");
			}
			
			addEventHandlers();
			
			GridPane.setConstraints(addToPlaylistButton, 1, 0);
			GridPane.setConstraints(favoriteButton, 2, 0);
			
			menu.getChildren().addAll(addToPlaylistButton, favoriteButton);
			menu.setHgap(10);
	        menu.setVgap(10);
			
			setCenter(menu);
			
		}
		
		/**
		 * Add the event handlers for each button in the Song menu.
		 */
		private void addEventHandlers() {
			EventHandler<MouseEvent> addSongToPlaylist = new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {	
					if (controller.getCurSong() == null) {   
				        Platform.runLater(() -> {
					        Alert error = new Alert(AlertType.INFORMATION, "Please select song to play!", ButtonType.OK);
					        error.show();
					    });
				        return;
					} 
				    
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Adding Song to Playlist");
					dialog.setHeaderText("Which Playlist would you like to add to?\n");
					dialog.setContentText("These are the playlists you have:\n" + controller.getAllPlaylistsAsString() + "Total Playlists: " + controller.getAllPlaylists().size() + "\n");
				    dialog.showAndWait().ifPresent(string -> 
				    {
				    	PlayList toAddto = model.getPlaylist(string);
				    	if (toAddto == null) {   
					        Platform.runLater(() -> {
						        Alert error = new Alert(AlertType.INFORMATION, "The playlist you want to add to does not exist", ButtonType.OK);
						        error.show();
						    });
					        return;
						}
				    	else if (toAddto.contains(controller.getCurSong())) {
				    		Platform.runLater(() -> {
						        Alert error = new Alert(AlertType.INFORMATION, "This song already exists in playlist.", ButtonType.OK);
						        error.show();
						    });
					        return;
				    	} else if (toAddto == controller.getFavorites()) {
				    		Platform.runLater(() -> {
						        Alert error = new Alert(AlertType.INFORMATION, "Add this Song to Favorites by favoriting it.", ButtonType.OK);
						        error.show();
						    });
					        return;
				    	}
				    	else {
				    		controller.addToPlaylist(toAddto, controller.getCurSong());
				    		//toAddto.addSong(controller.getCurSong());
				    	}
				    	
				    });
				}	
			};
			
			EventHandler<MouseEvent> favorite = new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					Song curSong = controller.getCurSong();
					if (curSong == null) {   
				        Platform.runLater(() -> {
					        Alert error = new Alert(AlertType.INFORMATION, "Please select song to play!", ButtonType.OK);
					        error.show();
					    });
				        return;
					} 
					if (curSong.isFavorite()) {
						controller.removeFromFavorites(curSong);
						curSong.unFavorite();
						
						favoriteButton.setText("Favorite");
					} else {
						controller.addToFavorites(curSong);
						curSong.makeFavorite();
						favoriteButton.setText("Unfavorite");
					}
					
				}
			};
			
			
			addToPlaylistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addSongToPlaylist);
			favoriteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, favorite);
		}
	}

	/**
	 * Update the View
	 */
	@Override
	public void update(Observable o, Object arg) {
		VBox root = new VBox();
		HBox hbox = new HBox();
		
		ImageView image = setAlbumArt(controller.getCurSong());
		
		hbox.setPadding(new Insets(10, 10, 10, 10));
		
		VBox UI = new VBox();
		BorderPane menu = new Menu();
		BorderPane songMenu = new SongMenu();
		
		ScrollPane songView = playListView();
		songView.setPrefViewportWidth(SCROLL_MAX_WIDTH);
		songView.setPrefViewportHeight(SCROLL_MAX_HEIGHT);
		
		UI.getChildren().addAll(songView);
		hbox.getChildren().addAll(image, UI);
		
		VBox curSongView = showCurSong();

		controls = new ControlMenu(mediaPlayers);
		
		if (controller.getCurSong() != null && mediaPlayers.size() > 0) {
			mediaBar = new MediaBar(mediaPlayers);
			curSongView.setAlignment(Pos.CENTER);
			controls.setAlignment(Pos.CENTER);
			controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
			root.getChildren().addAll(menu, songMenu, hbox, curSongView, controls, mediaBar);
		} else {
			curSongView.setAlignment(Pos.CENTER);
			controls.setAlignment(Pos.CENTER);
			root.getChildren().addAll(menu, hbox, curSongView, controls);
		}

		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		if (controller.getCurSong() != null) {
			mainStage.setTitle("Music Player - " + controller.getCurSong().getName() + " by "+ controller.getCurSong().getArtist());
		} else {
			mainStage.setTitle("Music Player - No song playing...");
		}
		
		mainStage.show();
		
	}
	
	
	/**
	 * Recursively assigns next song to play based on the current PlayList 
	 * and the current song. Calls this same function to determine what Song
	 * to play after said next song. 
	 * 
	 * Base case: When the Song passed as an argument has an index equal to
	 * the length of the PlayList it belongs to.
	 * 
	 * @param curPlaylist
	 * 		the current PlayList that is being played.
	 * @param curSong
	 * 		the current Song that is playing.
	 */
	public void playNextSong(PlayList curPlaylist, Song curSong) {
		if (curSong.getIndex() == curPlaylist.getSize() - 1) {
			controls.setImage(controls.playPauseButton, "play.png", BUTTON_SIZE_1);
			return;
		}
		mediaPlayers = new ArrayList<>();

		int index = curSong.getIndex();
		Song nextSong = curPlaylist.getPlayOrder().get(index + 1);
		
		Media file = new Media(new File(nextSong.getAudioPath()).toURI().toString());
		
		MediaPlayer nextMediaPlayer = new MediaPlayer(file);
		mediaPlayers.add(nextMediaPlayer);
		
		nextMediaPlayer.setAutoPlay(true);
		CURRENT_PLAYER = nextMediaPlayer;
		CURRENT_SONG = nextSong;
		controller.changeSong(nextSong);
		
		nextMediaPlayer.setOnEndOfMedia(() -> playNextSong(curPlaylist, nextSong));
	}
	
}

