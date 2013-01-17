package com.mos7af.radioquran;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class RadioQuran extends TabActivity 
{
	
	public static int tabIndex = 0;
	private TabHost tabHost;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_radio_quran);
        if(Utils.isConnectingToInternet(RadioQuran.this))
        {
        	AddAppTabs();
        }else
        {
        	showAlertDialog(this, "No Internet Connection",
					"You don't have internet connection.", false);
        }
    }
    public void AddAppTabs()
    {
    	tabHost = getTabHost();
    	tabHost.setOnTabChangedListener(new OnTabChangeListener() 
    	{
			public void onTabChanged(String tabId)
			{
				
				int tabIndex = tabHost.getCurrentTab();
				if(tabIndex==2 && ChannelslistManager.channelId == null)
				{
					 tabHost.setCurrentTab(0);
					 Toast.makeText(RadioQuran.this,"You need to select a reciter!!!", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
        // Tab for reciters
        TabSpec channelsSpec = tabHost.newTabSpec("reciters");
        channelsSpec.setIndicator("Channels", getResources().getDrawable(R.drawable.icon_reciters_tab));
        Intent channelsIntent = new Intent(this, ChannelsActivity.class);
        channelsSpec.setContent(channelsIntent);
        
        // Tab for player
        TabSpec playerSpec = tabHost.newTabSpec("player");
        playerSpec.setIndicator("player", getResources().getDrawable(R.drawable.icon_player_tab));
        Intent  playerIntent = new Intent(this, PlayerActivity.class);
        playerSpec.setContent(playerIntent);
      
       
        
    
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(channelsSpec); // Adding reciters tab
        tabHost.addTab(playerSpec); // Adding player tab
   
        
        tabHost.setCurrentTab(tabIndex);
    }
    public void switchTab(int tab)
    {
        tabHost.setCurrentTab(tab);
    }
    public void loadSuras()
    {
    	tabHost.setCurrentTab(2);
    }
    public void loadMediaPlayer(int songIndex)
    {
    	PlayerActivity activity =(PlayerActivity) getLocalActivityManager().getActivity("player"); 
    	if(activity!=null)
    		activity.LoadNewSong(songIndex);
    	tabHost.setCurrentTab(1);
    }
    public void ShowErrorDialog()
    {
    	showAlertDialog(this, "No Internet Connection",
				"You don't have internet connection.", false);
    }
    private void showAlertDialog(Context context, String title, String message, Boolean status) 
    {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setCancelable(false);
		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);
		
		// Setting alert dialog icon
		alertDialog.setIcon( R.drawable.fail);
			
		 alertDialog.setButton("Try Again!!", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		    	  if(Utils.isConnectingToInternet(RadioQuran.this))
		          {
		          	AddAppTabs();
		          }else
		          {
		        	  ShowErrorDialog();
		          }
		 
		    } });
		alertDialog.show();
	}

  
}