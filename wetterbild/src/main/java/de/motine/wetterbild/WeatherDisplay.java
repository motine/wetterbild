package de.motine.wetterbild;

import android.widget.*;

/**
 * Handles a bunch of image views to display the weather information.
 */
public class WeatherDisplay {
  
  public enum Sun { FULL, HALF, NONE, UNKNOWN }
  public enum Undershirt { NONE, SHORT, LONG }
  public enum Pants { SHORT, LONG, UNDER }
  
  private TextView  temp;
  private ImageView wind;
  private ImageView rain;
  private ImageView sun1;
  private ImageView sun2;
  private ImageView sun3;
  private ImageView cl_j; // jacket
  private ImageView cl_s; // shirt
  private ImageView cl_p; // pants

  public WeatherDisplay(TextView temp, ImageView wind, ImageView rain,
                        ImageView sun1, ImageView sun2, ImageView sun3,
                        ImageView cl_j, ImageView cl_s, ImageView cl_p) {
    this.temp = temp;
    this.wind = wind;
    this.rain = rain;
    this.sun1 = sun1;
    this.sun2 = sun2;
    this.sun3 = sun3;
    this.cl_j = cl_j;
    this.cl_s = cl_s;
    this.cl_p = cl_p;
  }
  
  public void setTemperature(int value) {
    temp.setText(Integer.toString(value) + "º");
  }
  // value between 0 to 3
  public void setWind(int value) {
    wind.setImageDrawable(null);
    switch (value) {
      case 0: wind.setImageResource(R.drawable.tow_0); break;
      case 1: wind.setImageResource(R.drawable.tow_1); break;
      case 2: wind.setImageResource(R.drawable.tow_2); break;
      case 3: wind.setImageResource(R.drawable.tow_3); break;
      default: throw new RuntimeException("Unexpected wind value.");
    }
  }
  
  // value between 0 to 3
  public void setRain(int value) {
    rain.setImageDrawable(null);
    switch (value) {
      case 0: rain.setImageResource(R.drawable.tor_0); break;
      case 1: rain.setImageResource(R.drawable.tor_1); break;
      case 2: rain.setImageResource(R.drawable.tor_2); break;
      case 3: rain.setImageResource(R.drawable.tor_3); break;
      default: throw new RuntimeException("Unexpected rain value.");
    }
  }
  
  public void setJacket(boolean value) {
    cl_j.setImageDrawable(null);
    if (value) {
      cl_j.setImageResource(R.drawable.clj_on);
    } else {
      cl_j.setImageResource(R.drawable.clj_off);
    }
  }
  
  public void setSun(Sun value) {
    sun1.setImageDrawable(null);
    sun2.setImageDrawable(null);
    sun3.setImageDrawable(null);
    switch (value) {
      case FULL:
        sun1.setImageResource(R.drawable.sun_1on);
        sun2.setImageResource(R.drawable.sun_2off);
        sun3.setImageResource(R.drawable.sun_3off);
        break;
      case HALF:
        sun1.setImageResource(R.drawable.sun_1off);
        sun2.setImageResource(R.drawable.sun_2on);
        sun3.setImageResource(R.drawable.sun_3off);
        break;
      case NONE:
        sun1.setImageResource(R.drawable.sun_1off);
        sun2.setImageResource(R.drawable.sun_2off);
        sun3.setImageResource(R.drawable.sun_3on);
        break;
      case UNKNOWN:
        sun1.setImageResource(R.drawable.sun_1off);
        sun2.setImageResource(R.drawable.sun_2off);
        sun3.setImageResource(R.drawable.sun_3off);
        break;
      default: throw new RuntimeException("Unexpected sun value.");
    }
  }

  public void setUndershirt(Undershirt value) {
    cl_s.setImageDrawable(null);
    switch (value) {
      case LONG: cl_s.setImageResource(R.drawable.cls_long); break;
      case SHORT: cl_s.setImageResource(R.drawable.cls_short); break;
      case NONE: cl_s.setImageResource(R.drawable.cls_off); break;
      default: throw new RuntimeException("Unexpected undershirt value.");
    }
  }

  public void setPants(Pants value) {
    cl_p.setImageDrawable(null);
    switch (value) {
      case UNDER: cl_p.setImageResource(R.drawable.clp_under); break;
      case LONG: cl_p.setImageResource(R.drawable.clp_long); break;
      case SHORT: cl_p.setImageResource(R.drawable.clp_off); break;
      default: throw new RuntimeException("Unexpected pants value.");
    }
  }
  
}