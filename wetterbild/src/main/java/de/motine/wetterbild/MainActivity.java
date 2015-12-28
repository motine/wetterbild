package de.motine.wetterbild;

import java.util.*;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.text.format.DateFormat;

import de.motine.wetterbild.*;

public class MainActivity extends Activity {
  
  private final long CHANGE_DELAY = 7000; // ms between the photo changes
  // private final long CHANGE_DELAY = 650; // ms between the photo changes
  private final long WEATHER_UPDATE_FREQUENCY = 5*60; // sec between updates
  private final long FADE_DURATION = 300; // ms for the fade out / fade in respectively
  
  TextView timeView;
  ImageView photoView;
  // change photo timer
  private Handler shortUpdateHandler;
  private Runnable shortUpdateRunnable;
  private Handler longUpdateHandler;
  private Runnable longUpdateRunnable;
  private PhotoSupply photoSupply;
  private Crossfader imageFader;
  private Crossfader backgroundFader;
  private WeatherDisplay weatherDisplay;
  private WeatherSource weatherSource;
  private Effects effects;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    enableFullscreen();
    setContentView(R.layout.main);

    // get references
    imageFader = new Crossfader((ImageView)findViewById(R.id.photoview), (ImageView)findViewById(R.id.photoviewover));
    backgroundFader = new Crossfader((ImageView)findViewById(R.id.photobackground), (ImageView)findViewById(R.id.photobackgroundover));
    timeView = (TextView) findViewById(R.id.time);
    weatherDisplay = new WeatherDisplay(
      (TextView)findViewById(R.id.toc), (ImageView)findViewById(R.id.tow), (ImageView)findViewById(R.id.tor), 
      (ImageView)findViewById(R.id.sun1), (ImageView)findViewById(R.id.sun2), (ImageView)findViewById(R.id.sun3), 
      (ImageView)findViewById(R.id.clj), (ImageView)findViewById(R.id.cls), (ImageView)findViewById(R.id.clp));

    // custom classes
    effects = new Effects(this);
    photoSupply = new PhotoSupply(this);
    weatherSource = new WeatherSource(weatherDisplay);
    
    // update time and photo
    shortUpdateHandler = new Handler();
    shortUpdateRunnable = new Runnable() { @Override public void run() { updateTime(); changeToNextPhoto(); shortUpdateHandler.postDelayed(shortUpdateRunnable, CHANGE_DELAY); } };
    shortUpdateHandler.postDelayed(shortUpdateRunnable, 100);
    // shortUpdateHandler.removeCallbacks(shortUpdateRunnable);

    // update weather
    longUpdateHandler = new Handler();
    longUpdateRunnable = new Runnable() { @Override public void run() { updateBrightness(); weatherSource.update(); longUpdateHandler.postDelayed(longUpdateRunnable, WEATHER_UPDATE_FREQUENCY * 1000); } };
    longUpdateHandler.postDelayed(longUpdateRunnable, 100);
  }
    
  private void enableFullscreen() {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    // remove notification bar
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    // hide bottom bar
    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    decorView.setSystemUiVisibility(uiOptions);
    // avoid display sleep
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }
  
  private void updateTime() {
    timeView.setText(DateFormat.format("H:mm", new java.util.Date()));
  }
  
  private void updateBrightness() {
    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    float brightness = 1.0f;
    if ((hour <= 7) || (hour >= 22)) {
      brightness = 0f;
    }
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.screenBrightness = brightness;
    getWindow().setAttributes(lp);
  }
  
  private void changeToNextPhoto() {
    Drawable nextPhoto = photoSupply.nextPhoto();
    // gather fitting mode
    ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
    if (nextPhoto instanceof BitmapDrawable) {
      BitmapDrawable bitmap = (BitmapDrawable)nextPhoto;
      if (bitmap.getIntrinsicWidth() < bitmap.getIntrinsicHeight()) { // portrait
        scaleType = ImageView.ScaleType.CENTER_INSIDE;
      }
    }
    imageFader.fadeTo(nextPhoto, scaleType);
    
    Drawable blurred = effects.blur(((BitmapDrawable)nextPhoto).getBitmap()); // assumption: we have a bitmap drawable
    backgroundFader.fadeTo(blurred, null);
  }
}
