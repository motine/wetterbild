package de.motine.wetterbild;

import java.net.*;
import java.io.*;
import org.json.*;
import android.widget.*;
import android.os.AsyncTask;
import android.util.Log;

import de.motine.wetterbild.WeatherDisplay;
import de.motine.wetterbild.Custom;

// http://api.openweathermap.org/data/2.5/weather?zip=10439,de&units=metric&appid=XXX
// http://api.openweathermap.org/data/2.5/weather?q=Wirges,de&units=metric&appid=XXX

// more info: http://openweathermap.org/current#current_JSON
// {
//     "id": 2855598, "base": "cmc stations", "cod": 200,
//     "clouds": { "all": 0 },
//     "coord": { "lat": 52.57, "lon": 13.4 },
//     "dt": 1450969070,
//     "main": {
//         "humidity": 76,
//         "pressure": 1022,
//         "temp": 8.21,
//         "temp_max": 10,
//         "temp_min": 6.11 },
//     "name": "Berlin Pankow",
//     "sys": { "country": "DE", "id": 4892, "message": 0.0035, "sunrise": 1450941397, "sunset": 1450968939, "type": 1},
//     "weather": [{ "description": "Sky is Clear", "icon": "01n", "id": 800, "main": "Clear"}],
//     "wind": { "deg": 180, "speed": 3.1}}

/**
 * Retrieves the weather from openweathermap.org and sets the WeatherDisplay accordingly.
 */
public class WeatherSource {
  private class DownloadWeatherTask extends AsyncTask<Void, Void, String> {
    protected String doInBackground(Void... nothing) {
      // inspired by: http://stackoverflow.com/a/1485730/4007237
      try {
        StringBuilder result = new StringBuilder();
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + Custom.OPEN_WEATHER_CITY + "&units=metric&appid=" + Custom.OPEN_WEATHER_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
           result.append(line);
        }
        rd.close();
        return result.toString();
      } catch (Exception e) {
        Log.w("wetterbild", e.toString());
        return null;
      }
    }

    protected void onPostExecute(String result) {
      if (result == null) {
        setUnknown();
      } else {
        setFromJSON(result);
      }
    }
  }
  
  private WeatherDisplay display;
  
  public WeatherSource(WeatherDisplay display) {
    this.display = display;
  }
  
  public void update() {
    new DownloadWeatherTask().execute();
    setUnknown();
  }

  public void setFromJSON(String jsonString) {
    try {
      JSONObject jRoot = new JSONObject(jsonString);
      JSONObject jMain = jRoot.getJSONObject("main");
      
      // temperature
      int temp = (int)jMain.getDouble("temp");
      display.setTemperature(temp);
      // clouds
      JSONObject jClouds = jRoot.getJSONObject("clouds");
      int cloudPercent = jClouds.getInt("all");
      if (cloudPercent <= 25) {
        display.setSun(WeatherDisplay.Sun.FULL);
      } else if (cloudPercent <= 75) {
        display.setSun(WeatherDisplay.Sun.HALF);
      } else {
        display.setSun(WeatherDisplay.Sun.NONE);
      }
      // rain
      int rain = 0; // also see below at correctedTemp
      if (jRoot.isNull("rain") || jRoot.getJSONObject("rain").isNull("3h")) {
        display.setRain(0); // no rain contained in the result
      } else {
        rain = jRoot.getJSONObject("rain").getInt("3h");
        if (rain <= 0) {
          display.setRain(0);
        } else if (rain < 5) {
          display.setRain(1);
        } else if (rain < 10) {
          display.setRain(2);
        } else {
          display.setRain(3);
        }
      }
      // wind
      double wind = jRoot.getJSONObject("wind").getDouble("speed");
      if (wind < 1.5) { // we ignore little less than 1 bft
        display.setWind(0);
      } else if (wind < 3.3) { // less than 2 bft
        display.setWind(1);
      } else if (wind < 7.9) { // less than 4 bft
        display.setWind(2);
      } else { // above 4 bft
        display.setWind(3);
      }

      int correctedTemp = temp + (rain > 5 ? -2 : 0);
      boolean jacket = false;
      WeatherDisplay.Undershirt undershirt = WeatherDisplay.Undershirt.NONE; //{ NONE, SHORT, LONG }
      WeatherDisplay.Pants pants = WeatherDisplay.Pants.SHORT; // { SHORT, LONG, UNDER }
      
      if (correctedTemp < 16) {
        undershirt = WeatherDisplay.Undershirt.SHORT;
      }
      if (correctedTemp < 12) {
        jacket = true;
      }
      if (correctedTemp < 8) {
        pants = WeatherDisplay.Pants.LONG;
      }
      if (correctedTemp < 6) {
        pants = WeatherDisplay.Pants.UNDER;
      }
      if (correctedTemp < 5) {
        undershirt = WeatherDisplay.Undershirt.LONG;
      }
      display.setJacket(jacket);
      display.setUndershirt(undershirt);
      display.setPants(pants);
    } catch (Exception e) {
      Log.w("wetterbild", e.toString());
      setUnknown();
    }
  }

  public void setUnknown() {
    display.setTemperature(-99);
    display.setWind(0);
    display.setRain(0);
    display.setJacket(false);
    display.setSun(WeatherDisplay.Sun.UNKNOWN);
    display.setUndershirt(WeatherDisplay.Undershirt.NONE);
    display.setPants(WeatherDisplay.Pants.SHORT);
  }
}