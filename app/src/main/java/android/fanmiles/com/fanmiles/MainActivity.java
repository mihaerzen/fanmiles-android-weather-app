package android.fanmiles.com.fanmiles;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int DAYS_TO_SHOW = 6;

    private String OPEN_WEATHER_APP_ID = "";
    private SearchView mSearchView;
    private int searchPlateId;

    private TextView mActiveCityNameTextView;
    private TextView mCurrentTemperatureTextView;
    private TextView mHighTemperatureTextView;
    private TextView mLowTemperatureTextView;
    private TextView mWeatherDescriptionTextView;
    private ImageView mPerceptionIconImageView;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide action bar
        getSupportActionBar().hide();

        OPEN_WEATHER_APP_ID = getResources().getString(R.string.OPEN_WEATHER_APP_ID);

        mSearchView = (SearchView) findViewById(R.id.search);
        searchPlateId = getResources().getIdentifier("android:id/search_plate", null, null);

        mActiveCityNameTextView = (TextView) findViewById(R.id.active_city_name);
        mCurrentTemperatureTextView = (TextView) findViewById(R.id.temperature_current);
        mHighTemperatureTextView = (TextView) findViewById(R.id.temperature_high);
        mLowTemperatureTextView = (TextView) findViewById(R.id.temperature_low);
        mWeatherDescriptionTextView = (TextView) findViewById(R.id.weather_description);
        mPerceptionIconImageView = (ImageView) findViewById(R.id.perception_icon);

        new FetchWeatherForecastTask().execute("berlin");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new FetchWeatherForecastTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void showProgress() {
        View progressBar = mSearchView.findViewById(searchPlateId).findViewById(R.id.search_progress_bar);
        if (progressBar != null) {
            progressBar.animate().setDuration(200).alpha(1).start();
        } else {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.loading_icon, null);
            ((ViewGroup) mSearchView.findViewById(searchPlateId)).addView(v, 1);
        }
    }

    private void hideProgress() {
        View progressBar = mSearchView.findViewById(searchPlateId).findViewById(R.id.search_progress_bar);
        if (progressBar != null) {
            progressBar.animate().setDuration(200).alpha(0).start();
        }
    }

    private class FetchWeatherForecastTask extends AsyncTask<String, Void, ArrayList<WeatherDay>> {
        protected void onPreExecute() {
            showProgress();
        }

        protected ArrayList doInBackground(String... queries) {

            String query = queries[0];

            ArrayList<WeatherDay> days = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            String jsonResponseCurrent;
            try {
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" +
                        query + "&mode=json&units=metric&appid=" + OPEN_WEATHER_APP_ID;

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                jsonResponseCurrent = response.body().string();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return days;
            }

            String jsonResponseList;
            try {
                String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" +
                        query + "&mode=json&units=metric&cnt=" +
                        DAYS_TO_SHOW + "&appid=" + OPEN_WEATHER_APP_ID;

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                jsonResponseList = response.body().string();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return days;
            }

            try {
                JSONObject jsonObjectCurrent = new JSONObject(jsonResponseCurrent);
                JSONObject jsonObject = new JSONObject(jsonResponseList);

                String cityName = jsonObjectCurrent.getString("name");
                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EE");

                JSONArray daysList = jsonObject.getJSONArray("list");
                for (int i = 0; i < daysList.length(); i++) {
                    WeatherDay day = new WeatherDay();

                    JSONObject jsonDayObject = (JSONObject) daysList.get(i);

                    Date date = new Date(jsonDayObject.getLong("dt") * 1000L);
                    String dayOfTheWeek = dayOfWeekFormat.format(date);

                    JSONObject tempObject = jsonDayObject.getJSONObject("temp");
                    JSONObject weatherObject = jsonDayObject.getJSONArray("weather").getJSONObject(0);

                    day.setCityName(cityName);
                    day.setName(dayOfTheWeek);
                    day.setHighestTemperature(tempObject.getInt("max"));
                    day.setLowestTemperature(tempObject.getInt("min"));
                    day.setMain(weatherObject.getString("main"));
                    day.setDescription(weatherObject.getString("description"));

                    days.add(day);
                }

                // set current temperature
                WeatherDay currentDay = days.get(0);
                currentDay.setCurrentTemperature(jsonObjectCurrent.getJSONObject("main").getInt("temp"));

            } catch (JSONException e) {
                Log.e(TAG, e.toString());
                return days;
            }

            return days;
        }

        protected void onPostExecute(ArrayList<WeatherDay> days) {
            String degreeSign = "°";
            WeatherDay currentDay = days.get(0);

            mActiveCityNameTextView.setText(currentDay.getCityName());
            mCurrentTemperatureTextView.setText(currentDay.getCurrentTemperature() + degreeSign);
            mHighTemperatureTextView.setText(currentDay.getHighestTemperature() + degreeSign);
            mLowTemperatureTextView.setText(currentDay.getLowestTemperature() + degreeSign);
            mWeatherDescriptionTextView.setText(currentDay.getDescription());
            mPerceptionIconImageView.setImageResource(currentDay.getIcon());

            LinearLayout weekdaysLayout = (LinearLayout) findViewById(R.id.weekdays);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            weekdaysLayout.removeAllViews();

            int daysSize = days.size();

            for (int i = 1; i < daysSize && i <= DAYS_TO_SHOW; i++) {
                final WeatherDay day = days.get(i);

                View weatherDayItem = inflater.inflate(R.layout.weather_day_item, null);
                TextView weekdayName = (TextView) weatherDayItem.findViewById(R.id.weekday_name);
                TextView weekdayTemperatureLow = (TextView) weatherDayItem.findViewById(R.id.weekday_temperature_low);
                TextView weekdayTemperatureHigh = (TextView) weatherDayItem.findViewById(R.id.weekday_temperature_high);
                ImageView weekdayIcon = (ImageView) weatherDayItem.findViewById(R.id.weekday_icon);

                weekdayName.setText(day.getName());
                weekdayIcon.setImageResource(day.getIcon());
                weekdayTemperatureLow.setText(day.getLowestTemperature() + degreeSign);
                weekdayTemperatureHigh.setText(day.getHighestTemperature() + degreeSign);

                weatherDayItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayToast(day.getDescription());
                    }
                });

                weekdaysLayout.addView(weatherDayItem);
            }

            hideProgress();
        }

        Toast toastReference;

        private void displayToast(String text) {
            if (toastReference != null) {
                toastReference.cancel();
            }
            toastReference = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toastReference.show();
        }
    }
}
