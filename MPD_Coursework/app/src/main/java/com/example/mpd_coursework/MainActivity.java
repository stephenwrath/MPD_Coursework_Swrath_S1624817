//Stephen Wrath S1624817
package com.example.mpd_coursework;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    private TextView rawDataDisplay;
    private String result;
    private Button currentButton;
    private Button plannedButton;
    private Button roadworkButton;
    private GoogleMap mMap;
    // Traffic Scotland URLs
    private String urlSourceRW = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlSourcePW = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlSourceCI = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment1 = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment1.getMapAsync(this);

        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        currentButton = (Button) findViewById(R.id.currentButton);
        currentButton.setOnClickListener(this);
        plannedButton = (Button) findViewById(R.id.plannedButton);
        plannedButton.setOnClickListener(this);
        roadworkButton = (Button) findViewById(R.id.roadworkButton);
        roadworkButton.setOnClickListener(this);

    }

    public void onMapReady(GoogleMap googlemap) {
        mMap = googlemap;
    }

    public void onClick(View aview) {

        if (aview != null) {
            int id = aview.getId();
            mMap.clear();

            if (id == R.id.currentButton) {
                String urlSource = urlSourceCI;
                startProgress(urlSource);
            } else if (id == R.id.plannedButton) {
                String urlSource = urlSourcePW;
                startProgress(urlSource);
            } else if (id == R.id.roadworkButton) {
                String urlSource = urlSourceRW;
                startProgress(urlSource);
            }

        }

    }

    public void startProgress(String urlSource) {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    }
        //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;
        private LinkedList<Roadwork> alist;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                result = in.readLine();
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !


            Roadwork roadwork = new Roadwork();
            String temp = null;
            alist = null;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // Found a start tag
                    if (eventType == XmlPullParser.START_TAG) {
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("channel")) {
                            alist = new LinkedList<Roadwork>();
                        } else if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.e("MyTag", "Item Start Tag found");
                            roadwork = new Roadwork();
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.e("MyTag", "item is " + roadwork.toString());
                            alist.add(roadwork);
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            // Now just get the associated text

                            // Do something with text
                            Log.e("MyTag", "Title is " + temp);
                            roadwork.setTitle(temp);
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            // Check which Tag we have
                            // Now just get the associated text

                            // Do something with text
                            roadwork.setDesc(temp);
                        }else if (xpp.getName().equalsIgnoreCase("point")) {
                        // Check which Tag we have
                        // Now just get the associated text

                        // Do something with text
                            //Double[] points = new Double[];
                            Log.e("MyTag", "point is " + temp);
                            roadwork.setPoint(temp);
                    }
                    } else if (eventType == XmlPullParser.TEXT) {
                            temp = xpp.getText();
                            temp = temp.replaceAll("<br />", " \n ");
                            System.out.println("Text " + temp);
                            Log.e("MyTag", "Text is " + temp);
                        }
                    // Get the next event
                    eventType = xpp.next();

                } // End of while

                //return alist;
            } catch (XmlPullParserException ae1) {
                Log.e("MyTag", "Parsing error" + ae1.toString());
            } catch (IOException ae1) {
                Log.e("MyTag", "IO error during parsing");
            }

            Log.e("MyTag", "End document");

            System.out.println("Size: " + alist.size());
            final Roadwork finalRoadwork = roadwork;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    String data = "";
                    String point = "";

                    String[] pointSplit = new String[2];


                    for(Roadwork roadwork:alist) {
                        roadwork.getTitle();
                        roadwork.getDesc();
                        roadwork.getPoint();
                        data += roadwork.getTitle() + " | " + roadwork.getDesc() + " \n  \n ";
                        point += roadwork.getPoint() + " ";
                        pointSplit = point.split(" ");
                    }
                    rawDataDisplay.setText(data);

                    int size = pointSplit.length;

                    Double[] latlng = new Double[size];
                    Double[] lat = new Double[size/2];
                    Double[] lng = new Double[size/2];
                    int latCount = 0;
                    int lngCount = 0;

                    for(int i=0;i<size; i++) {
                            latlng[i]= Double.parseDouble(pointSplit[i]);
                        }
                    for(int i=0;i<size;i++) {
                        if(latlng[i]<0) {
                            lng[lngCount] = latlng[i];
                            lngCount++;
                        }else if(latlng[i] >=0) {
                            lat[latCount] = latlng[i];
                            latCount++;
                        }
                    }
                    int size1 = lat.length;

                    System.out.println("Long: " + Arrays.toString(lng) + "Lat: " + Arrays.toString(lat));
                    System.out.println( Arrays.toString(latlng));
                    for (int i = 0; i < size1; i++) {
                        LatLng points = new LatLng(lat[i], lng[i]);
                        mMap.addMarker(new MarkerOptions().position(points));
                    }
                }

            });


    }
}


} // End of MainActivity