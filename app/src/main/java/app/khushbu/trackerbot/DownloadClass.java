package app.khushbu.trackerbot;

import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DownloadClass {

    String url;
    private int flag;
    int siteKey;

    DownloadClass(){

    }

    public void setUrl(String url){
        this.url=url;
    }
    public void formUrl(int frag){

        int id=siteKey;
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
            Upcoming.upcomingContestData.clear();
        }
        else {
            Ongoing.ongoingContestData.clear();
        }
    }
    public void downloadTask(RequestQueue requestQueue, final int frag, final int siteKey){

        disableSSLCertificateChecking();
        //to clear the screen
        clear(frag);
        this.siteKey=siteKey;
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

                        String curTime=time.getCurrentTimeStamp("UTC");

                        if(frag==2) {
                            if (curTime.compareTo(eventStartTime) < 0) {
                                if (curTime.compareTo(eventEndTime) < 0) {

                                    Upcoming.upcomingContestData.add(new ContestData(eventName,
                                            eventUrl,eventStartTime,eventEndTime,
                                            eventDuration,(int)MainActivity.imgId.get(siteKey)));

                                }
                            }
                            else
                                break;
                        }
                        else if(frag == 1){
                            if (curTime.compareTo(eventEndTime) < 0) {
                                if (curTime.compareTo(eventStartTime) > 0) {
                                    Ongoing.ongoingContestData.add(new ContestData(eventName,
                                            eventUrl,eventStartTime,eventEndTime,
                                            eventDuration,(int)MainActivity.imgId.get(siteKey)));
                                }
                            }
                            else
                                break;
                        }
                    }
                    reverse(frag);
                    try {
                        if (frag == 2) {
                            Upcoming.adapter.notifyDataSetChanged();

                            Upcoming.progressBar.setVisibility(View.GONE);

                            if(Upcoming.upcomingContestData.size() <= 0){
                                Upcoming.noContestTextView.setVisibility(View.VISIBLE);
                            }
                            else{
                                Upcoming.noContestTextView.setVisibility(View.GONE);
                            }
                        }
                        else {

                            //Ongoing.adapter.notifyDataSetChanged();
                            Ongoing.progressBar.setVisibility(View.GONE);
                            if(Ongoing.ongoingContestData.size() <= 0)
                                Ongoing.noContestTextView.setVisibility(View.VISIBLE);
                            else
                                Ongoing.noContestTextView.setVisibility(View.GONE);

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

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
            Collections.reverse(Upcoming.upcomingContestData);

        }
        else {
            Collections.reverse(Ongoing.ongoingContestData);
        }
    }


    public void downloadContestTask(RequestQueue requestQueue){

        //to clear the screen
        disableSSLCertificateChecking();
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, this.url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray=response.getJSONArray("objects");

                    Time time=new Time();


                    for(int i=jsonArray.length()-1;i>=0;i--) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String eventName=jsonObject.getString("event");
                        String eventUrl=jsonObject.getString("href");
                        String eventStartTime=jsonObject.getString("start");
                        String eventEndTime=jsonObject.getString("end");
                        int eventDuration=jsonObject.getInt("duration");
                        JSONObject object= jsonObject.getJSONObject("resource");
                        int siteKey = object.getInt("id");
                        String siteName = object.getString("name");

                        siteName = siteName.split("\\.")[0];
                        siteName = siteName.substring(0,1).toUpperCase()+siteName.substring(1,siteName.length());

                        String curTime=time.getCurrentTimeStamp("UTC");

                        if (curTime.compareTo(eventEndTime) < 0) {
                            if(curTime.compareTo(eventStartTime) >= 0) {
                                if(siteKey != 4) {
                                    ContestData contestData = new ContestData();
                                    contestData.setEvent_names(eventName);
                                    contestData.setEvent_start_time(eventStartTime);
                                    contestData.setEvent_end_time(eventEndTime);
                                    contestData.setEvent_url(eventUrl);
                                    contestData.setEvent_duration(eventDuration);
                                    contestData.setSite_key(siteKey);
                                    contestData.setSite_name(siteName);
                                    //contestData.setImgId(Integer.parseInt(MainActivity.imgId.get(siteKey).toString()));

                                    ALL_CONTEST_Activity.allContestList.add(contestData);
                                }
                            }
                        }


                    }
                    if(flag == 3) {
                        try {
                            ALL_CONTEST_Activity.aAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        flag=0;
                    }


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
        //ALL_CONTEST_Activity.aAdapter.notifyDataSetChanged();

    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
