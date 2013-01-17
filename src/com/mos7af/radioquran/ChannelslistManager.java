package com.mos7af.radioquran;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class ChannelslistManager {
	public static String channelId = null;
	private static ChannelslistManager instance = null;
	private ArrayList<HashMap<String, String>> channelsList = new ArrayList<HashMap<String, String>>();

   public static ChannelslistManager getInstance() {
      if(instance == null) {
         instance = new ChannelslistManager();
      }
      return instance;
   }
	// Constructor
	public ChannelslistManager(){
		if(channelsList.isEmpty())
		{
			HashMap<String, String> channel = new HashMap<String, String>();			
			channel.put("channelId", "1");
			channel.put("channelTitleAr", "الأذاعة العامة للقران");
			channel.put("channelTitleEn", "Public Radio of the Quran");
			channel.put("channelStreamPath", "http://50.22.217.209:9998/;stream.nsv");
			channel.put("channelImage","http://s3.amazonaws.com/quranfiles/Images/Radio/1.jpg");
  			channelsList.add(channel);
		}
	}
	public ArrayList<HashMap<String, String>> getPlayList()
	{
		return channelsList;
	}
	public void AddNewchannel(HashMap<String, String> channel)
	{
		channelsList.add(channel);
	
	}
	public void AddNewchannelAt(int index,HashMap<String, String> channel)
	{
		channelsList.add(index,channel);
	
	}
	public void deletAllchannels()
	{
		channelsList.clear();
	}
	public void SetSongs(ArrayList<HashMap<String, String>> channels )
	{
		channelsList.clear();
		for(int i=0;i<channels.size();i++)
		{
			HashMap<String, String> channel = new HashMap<String, String>();			
			channel.put("channelId", channels.get(i).get("channelId"));
			channel.put("channelTitleAr", channels.get(i).get("channelTitleAr"));
			channel.put("channelTitleEn", channels.get(i).get("channelTitleEn"));
			channel.put("channelStreamPath", channels.get(i).get("channelStreamPath"));
			channel.put("channelImage",channels.get(i).get("channelImage"));
			channelsList.add(channel);
		}
	
	}
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
