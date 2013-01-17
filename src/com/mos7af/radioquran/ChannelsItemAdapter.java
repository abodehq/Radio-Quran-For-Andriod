package com.mos7af.radioquran;

import java.util.ArrayList;
import java.util.HashMap;



import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelsItemAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public ChannelsItemAdapter(Activity a, ArrayList<HashMap<String, String>> _source) {
        activity = a;
        data=_source;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.ly_channels_item, null);

        TextView reciterNameAr = (TextView)vi.findViewById(R.id.title); 
        TextView reciterNameEn = (TextView)vi.findViewById(R.id.artist); 
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
        
        HashMap<String, String> sura = new HashMap<String, String>();
        sura = data.get(position);
        String fontPath = "fonts/arabic.ttf";
        
	     Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
	     reciterNameAr.setTypeface(tf);
        reciterNameAr.setText(sura.get("channelTitleAr"));
        reciterNameEn.setText(sura.get("channelTitleEn"));
       imageLoader.DisplayImage(sura.get("channelImage"), thumb_image);
        return vi;
    }
}