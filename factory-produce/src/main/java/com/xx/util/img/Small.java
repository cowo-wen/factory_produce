package com.xx.util.img;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

public class Small
{
  private static final BufferedImage resize(BufferedImage source, int targetW, int targetH)
  {
    int type = source.getType();
    BufferedImage target = null;
    double sx = targetW / source.getWidth();
    double sy = targetH / source.getHeight();

    if (type == 0)
    {
      ColorModel cm = source.getColorModel();
      WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
      boolean alphaPremultiplied = cm.isAlphaPremultiplied();
      target = new BufferedImage(cm, raster, alphaPremultiplied, null);
    }
    else {
      target = new BufferedImage(targetW, targetH, type);
    }Graphics2D g = target.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
    g.dispose();
    return target;
  }

  public static void resize(String fromFileStr, String saveToFileStr, int width, int height)
  {
    try
    {
      File fromFile;
      File saveFile;
      if (saveToFileStr == null)
      {
        saveFile = new File(fromFileStr);
        fromFile = File.createTempFile("img", ".jpg");
        InputStream is = new FileInputStream(saveFile);
        FileOutputStream fos = new FileOutputStream(fromFile);
        IOUtils.copy(is, fos);
        is.close();
        is = null;
        fos.close();
        fos = null;
      }
      else
      {
        fromFile = new File(fromFileStr);
        saveFile = new File(saveToFileStr);
      }

      String imgType = "JPEG";
      if (fromFileStr.toLowerCase().endsWith(".png"))
      {
        imgType = "PNG";
      }

      BufferedImage srcImage = ImageIO.read(fromFile);
      if ((width > 0) || (height > 0))
      {
        srcImage = resize(srcImage, width, height);
      }
      ImageIO.write(srcImage, imgType, saveFile);
      if (saveToFileStr == null) {
        fromFile.delete();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static final void resize(String fromFileStr, int width, int height)
  {
    try
    {
      resize(fromFileStr, null, width, height);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] argv)
  {
    try
    {
      resize("d:/a.jpg", "d:/a1.jpg", 50, 50);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}