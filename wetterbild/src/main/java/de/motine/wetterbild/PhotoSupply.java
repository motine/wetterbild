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

  public String nextPhoto() {
    // evil to list the directory every time...
    // But if I don't somehow the InputMethodManagerService crashses with:
    //   W/InputMethodManagerService(  457): Session failed to close due to DeadObject
    //   W/InputMethodManagerService(  457): android.os.DeadObjectException
    //   W/InputMethodManagerService(  457):   at android.os.BinderProxy.transactNative(Native Method)
    //   W/InputMethodManagerService(  457):   at android.os.BinderProxy.transact(Binder.java:496)
    File root_path = new File(Custom.PHOTO_FOLDER);
    ArrayList<String> files = listJPGs(root_path);
    if (files.size() == 0) {
      return null;
    }
    return files.get(new Random().nextInt(files.size()));
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