package de.motine.wetterbild;

import java.lang.*;
import java.util.*;

import android.os.*;
import android.util.Log;

public class PeriodicExecuter {
  
  private Runnable toDo;
  private long delay;

  private Handler handler;
  private Runnable runnable;

  public PeriodicExecuter(Runnable aToDo, long aDelaySec) {
    this.toDo = aToDo;
    this.delay = aDelaySec;

    handler = new Handler();
    runnable = new Runnable() {
      @Override
      public void run() {
        toDo.run(); 
        handler.postDelayed(runnable, delay * 1000);
      }
    };
    handler.postDelayed(runnable, 100);
  }
}
