package controller;

import model.MusicPlayerModel;

public class MusicPlayerController {

	
	private MusicPlayerModel model; 
	
	
	
	public MusicPlayerController(MusicPlayerModel model) {
		this.model = model; 
	}
	
	public void play() {
		model.play();
		
	}

	public void stop() {
		model.stop();
	}
	
}
