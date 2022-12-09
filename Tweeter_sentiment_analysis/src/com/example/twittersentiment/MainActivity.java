package com.example.twittersentiment;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.simple.parser.JSONParser;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;


import android.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listitem);
		ArrayList tweets = getTweets("android", 1);

		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(new TweetItemAdapter(this, R.layout.listitem, tweets));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class Tweet 
	{
		public String username;
		public String message;
		public String image_url;

		public Tweet(String username, String message, String url) 
		{
			this.username = username;
			this.message = message;
			this.image_url = url;
		}
	}
	
	public ArrayList<Tweet> getTweets(String searchTerm, int page) 
	{
		String searchUrl =
		"http://search.twitter.com/search.json?q=@"
		+ searchTerm + "&rpp=100&page=" + page;
		ArrayList tweets =
		new ArrayList();

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(searchUrl);

		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		String responseBody = null;
		try {
		responseBody = client.execute(get, responseHandler);
		} catch(Exception ex) {
		ex.printStackTrace();
		}

		JSONObject jsonObject = null;
		JSONParser parser=new JSONParser();

		try {
		Object obj = parser.parse(responseBody);
		jsonObject=(JSONObject)obj;
		}catch(Exception ex){
		Log.v("TEST","Exception: " + ex.getMessage());
		}

		JSONArray arr = null;

		try {
		Object j = jsonObject.get("results");
		arr = (JSONArray)j;
		} catch(Exception ex){
		Log.v("TEST","Exception: " + ex.getMessage());
		}

		for(Object t : arr) 
		{
			Tweet tweet = new Tweet
			(
				((JSONObject)t).get("from_user").toString(),
				((JSONObject)t).get("text").toString(),
				((JSONObject)t).get("profile_image_url").toString()
			);
			tweets.add(tweet);
		}

		return tweets;
	}
	
	public Bitmap getBitmap(String bitmapUrl) {
		try 
		{
			URL url = new URL(bitmapUrl);
			return BitmapFactory.decodeStream(url.openConnection().getInputStream());
		}
			catch(Exception ex) {return null;
		}
	}
	
	public class TweetItemAdapter extends ArrayAdapter 
	{
		private ArrayList tweets;
		public TweetItemAdapter(Context context, int textViewResourceId, ArrayList tweets) 
		{
			super(context, textViewResourceId, tweets);
			this.tweets = tweets;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View v = convertView;
			if (v == null) 
			{
				LayoutInflater vi = (LayoutInflater)getSystemService (Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.listitem, null);
			}

		Tweet tweet = (Tweet) tweets.get(position);

		if (tweet != null)
		{
			TextView username = (TextView) v.findViewById(R.id.username);
			TextView message = (TextView) v.findViewById(R.id.message);
			ImageView image = (ImageView) v.findViewById(R.id.avatar);

			if (username != null) 
			{
				username.setText(tweet.username);
			}
	
			if(message != null) 
			{
				message.setText(tweet.message);
			}
	
			if(image != null) 
			{
				image.setImageBitmap(getBitmap(tweet.image_url));
			}
		}

		return v;
		}
	}
	
}
