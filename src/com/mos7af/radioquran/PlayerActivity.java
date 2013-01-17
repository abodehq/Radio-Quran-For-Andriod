package com.mos7af.radioquran;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songReciterLabel;
	
	private TextView songCurrentDurationLabel;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private ChannelslistManager songManager;
	private Utilities utils;
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private boolean isPrepared =false;
	
	private boolean isActive = true;
	private ImageLoader imageLoader;
	 private void CreateMediaPlayer()
	 {
			// Mediaplayer
			mp = new MediaPlayer();
			mp.setOnCompletionListener(this); // Important
			mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			    @Override
			    public void onPrepared(MediaPlayer mp) {
			    	isPrepared = true;
			    }
			});
			mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if(!Utils.isConnectingToInternet(PlayerActivity.this))
		  	        {
		  				showAlertDialog(PlayerActivity.this, "No Internet Connection",
		  						"You don't have internet connection.", false);
		  	        }else
		  	        {
		  	        	showAlertDialog(PlayerActivity.this, "play Error!!",
		  						"An error has occurred attempting to play sura!!", false);
		  	        }
					return false;
				}
			});
	 }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_radio_player);
		
		imageLoader=new ImageLoader(getApplicationContext());
	
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songReciterLabel = (TextView) findViewById(R.id.songTitle1);
		
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		//songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		
	
		
		songManager = ChannelslistManager.getInstance();
		utils = new Utilities();
		
		// Listeners
		//songProgressBar.setOnSeekBarChangeListener(this); // Important
		songProgressBar.setClickable(false);
		CreateMediaPlayer();
	
		
		// Getting all songs list
		songsList = songManager.getPlayList();
		
		// By default play first song
		playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isActive)
				{
					
					// check for already playing
					if(mp.isPlaying()){
						if(mp!=null){
							mp.pause();
							// Changing button image to play button
							btnPlay.setImageResource(R.drawable.btn_play);
						}
					}else{
						// Resume song
						if(mp!=null){
							mp.start();
							// Changing button image to pause button
							btnPlay.setImageResource(R.drawable.btn_pause);
						}
					}
				}else
				{
					
					playSong(currentSongIndex);
				}
				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
	
		
		
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}
				
			}
		});
		
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
				
			}
		});
		
		
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayerPlayListActivity.class);
				startActivityForResult(i, 100);			
			}
		});
		
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 currentSongIndex = data.getExtras().getInt("songIndex");
         	 // play selected song
             playSong(currentSongIndex);
        }
 
    }
	
	public void LoadNewSong(int _songIndex)
	{
		playSong(_songIndex);
		currentSongIndex = _songIndex;
	}
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	ProgressDialog dialog;
	public void  playSong(int songIndex){
		
		isActive = true;
		songsList = songManager.getPlayList();
		dialog  = new ProgressDialog(PlayerActivity.this);
		dialog.setButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            		
            	dialog.dismiss();
            	getTask.cancel(true);
            	
            	mp.release();
            	CreateMediaPlayer();
            	isActive = false;
            }
        });
		dialog.setMessage("Buffering. Please wait...");
		 dialog.show();
		 
		
		isPrepared = false;
		
		
		 getTask = new GetTask();
	     getTask.execute();

		
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   try
			   {
				  if(isPrepared)
				  {
					 
				   	long totalDuration = mp.getDuration();
			   		long currentDuration = mp.getCurrentPosition();
			  
			   		// Displaying Total Duration time
			   		//songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   		// Displaying time completed playing
			   		songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   		// Updating progress bar
			   		int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   		//Log.d("Progress", ""+progress);
			   		songProgressBar.setProgress(progress);
			   
			   		// Running this thread after 100 milliseconds
			   		mHandler.postDelayed(this, 100);
				  }
				  
			   }catch(Exception e)
			   {
				   
			   }
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		
		
		
		mHandler.removeCallbacks(mUpdateTimeTask);
		if(isActive)
		{
			int totalDuration = mp.getDuration();
			int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
			
			// forward or backward to certain seconds
			mp.seekTo(currentPosition);
			
			// update timer progress again
			updateProgressBar();
		}
		else
		{
			
			playSong(currentSongIndex);
		}
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}

	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	 }
	
	
	
	ListView list;
    private GetTask getTask = null;
	 private class GetTask extends AsyncTask<Void, Void, ReturnModel> {
	        @Override
	        protected ReturnModel doInBackground(Void... params) {
	        
	          return GetData();
	        }

	        @Override
	        protected void onPostExecute(ReturnModel result) {

	          
	        	dialog.dismiss();
	        	if(result!=null)
	        	{
		        	mp.start();
					// Displaying Song title
					String songTitle = songsList.get(currentSongIndex).get("channelTitleAr");
		        	songTitleLabel.setText(songTitle);
					
		        	String songReciter = songsList.get(currentSongIndex).get("channelTitleEn") ;
		        	songReciterLabel.setText(songReciter);
		        	
		        	String imgPath = songsList.get(currentSongIndex).get("channelImage");
		        	ImageView thumb_image=(ImageView)findViewById(R.id.list_image);
		        	imageLoader.DisplayImage(imgPath, thumb_image);
		        	// Changing Button Image to pause image
					btnPlay.setImageResource(R.drawable.btn_pause);
					
					// set Progress bar values
					songProgressBar.setProgress(0);
					songProgressBar.setMax(100);
					
					// Updating progress bar
					updateProgressBar();
	        	}else
	        	{
	        		if(!Utils.isConnectingToInternet(PlayerActivity.this))
		  	        {
		  				showAlertDialog(PlayerActivity.this, "No Internet Connection",
		  						"You don't have internet connection.", false);
		  	        }else
		  	        {
		  	        	showAlertDialog(PlayerActivity.this, "play Error!!",
		  						"An error has occurred attempting to play sura!!", false);
		  	        }
		  			
		  			getTask.cancel(true);
	        	}
		          
	          
	        }
	      }
	    
	    
	    
	    private ReturnModel GetData()
	    {
	    	  
	  		                
	  		try {
	  		       
	  			String songPath = songsList.get(currentSongIndex).get("channelStreamPath");  
	  			//songPath = "http://38.96.148.75/SunnahAudio";
	  			mp.reset();
				mp.setDataSource(songPath);
				mp.prepare();   
	  		        
	  		        
	  		     
	  		} catch (Exception e) 
	  		{
	  			return null;
	  			 
	  		}

	  		ReturnModel returnModel = new ReturnModel();
	  		returnModel.setheadlines(songsList);
	         return returnModel;
	    }
	    
	    
	    private class ReturnModel {
	        private ArrayList<HashMap<String, String>> songsList ;
	     

	        public ArrayList<HashMap<String, String>> getheadlines() {
	          return songsList;
	        }

	        public void setheadlines(ArrayList<HashMap<String, String>> _songsList) {
	          this.songsList = _songsList;
	        }

	        
	      }
	    public void ShowErrorDialog()
	    {
	    	showAlertDialog(PlayerActivity.this, "No Internet Connection",
					"You don't have internet connection.", false);
	    }
	    private void showAlertDialog(Context context, String title, String message, Boolean status) 
	    {
	    	
	    	if (((Activity) context).isFinishing() == false) {
	    		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

				// Setting Dialog Title
				alertDialog.setTitle(title);

				// Setting Dialog Message
				alertDialog.setMessage(message);
				
				// Setting alert dialog icon
				alertDialog.setIcon( R.drawable.fail);
					
				 alertDialog.setButton("Try Again!!", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				 
				    	  if(Utils.isConnectingToInternet(PlayerActivity.this))
				          {
				    		  mp.release();
				            CreateMediaPlayer();
				    		 playSong(currentSongIndex);
				          	
				          }else
				          {
				        	  ShowErrorDialog();
				          }
				 
				    } });
				alertDialog.show();
	    	}
			
		}
	
}