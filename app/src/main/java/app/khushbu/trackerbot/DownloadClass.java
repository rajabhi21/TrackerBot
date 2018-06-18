package app.khushbu.trackerbot;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class DownloadClass {

    String url;
    DownloadClass(){

    };
    public void formUrl(int frag){

        int id=ContestListActivity.siteKey;
        String url_left="https://clist.by/api/v1/json/contest/?username=abhi&api_key=8f62bf40d07bb9af09535a22f21653ace0da43a4&resource__id=";
        String resource_id="1";
        if(id != -1);
            resource_id=Integer.toString(id);
        String url_right="&order_by=-start";
        if(frag==1)
            url_right="&order_by=-end";
        String url=url_left+resource_id+url_right;
        this.url=url;

        //Log.i("url",this.url);
        //Log.i("url1",url);

    }
    public  void clear(int frag){
        if(frag==2) {
            Upcoming.event_names.clear();
            Upcoming.event_url.clear();
            Upcoming.event_start_time.clear();
            Upcoming.event_end_time.clear();
            Upcoming.event_duration.clear();
        }
        else {
            Ongoing.event_names.clear();
            Ongoing.event_url.clear();
            Ongoing.event_start_time.clear();
            Ongoing.event_end_time.clear();
            Ongoing.event_duration.clear();
        }
    }
    public void downloadTask(RequestQueue requestQueue, final int frag){

        //to clear the screen
        clear(frag);
        this.formUrl(frag);
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, this.url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray=response.getJSONArray("objects");

                    Time time=new Time();


                    for(int i=0;i<jsonArray.length();i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String eventName=jsonObject.getString("event");
                        String eventUrl=jsonObject.getString("href");
                        String eventStartTime=jsonObject.getString("start");
                        String eventEndTime=jsonObject.getString("end");
                        int eventDuration=jsonObject.getInt("duration");

                        String curTime=time.getCurrentTimeStamp();

                        if(frag==2) {
                            if (curTime.compareTo(eventEndTime) < 0) {
                                if (curTime.compareTo(eventStartTime) < 0) {
                                    //Log.i("names",eventName);
                                    Upcoming.event_names.add(eventName);
                                    Upcoming.event_url.add(eventUrl);
                                    Upcoming.event_start_time.add(eventStartTime);
                                    Upcoming.event_end_time.add(eventEndTime);
                                    Upcoming.event_duration.add(eventDuration);

                                }
                            }
                        }
                        else{
                            if (curTime.compareTo(eventStartTime) > 0) {

                                if (curTime.compareTo(eventEndTime) < 0) {

                                    Ongoing.event_names.add(eventName);
                                    Ongoing.event_url.add(eventUrl);
                                    Ongoing.event_start_time.add(eventStartTime);
                                    Ongoing.event_end_time.add(eventEndTime);
                                    Ongoing.event_duration.add(eventDuration);


                                }
                            }
                        }
                    }
                    reverse(frag);
                    if(frag==2)
                        Upcoming.adapter.notifyDataSetChanged();
                    else
                        Ongoing.adapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(objectRequest);
        //adapter.notifyDataSetChanged();

    }
    public void reverse(int frag){
        if(frag==2) {
            Collections.reverse(Upcoming.event_names);
            Collections.reverse(Upcoming.event_url);
            Collections.reverse(Upcoming.event_start_time);
            Collections.reverse(Upcoming.event_end_time);
            Collections.reverse(Upcoming.event_duration);
        }
        else {
            Collections.reverse(Ongoing.event_names);
            Collections.reverse(Ongoing.event_url);
            Collections.reverse(Ongoing.event_start_time);
            Collections.reverse(Ongoing.event_end_time);
            Collections.reverse(Ongoing.event_duration);
        }
    }
}
