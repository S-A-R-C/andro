package com.s_a_r_c.applicationprojecttest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.s_a_r_c.applicationprojecttest.Helpers.UserDatabase;
import com.s_a_r_c.applicationprojecttest.dummy.DummyContent;
import com.s_a_r_c.applicationprojecttest.dummy.FinalContent;
import com.s_a_r_c.applicationprojecttest.dummy.SongContent;

import java.util.ArrayList;
import java.util.HashMap;

public class visualizeSongsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_songs);
        setTitle("Listes des musiques");

        final ListView mListView = (ListView) findViewById(R.id.listView);
        ArrayList<String> playlists = new ArrayList<String>();
        final HashMap<Integer, FinalContent.SongItem> songsMaps = new HashMap<Integer, FinalContent.SongItem>();
        int count = 0;
        if(DummyContent.getStrSoloMusic().equals("true")&&!DummyContent.getId().equals("")&&false==true)
        {
            Log.e("OFFLINE","OFFLINE");
            for(FinalContent.SongItem songItem : FinalContent.SONGITEMSPRIMAL) {
                if(songItem.strOwnerID.equals(DummyContent.getId()))
                playlists.add(songItem.strTitre+" ;"+songItem.strId);
                songsMaps.put(count, songItem);
                count++;
            }
        }
        else
        {

            for(FinalContent.SongItem songItem : FinalContent.SONGITEMSPRIMAL) {
                playlists.add(songItem.strTitre+" ;"+songItem.strId);
                songsMaps.put(count, songItem);
                count++;
            }
        }

    DummyContent.setStrSoloMusic("");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,   playlists) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View rowView = LayoutInflater.from(getApplicationContext()).
                        inflate(R.layout.listview_custom, parent, false);

                TextView text1 = (TextView) rowView.findViewById(R.id.tvTitre);
                ImageView icon = (ImageView) rowView.findViewById(R.id.listviewVignette);

                text1.setText(songsMaps.get(position).strArtiste + " - " + songsMaps.get(position).strTitre);

                if (UserDatabase.getInstance(getApplicationContext()).loggedIn())
                    Log.e("LISTVIEW COLORS" , songsMaps.get(position).strId + "~" + (UserDatabase.getInstance(getApplicationContext()).retournerInfosUser().get(UserDatabase.USER_ID)));
                if (UserDatabase.getInstance(getApplicationContext()).loggedIn() && songsMaps.get(position).strOwnerID.equals((UserDatabase.getInstance(getApplicationContext()).retournerInfosUser().get(UserDatabase.USER_ID)))) {
                    text1.setTextColor(Color.parseColor("#00ff00"));
                }

                if(songsMaps.get(position).strActive.equals("false") && !(UserDatabase.getInstance(getApplicationContext()).loggedIn() && songsMaps.get(position).strOwnerID.equals((UserDatabase.getInstance(getApplicationContext()).retournerInfosUser().get(UserDatabase.USER_ID))))) {
                    text1.setText("Inactive" + " - Id : " + songsMaps.get(position).strId);
                } else {
                    byte[] decodedString = Base64.decode(songsMaps.get(position).strVignette, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    icon.setImageBitmap(decodedByte);
                    icon.requestLayout();
                }


                return rowView;
            }

            @Override
            public boolean isEnabled(int position){

                if(songsMaps.get(position).strActive.equals("false") && !(UserDatabase.getInstance(getApplicationContext()).loggedIn() && songsMaps.get(position).strOwnerID.equals((UserDatabase.getInstance(getApplicationContext()).retournerInfosUser().get(UserDatabase.USER_ID)))))
                    return false;
                else
                    return true;

            }
        };
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Item Selected","YEPEYEP"+mListView.getItemAtPosition(position).toString());

                String[] strSplit = mListView.getItemAtPosition(position).toString().split(";");
                DummyContent.setStrPlaylistSelected("");
                DummyContent.setStrSongSelected(strSplit[1]);
                if(!DummyContent.getId().equals("")&&DummyContent.getStrSeeMusic().equals("")) {
                    Intent intent = new Intent(getBaseContext(), modifySongActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getBaseContext(), songContent.class);
                    startActivity(intent);
                }
            }
        });
     }
}
