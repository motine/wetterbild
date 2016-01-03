package de.motine.wetterbild;

import java.util.*;
import java.io.*;

import android.content.*;
import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.*;
import android.media.ExifInterface;
import android.util.Log;

import de.motine.wetterbild.Custom;

/**
 * Retrieves the image and prepares it for display
 */
public class PhotoSupply {
  
  private Context context;
  
  public PhotoSupply(Context context) {
    this.context = context;
  }
  
  private Drawable getScaledDrawable(String path) { // we need to scale because drawables have limited size
    Bitmap full = BitmapFactory.decodeFile(path);
    int width = full.getWidth();
    int height = full.getHeight();
    
    try {
      // rotate according to exif (inspired from: http://stackoverflow.com/a/11081918/4007237)
      ExifInterface exif = new ExifInterface(path);
      int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
      int rotationInDegrees = exifToDegrees(rotation);
      Matrix matrix = new Matrix();
      if (rotation != 0f) { matrix.preRotate(rotationInDegrees); }
      // swap width and height if we need to rotate
      Bitmap rotated = Bitmap.createBitmap(full, 0, 0, width, height, matrix, true);

      // scale (we only make sure it is 600 high)
      int newWidth = (int) (600 / (rotated.getHeight() / (double)rotated.getWidth()));
      Bitmap scaled = Bitmap.createScaledBitmap(rotated, newWidth, 600, true);
      return new BitmapDrawable(scaled);
    } catch (Exception e) {
      Log.e("wetterbild", "ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR");
      Log.e("wetterbild", e.toString());
    }
    return null;
  }

  public Drawable nextPhoto() {
    // evil to do this every time...
    File root_path = new File(Custom.PHOTO_FOLDER);
    ArrayList<String> files = listJPGs(root_path);

    if (files.size() == 0) {
      return context.getResources().getDrawable(R.drawable.ic_launcher);
    }

    // select and scale
    String selected = files.get(new Random().nextInt(files.size()));
    return getScaledDrawable(selected);
  }
  
  private static int exifToDegrees(int exifOrientation) {        
    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; } 
    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }            
    return 0;    
  }

  // recursively list all JPGs
  // inspired by http://stackoverflow.com/a/11658192/4007237
  public static ArrayList<String> listJPGs(File dir) {
    ArrayList<String> fileTree = new ArrayList<String>();
    for (File entry : dir.listFiles()) {
      if (entry.isFile()) {
        String lower = entry.getName().toLowerCase();
        if ((!lower.startsWith(".")) && (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) {
          fileTree.add(entry.getAbsolutePath());
        }
      } else {
        fileTree.addAll(listJPGs(entry));
      }
    }
    return fileTree;
  }
}