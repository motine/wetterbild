package de.motine.wetterbild;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.renderscript.*;
import android.media.ExifInterface;
import android.util.Log;

/**
 * Provides a methods to apply renderscript effects to images
 */
public class Effects {
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 25.0f;
    private Context context;
    private RenderScript renderscript;
    
    public Effects(Context context) {
      this.context = context;
      this.renderscript = RenderScript.create(context);
    }


    // inspired by https://futurestud.io/blog/how-to-blur-images-efficiently-with-androids-renderscript
    public Drawable blur(Bitmap image) {
      int width = Math.round(image.getWidth() * BITMAP_SCALE);
      int height = Math.round(image.getHeight() * BITMAP_SCALE);

      Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
      Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

      ScriptIntrinsicBlur intrinisc = ScriptIntrinsicBlur.create(renderscript, Element.U8_4(renderscript));
      Allocation tmpIn = Allocation.createFromBitmap(renderscript, inputBitmap);
      Allocation tmpOut = Allocation.createFromBitmap(renderscript, outputBitmap);
      intrinisc.setRadius(BLUR_RADIUS);
      intrinisc.setInput(tmpIn);
      intrinisc.forEach(tmpOut);
      tmpOut.copyTo(outputBitmap);
      return new BitmapDrawable(context.getResources(), outputBitmap);
    }
    
    // Reads the image from path and returns a scaled drawable suitable for the fire display (height=600).
    // It also rotates the image according to the EXIF information.
    public Drawable readScaledDrawable(String path) { // we need to scale because drawables have limited size
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
    
    private static int exifToDegrees(int exifOrientation) {        
      if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
      else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; } 
      else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }            
      return 0;    
    }
    
    public void close() {
      renderscript.finish();
      renderscript.destroy();
    }
    
    @Override
    protected void finalize() throws Throwable {
      close();
      super.finalize();
    }
}