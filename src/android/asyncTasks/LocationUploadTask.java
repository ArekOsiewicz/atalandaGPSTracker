package com.atalanda.gpstracker.asyncTasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.atalanda.gpstracker.LocationCache;

import android.app.ApplicationErrorReport.BatteryInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;
import android.util.Pair;

public class LocationUploadTask extends AsyncTask<LocationCache, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(LocationCache... cacheEntries) {
		Log.d("Atalanda", "uploading "+cacheEntries.length+" locations");
		for (int i = 0; i < cacheEntries.length; i++) {
			LocationCache locationCache = cacheEntries[i];
			Location location = locationCache.getLocation();
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(locationCache.getUrl());
		    httppost.setHeader("Content-type", "application/json");

		    JSONObject body;
			try {
					body = new JSONObject(locationCache.getParameters());
			    body.put("verticalAccuracy", null);
			    body.put("horizontalAccuracy", location.getAccuracy());
			    body.put("heading", Float.toString(location.getBearing()));
					body.put("latitude", Double.toString(location.getLatitude()));
					body.put("longitude", Double.toString(location.getLongitude()));
			    body.put("velocity", Float.toString(location.getSpeed()));
			    body.put("batteryLevel", locationCache.getBatteryLevel());
			    body.put("timestamp", locationCache.getTimestamp());
			    httppost.setEntity(new StringEntity(body.toString()));
			} catch (JSONException | UnsupportedEncodingException e) {
				Log.e("error encoding JSON", e.getMessage());
				continue;
			}

		    Log.d("uploading position", "to "+locationCache.getUrl());

		    try {
		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        Log.d("upload response code", response.getStatusLine().toString());
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    	Log.e("upload failed", e.getLocalizedMessage());
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	Log.e("upload failed", e.getLocalizedMessage());
		    }

		}

		return true;
	}
}
