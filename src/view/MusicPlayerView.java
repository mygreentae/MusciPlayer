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
import java.util.concurrent.TimeUnit;

import controller.MusicPlayerController;
import javafx.application.Application;
import javafx.application.Platform;
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

public class MusicPlayerView extends Application implements Observer {
	private static MusicPlayerModel model;
	private static MusicPlayerController controller;
	private static SongLibrary songLibrary;
	
	private MediaPlayer player;
	
	private ControlMenu controls;
	
	//edit modes/ control what is shown in playlistView
	private static Boolean editMode;
	private static Boolean playListViewMode;
	private static Boolean searchMode;
	//private static something backbutton shit that
	//probably edits these to be either true or false.
	//that makes sense i think, click like "Back" and it makes edit mode 
	//false which changes the view
	
	//media player stuff
	private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private MediaBar mediaBar;
	
	private String MEDIA_URL = "";
	
	private ArrayList<Thread> threads;
	private ArrayList<MediaPlayer> mediaPlayers;
	
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

		threads = new ArrayList<>();
		mediaPlayers = new ArrayList<>();
		
		Song song = songLibrary.getSongs().get(0);
		String path = song.getAudioPath();
		File file = new File(path);
		String MEDIA_URL = file.toURI().toString();
		//System.out.println(MEDIA_URL);
		media = new Media(MEDIA_URL);
		mediaPlayer = new MediaPlayer(media);
		mediaView = new MediaView(mediaPlayer);

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
		Song song = controller.getCurSong();
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
		} else if (controller.isPlayingQueue()) {
			typeText.setText("Queue");
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
		
		//change to current song queue
		//ArrayList<Song> songList = controller.getCurPlaylist().getSongList();
		// add if (curPlaylist == null) {
		// then this line, otherwise, songList == curPlaylist;
		
		ArrayList<Song> songList = songLibrary.getSongs();
		PlayList playlist = controller.getCurPlaylist();
		if (playlist != null) {
			songList = playlist.getSongList();
		} 
		
		EventHandler<MouseEvent> playSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				stopThreads();
				mediaPlayers = new ArrayList<>();
				Node source = (Node)mouseEvent.getTarget();
				Node p = source.getParent(); //idk why SongTile is double parent.
				Song song = ((SongTile)p).getSong();
				
				Media file = new Media(new File(song.getAudioPath()).toURI().toString());
				mediaPlayer = new MediaPlayer(file);
				mediaView = new MediaView(mediaPlayer);
				mediaPlayers.add(mediaPlayer);
				Runnable runnable =
					    new Runnable(){
							public void run() {
								mediaPlayer.setAutoPlay(true);
					        }
					    };
				//controller.(librarySongs, false, null);  
				Thread thread = new Thread(runnable);
				threads.add(thread);
				thread.start();
				
				if (controller.isPlayingQueue() || !controller.isPlayingSong() || controller.getCurSong() != null) {
					if (controller.getCurPlaylist() == null) {
						controller.playPlaylist(controller.getPlaylist("Song Library"), false, song);
					}
					else {
						controller.changeSong(song);
					}
					controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
				} else {
					PlayList playlist = controller.getCurPlaylist();
					controller.playPlaylist(playlist, false, song);
					controls.setImage(controls.playPauseButton, "pause.png", BUTTON_SIZE_1);
				}
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
				
				if (t.getSong() == controller.getCurSong()) {
					Background highlight2 = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
					t.setBackground(highlight2);
				};			
				//controller.changeSong(song);
			}
		};
		
		for (int i = 0; i < songList.size(); i++) {
			SongTile songTile = new SongTile(songList.get(i));
//			songTile.getIndex().setText(Integer.toString(i+1));
			songTile.getTitle().setText(songList.get(i).getName());
			songTile.getArtist().setText(songList.get(i).getArtist());
			
			//songTile.setOnMouseClicked(playSong);
//			songTile.getPlayButton().addEventFilter(MouseEvent.MOUSE_CLICKED, playSong);

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
		System.out.println("cursong");
		System.out.println(curSong);
		
    	ImageView imageView = new ImageView();
    	if (curSong == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	}
    	else if (curSong.getCover() == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	} 
    	else {
    		System.out.println(curSong.getCover().substring(4));
    		System.out.println("curSong cover");
    		//imageView.setImage(new Image("images/monteroArt.jpg"));
    		//imageView.setImage(new Image("images/monteroArt.jpg"));
    		try {
    			Image i = new Image(curSong.getCover().substring(4).strip());
    			imageView.setImage(i);
    			System.out.println("image path");
    			System.out.println(i.getUrl());
    		} catch (IllegalArgumentException e) {
    			System.out.println("error with album image");
    			imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    		}
    		// change
    	}
    	
    	imageView.setFitHeight(400);
		imageView.setFitWidth(400);
		
    	return imageView;
    }
	
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
			
			if (song == controller.getCurSong() && controller.getCurSong() != null) {
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
	

	private class ControlMenu extends HBox {
		
		
		private Button playPauseButton;
		private Button shuffleButton;
		private Button fastForwardButton;
		private Button backwardButton;
		private Button prevSongButton;
		private Button nextSongButton;
		private MediaPlayer player;
	    private MediaView mediaView;
	    private List<MediaPlayer> players;
		
		private ControlMenu(ArrayList<MediaPlayer> players) {
			this.players = players;
			
			if (controller.getCurSong() != null) {
		        player = players.get(0);
			}
			
	        mediaView = new MediaView(player);
	        
			playPauseButton = new Button();
			shuffleButton = new Button();
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
		
		private void setImage(Button b, String filename, int size) {
			b.setShape(new Circle(10));
			
			ImageView imageView = new ImageView(new Image ("utilities/buttons/" + filename));
	        imageView.setFitHeight(size);
	        imageView.setFitWidth(size);
	        imageView.setPreserveRatio(true);
	        b.setGraphic(imageView);
	        b.setMaxWidth(Double.MAX_VALUE);    
	        //b.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			
		}
		
		private void setPausePlayButton() {
			setImage(playPauseButton, "play.png", BUTTON_SIZE_1);
	        
	     // Adding Functionality
	        // to play the media player
	        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent e)
	            {
	            	System.out.println("playpuase touched");
	            	if (controller.isPlayingSong() || controller.isPlayingPlaylist()) {
	            		setImage(playPauseButton, "pause.png", BUTTON_SIZE_1);
	            		System.out.println("playing song");
	            	} else {
	            		setImage(playPauseButton, "play.png", BUTTON_SIZE_1);
	            		System.out.println("paused");
	            	}
	            	
	            	if (controller.getCurSong() == null) {   
	            		System.out.println("curSong == null");
				        Platform.runLater(() -> {
					        Alert dialog = new Alert(AlertType.INFORMATION, "Please select song to play!", ButtonType.OK);
					        dialog.show();
					    });
				        return;
					} 
	            	
	            	if (player != null) {
	            		System.out.println("curSong != null");
	            		 Status status = player.getStatus(); // To get the status of Player
	 	                if (status == Status.PLAYING) {
	 	                	System.out.println("status = playing");
	 	                    // If the status is Video playing
	 	                    if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
	 	                        // If the player is at the end of video
	 	                        player.seek(player.getStartTime()); // Restart the video
	 	                        player.play();
	 	                        System.out.println("play");
	 	                    }
	 	                    else {
	 	                        // Pausing the player
	 	                    	System.out.println("pausing");
	 	                        player.pause();
	 	                       System.out.println("pause");
	 	                      setImage(playPauseButton, "play.png", BUTTON_SIZE_1);
	 	                    }
	 	                } // If the video is stopped, halted or paused
	 	                if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) {
	 	                	System.out.println("plays2");
	 	                    player.play(); // Start the video
	 	                   setImage(playPauseButton, "pause.png", BUTTON_SIZE_1);
	 	                }
	            	} else {
	            		System.out.println("Player is null");
	            	}
	            }
	            
	        });
		}
	        
		
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
		
		private void setNextSongButton() {
			setImage(nextSongButton, "next-song.png", BUTTON_SIZE_2);
	        
			nextSongButton.setOnAction(new EventHandler<ActionEvent> () {

				@Override
				public void handle(ActionEvent arg0) {
					if (player != null) {
						player.seek(Duration.seconds(controller.getCurSong().getDuration() - .1));
					}
				}
			});
		}
		
		private void setPrevSongButton() {
			setImage(prevSongButton, "prev-song.png", BUTTON_SIZE_2);
	        
			prevSongButton.setOnAction(new EventHandler<ActionEvent> () {

				@Override
				public void handle(ActionEvent arg0) {
					if (player != null) {
						player.seek(Duration.seconds(0));
					}
				}
			}) ;
		}
		
		private void setShuffleButton() {
			setImage(shuffleButton, "shuffle.png", BUTTON_SIZE_3);
	        
			shuffleButton.setOnAction(new EventHandler<ActionEvent> () {

				@Override
				public void handle(ActionEvent arg0) {
					PlayList librarySongs = new PlayList(songLibrary.getSongs());
					if (controller.getCurPlaylist() == null) {
						controller.playPlaylist(librarySongs, true, controller.getCurSong());
					} else {
						controller.playPlaylist(controller.getCurPlaylist(), true, controller.getCurSong());
					}	
				}
			}) ;
		}
	}

	
	private class Menu extends BorderPane {
	
	private GridPane menu;
	private Button artistButton;
	private Button titleButton;
	private Button createPlaylistButton;
	private Button switchPlaylistButton;
	
	private Menu() {
		createPlaylistButton = new Button("Create New Playlist");
		switchPlaylistButton = new Button("Switch Playlist");
		artistButton = new Button("Sort by Artist");
		titleButton = new Button("Sort by Title");
		menu = new GridPane();
		
		GridPane.setConstraints(createPlaylistButton, 1, 0);
		GridPane.setConstraints(switchPlaylistButton, 2, 0);
		GridPane.setConstraints(artistButton, 3, 0);
		GridPane.setConstraints(titleButton, 4, 0);
		menu.getChildren().addAll(createPlaylistButton, switchPlaylistButton, artistButton, titleButton);
		
		menu.setHgap(10);
        menu.setVgap(10);
		
		Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
		this.setBackground(highlight);
		addEventHandlers();
		setCenter(menu);
		
		
	}
	
	
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
			    	PlayList toPlay = controller.getPlaylist(string);
			    	if (toPlay.getSize() > 0) {
			    		controller.playPlaylist(toPlay, false, toPlay.getSongList().get(0));
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
				System.out.println("sorted by artist");
				
				PlayList curPlaylist = controller.getCurPlaylist();
				
				ArrayList<Song> songs = songLibrary.getSongs();
				if (curPlaylist != null){
					songs = curPlaylist.getSongList();
					curPlaylist.setSongList(controller.sortArtist(songs));
				} else {
					songLibrary.setSongs(controller.sortArtist(songs));
					for (Song song : songLibrary.getSongs()) {
						System.out.println(song.getName());
					}
				}
				update(model, null);
			}
		};
		
		
		EventHandler<MouseEvent> sortPlaylistbyTitle = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				System.out.println("sorted by title");
				
				PlayList curPlaylist = controller.getCurPlaylist();
				
				ArrayList<Song> songs = songLibrary.getSongs();
				if (curPlaylist != null){
					songs = curPlaylist.getSongList();
					curPlaylist.setSongList(controller.sortTitle(songs));
				} else {
					songLibrary.setSongs(controller.sortTitle(songs));
					for (Song song : songLibrary.getSongs()) {
						System.out.println(song.getName());
					}
				}
				update(model, null);
			}
		};
		
		createPlaylistButton.addEventFilter(MouseEvent.MOUSE_CLICKED, createPlaylist);
		switchPlaylistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, switchPlaylist);
		artistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyArtist);
		titleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyTitle);
		}
	}
	
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
		
		private void addEventHandlers() {
			EventHandler<MouseEvent> addSongToPlaylist = new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {
					
					System.out.println("Add Song to playlist");
					
					
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
				    	}
				    	else {
				    		toAddto.addSong(controller.getCurSong());
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
					System.out.println(controller.getCurSong().isFavorite());
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
	 * Stops any running threads
	 */
	@SuppressWarnings("deprecation")
	private void stopThreads() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
		
		for (MediaPlayer player : mediaPlayers) {
			player.stop();
		}
		for (Thread thread : threads) {
			thread.stop();
		}
		System.out.println("Stopped threads");
	}
	
}

