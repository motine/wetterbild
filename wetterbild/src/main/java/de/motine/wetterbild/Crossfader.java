package de.motine.wetterbild;

import android.widget.*;
import android.view.animation.*;
import android.graphics.drawable.Drawable;
/**
 * Takes two image views and cross fades between them.
 * The target is the one which shows the current image.
 * targetOver is used to fade in the new image.
 * When the cross fade is done, the new image is set to target.
 */
public class Crossfader {
  protected ImageView target;
  protected ImageView targetOver;
  protected Drawable nextDrawable; // cache for which image shall be shown after the fade out
  protected ImageView.ScaleType nextScaleType;
  // change animation
  private AlphaAnimation overFadeIn;
  private AlphaAnimation fadeOut;

  public Crossfader(ImageView aTarget, ImageView aTargetOver) {
    this.target = aTarget;
    this.targetOver = aTargetOver;
    
    fadeOut = new AlphaAnimation(1f, 0f);
    fadeOut.setDuration(500);
    fadeOut.setFillAfter(false);

    overFadeIn = new AlphaAnimation(0f, 1f);
    overFadeIn.setDuration(500);
    overFadeIn.setFillAfter(true);

    overFadeIn.setAnimationListener(new Animation.AnimationListener() {
      public void onAnimationStart(Animation animation) {
        targetOver.setImageDrawable(nextDrawable);
        if (nextScaleType != null) {
          targetOver.setScaleType(nextScaleType);
        }
      }
      public void onAnimationRepeat(Animation animation) {}
      public void onAnimationEnd(Animation animation) {
        target.setImageDrawable(nextDrawable);
        if (nextScaleType != null) {
          target.setScaleType(nextScaleType);
        }
      }
    });    
  }
  // if scaleType is null, it will be left unchanged
  public void fadeTo(Drawable drawable, ImageView.ScaleType scaleType) {
    this.nextDrawable = drawable;
    this.nextScaleType = scaleType;
    // setup animation objects
    targetOver.startAnimation(overFadeIn);
    target.startAnimation(fadeOut);
  }
}