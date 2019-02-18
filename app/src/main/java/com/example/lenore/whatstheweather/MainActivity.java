package com.example.lenore.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText city;
    //Button button;
    TextView weather;
    String weatherString="";
    String cityName;

    public void buttonClicked(View view){
        try {
            cityName = city.getText().toString();
            WeatherInfoDownloadTask task = new WeatherInfoDownloadTask();

            String encodedCity = URLEncoder.encode(cityName, "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=b6907d289e10d714a6e88b30761fae22");


            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
            weather.setText("Could not find weather :( "+cityName);

        }

    }

    public class WeatherInfoDownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String...urls){
            String result = "";
            URL url ;
            HttpURLConnection urlConnection = null;
            int data;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                data = reader.read();
                while(data!=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return "failed";
            }

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray array = new JSONArray(weatherInfo);
                for(int i = 0; i<array.length();++i){
                    JSONObject jsonPart = array.getJSONObject(i);

                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                    weatherString = jsonPart.getString("main") + ": "+jsonPart.getString("description")+"\r\n";
                }
                if(!weather.equals("")){
                weather.setText(weatherString);}
                else{
                   // weather.setText("Could not find weather :( "+weatherString);
                }

            }catch (Exception e){
                e.printStackTrace();
                weather.setText("Could not find weather :( "+cityName);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = findViewById(R.id.city);
        //button = findViewById(R.id.button);
        weather = findViewById(R.id.weather);


    }
}
