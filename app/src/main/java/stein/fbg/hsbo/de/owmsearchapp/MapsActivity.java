package stein.fbg.hsbo.de.owmsearchapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        EditText editText = (EditText) findViewById(R.id.search);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    requestWeather(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void requestWeather(String city) {
        String url = "http://geoapi-kswe2016.rhcloud.com/api/germany/" + city;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast toast = Toast.makeText(getApplicationContext(), "Response is: " + response.toString(), Toast.LENGTH_LONG);
                        //toast.show();
                        addMapWeatherSymbol(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    public void addMapWeatherSymbol(JSONObject jsonObject) {
        try {
            JSONObject locationObject = jsonObject.getJSONObject("location");
            JSONObject weatherObject = jsonObject.getJSONObject("weather");
            JSONObject temperatureObject = weatherObject.getJSONObject("temperature");
            final LatLng position = new LatLng(locationObject.getDouble("latitude"), locationObject.getDouble("longitude"));
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Weather in " + locationObject.getString("city") + ":")
                    .snippet("temperature: " + temperatureObject.getDouble("value") + ", description: " + weatherObject.getString("description"))
                    .icon(BitmapDescriptorFactory.fromResource(getWeatherIcon(weatherObject.getString("description")))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getWeatherIcon(String description) {
        int icon = 0;
        switch (description) {
            case "clear sky":
                icon = R.drawable.owm_01d;
                break;
            case "few clouds":
                icon = R.drawable.owm_02d;
                break;
            case "scattered clouds":
                icon = R.drawable.owm_03d;
                break;
            case "broken clouds":
                icon = R.drawable.owm_04d;
                break;
            case "shower rain":
                icon = R.drawable.owm_09d;
                break;
            case "rain":
                icon = R.drawable.owm_10d;
                break;
            case "thunderstorm":
                icon = R.drawable.owm_11d;
                break;
            case "snow":
                icon = R.drawable.owm_13d;
                break;
            case "mist":
                icon = R.drawable.owm_50d;
                break;
            default:
                icon = R.drawable.owm_01d;
        }
        return icon;
    }
}
