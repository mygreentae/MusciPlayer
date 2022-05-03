package view;


import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaBar extends HBox { // MediaBar extends Horizontal Box

    // introducing Sliders
    Slider time = new Slider(); // Slider for time
    Slider vol = new Slider(); // Slider for volume
    Button PlayButton = new Button("||"); // For pausing the player
    Button skipButton = new Button(">>");
    Label volume = new Label("Volume: ");
    MediaPlayer player;
    MediaView mediaView;
    List<MediaPlayer> players;

    public MediaBar(ArrayList<MediaPlayer> players)
    { // Default constructor taking
        // the MediaPlayer object
        this.players = players;
        player = players.get(0);
        mediaView = new MediaView(player);
        
        setAlignment(Pos.CENTER); // setting the HBox to center
        setPadding(new Insets(5, 10, 5, 10));
        // Settih the preference for volume bar
        vol.setPrefWidth(70);
        vol.setMinWidth(30);
        vol.setValue(100);
        HBox.setHgrow(time, Priority.ALWAYS);
        PlayButton.setPrefWidth(30);
        
        // Adding the components to the bottom

        getChildren().add(time); // time slider
        getChildren().add(volume); // volume slider
        getChildren().add(vol);

        // Providing functionality to time slider
        player.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                updatesValues();
            }
        });

        // Inorder to jump to the certain part of video
        time.valueProperty().addListener(new InvalidationListener() { 
            public void invalidated(Observable ov) 
            {
                if (time.isPressed()) { // It would set the time 
                    // as specified by user by pressing 
                    player.seek(player.getMedia().getDuration().multiply(time.getValue() / 100)); 
                }
            }
        });

        // providing functionality to volume slider 
        vol.valueProperty().addListener(new InvalidationListener() { 
            public void invalidated(Observable ov) 
            { 
                if (vol.isPressed()) { 
                    player.setVolume(vol.getValue() / 100); // It would set the volume 
                    // as specified by user by pressing 
                }
            }
        });
        
        
        
        skipButton.setOnAction(actionEvent -> {
            final MediaPlayer curPlayer = player;
            MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
            nextPlayer.seek(Duration.ZERO);
            mediaView.setMediaPlayer(nextPlayer);
            nextPlayer.play();
        });
    }

    // Outside the constructor
    protected void updatesValues()
    {
        Platform.runLater(new Runnable() {
            public void run()
            {
                // Updating to the new time value
                // This will move the slider while running your video
                time.setValue(player.getCurrentTime().toMillis()/
                           player.getTotalDuration()
                                      .toMillis() * 100);
            }
        });
    }
}

