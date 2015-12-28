package de.motine.wetterbild;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.renderscript.*;

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