package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	//declear all the buttons and functions that will be used later
	
	@FXML
	private Pane pane;
	@FXML
	private Label songLabel; //This is for the name of the song currently playing
	@FXML
	private Button playButton, pauseButton, stopButton,resetButton, rewindButton, forwardButton,previousButton,nextButton,fwd_btn, play_btn, pause_btn, rew_btn,reset_btn; //This is the controls
	@FXML
	private ComboBox<String> speedBox; //The speed button to change speed
	@FXML
	private Slider volumeSlider; //To change volume
	@FXML
	private ProgressBar songProgressBar; //Keeps track of the song timer
	 
	
	private Media media;
	private MediaPlayer mediaPlayer; //To play the songs
	
	private File directory; //Holds the music directory
	private File[] files; //Array of music files
	
	private ArrayList<File> songs; //Access our playlist
	
	private int songNumber; //keeps track of what number song is playing currently
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200}; //An array of integers for the speed of the songs
	
	private Timer timer;  //Keep track of the progress bar
	private TimerTask task;
	
	private boolean running; //For when a player is currently playing a song or not
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		//Initialize the array list of files
		songs = new ArrayList<File>();
		
		directory = new File("music");  //Sets the directory to new File while passing the Music folder that contains the songs
		
		files = directory.listFiles(); //Get all the songs in the directory until there is no more songs
		
		if(files != null) {
			
			for(File file : files) {    // A for loop is called if there is still a song in the array list and it adds it to the file
				
				songs.add(file);
			}
		}
		
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);  //loads the mediaplayer to the media object 
		
		
		songLabel.setText(songs.get(songNumber).getName()); //This gets the name of the current song playing
		
		for(int i = 0; i < speeds.length; i++) {
			
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");  //This access the speed array list
		}
		
		speedBox.setOnAction(this::changeSpeed); //references the change speed method
		
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);	//This activates the volume up and down
			}
		});
		
		songProgressBar.setStyle("-fx-accent: #00FF00;");
	}
	
	
    //This function play the song when you click on the button
	public void playMedia() {
		
		beginTimer(); //The timer begins for the progress Bar
		changeSpeed(null); //This function retains the same speed on different songs
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01); 
		mediaPlayer.play(); //The music plays
	}
	
	
    //This function pauses the song when you click on the button
	public void pauseMedia() {
		
		cancelTimer();
		mediaPlayer.pause();
	}
	
	//This stops the music
	public void stopMedia() {
		cancelTimer();
		mediaPlayer.stop();
	}
	
    /* This function restarts the song when you click on the button
	public void resetMedia() {
		
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	} */
	
	
    //This function rewinds the song when you click on the button
	public void rewindMedia(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-5)));
	}
	
	
    //This function fastforward the song when you click on the button
	public void forwardMedia(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
	}
	
	
    //This function Prev the song when you click on the button
    public void previousMedia() {
		
		if(songNumber > 0) {
			
			songNumber--;   //If the current song is greater than 1, it substracts and go back to the previous song
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());  //It finds the previous song
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia(); //calls the play function and automatically plays
		}
		else {
			
			songNumber = songs.size() - 1;   //Lets you cycle backward through the playlist
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
	}
    
    
    
    //This function skips the song to the next one when you click on the button
	public void nextMedia() {
		
		
		//check the current song number
		if(songNumber < songs.size() - 1) {
			
			songNumber++;
			
			
			//stops the song currently playing
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia(); //calls the play function (Automatically) and plays the next song until the last song
		}
		else {
			
			
			//This restarts the playlist once you get to the end of the song and it lets you cycle through it
			songNumber = 0;
			
			mediaPlayer.stop();
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
	}
	
	
    //This function reference the method and changes the speed of the song when you click on the button
     public void changeSpeed(ActionEvent event) {
		
		if(speedBox.getValue() == null) {
			
			mediaPlayer.setRate(1);
		}
		else {
			
			//mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
		}
	}
	
	
	
    //This function keeps track of where the song is currently is on the progress Bar
	public void beginTimer() {
		
		timer = new Timer();
		
		task = new TimerTask() {
			
			public void run() {
				
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				songProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					
					cancelTimer();
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	
	public void cancelTimer() {
		
		running = false;
		timer.cancel();
	}
}