package com.mos7af.radioquran;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
public class ChannelsActivity extends Activity {
		
	ListView list;
    ChannelsItemAdapter channelItemAdapter;
    private ChannelsActivity _scope;
    private GetTask getTask;
    private ArrayList<HashMap<String, String>> channelsList;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_channels);
        _scope = this;
        RelativeLayout relativeclic1 =(RelativeLayout)findViewById(R.id.footer);
        relativeclic1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	list.setAdapter(null);
            	getTask.cancel(true);
            	getTask = new GetTask();
                getTask.execute(); 
            }
        });
        
        getTask = new GetTask();
        getTask.execute(); 
    }
    
    private class GetTask extends AsyncTask<Void, Void, ReturnModel> {
        @Override
        protected ReturnModel doInBackground(Void... params) {
          return GetData();
        }

        @Override
        protected void onPostExecute(ReturnModel result) {

          
        	
        	//loading.setVisibility(View.INVISIBLE);
        	channelsList = result.getheadlines();
			list= (ListView)findViewById(R.id.list);
	  		
			 channelItemAdapter = new ChannelsItemAdapter(_scope, channelsList);    
	        
	         list.setAdapter(channelItemAdapter);
	         
	         list.setOnItemClickListener(new OnItemClickListener() {
	        	  @Override
	        	  public void onItemClick(AdapterView<?> parent, View view,
	        	    int position, long id) 
	        	  {
					// Starting new intent
					Intent in = new Intent(getApplicationContext(),
							RadioQuran.class);
				    ChannelslistManager.channelId =  channelsList.get(position).get("channelId");
				    ChannelslistManager suraslistManager=ChannelslistManager.getInstance();
					suraslistManager.deletAllchannels();
					suraslistManager.SetSongs(channelsList);
				    RadioQuran.tabIndex =1;
					RadioQuran mP3Quran= (RadioQuran)getParent();
					mP3Quran.loadMediaPlayer(position);
	        	  }
	        	});

          
        }
      }
    
    
    
    private ReturnModel GetData()
    {
    	
    	
    //	loading.setVisibility(View.VISIBLE);
    	channelsList = new ArrayList<HashMap<String, String>>();

  		
  		AMFConnection amfConnection= new AMFConnection();
  		try {   
  	        amfConnection.connect("http://mos7af.com/HolyQuranApi/index.php/amf/gateway");
  		} catch (ClientStatusException cse) {
  		        System.out.println("Error while connecting");
  		       // return false;
  		}
  		                
  		try {
  		        Object result = amfConnection.call("RadioServices.getAllRadioChannels");
  		        System.out.println(result);
  		        try {
  		            JSONArray jsonArray = new JSONArray(result.toString()) ;
  		            System.out.println("Number of entries " + jsonArray.length());
  		            for (int i = 0; i < jsonArray.length(); i++) {
  		              JSONObject jsonObject = jsonArray.getJSONObject(i);
  		
  			  			HashMap<String, String> channel = new HashMap<String, String>();
	  			  		channel.put("channelId", jsonObject.getString("channelId"));
	  			  		channel.put("channelTitleAr", jsonObject.getString("channelTitleAr"));
	  			  		channel.put("channelTitleEn", jsonObject.getString("channelTitleEn"));
	  			  		channel.put("channelStreamPath", jsonObject.getString("channelStreamPath"));
	  			  	
	  			  		channel.put("channelImage","http://s3.amazonaws.com/quranfiles/Images/Radio/"+ jsonObject.getString("channelId")+".jpg");
	  			  		channelsList.add(channel);
  		            }
  		          } catch (Exception e) {
  		            e.printStackTrace();
  		          }
  		        
  		        
  		        
  		        
  		        
  		     
  		} catch (Exception e) {
  		        System.out.println("Error while calling remote method");
  		       // return false;
  		}

  		ReturnModel returnModel = new ReturnModel();
  		returnModel.setheadlines(channelsList);
         return returnModel;
    }
    private class ReturnModel {
        private ArrayList<HashMap<String, String>> channelsList ;
     

        public ArrayList<HashMap<String, String>> getheadlines() {
          return channelsList;
        }

        public void setheadlines(ArrayList<HashMap<String, String>> _songsList) {
          this.channelsList = _songsList;
        }

        
      }
    
    
    
    
}
