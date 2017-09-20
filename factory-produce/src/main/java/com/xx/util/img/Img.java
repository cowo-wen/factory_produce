package com.xx.util.img;

import com.xx.util.checker.Checker;
import com.xx.util.string.Format;
import com.xx.util.string.Parser;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

public class Img
{
  private int srcWidth;
  private int srcHeight;
  private File file;

  public Img(File file)
  {
    try
    {
      this.file = file;
      Image image = ImageIO.read(file);
      this.srcWidth = image.getWidth(null);
      this.srcHeight = image.getHeight(null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public int getSrcHeight()
  {
    return this.srcHeight;
  }

  public int getSrcWidth()
  {
    return this.srcWidth;
  }

  public static boolean check(File file)
  {
    String type = Checker.toType(file);

    boolean isImg = Checker.checkImg(type);

    return isImg;
  }

  public static boolean check(InputStream is)
  {
    try
    {
      File file = File.createTempFile("tmp", ".jpg");
      FileOutputStream fos = new FileOutputStream(file);
      IOUtils.copy(is, fos);
      fos.close();
      fos = null;
      boolean isImg = check(file);
      file.delete();
      return isImg;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return false;
  }

  public int[] resize(int mWidth, int mHeight, boolean isMax)
  {
    double sx = mWidth / this.srcWidth;
    double sy = mHeight / this.srcHeight;
    if (isMax)
    {
      if (sx > sy)
      {
        sy = sx;
        mHeight = (int)(sy * this.srcHeight);
      }
      else
      {
        sx = sy;
        mWidth = (int)(sx * this.srcWidth);
      }

    }
    else if (sx > sy)
    {
      sx = sy;
      mWidth = (int)(sx * this.srcWidth);
    }
    else
    {
      sy = sx;
      mHeight = (int)(sy * this.srcHeight);
    }

    return new int[] { mWidth, mHeight };
  }

  public void small(int mWidth, int mHeight)
  {
    small(mWidth, mHeight, false);
  }

  public void small(int mWidth, int mHeight, boolean isCut)
  {
    if ((mWidth >= this.srcWidth) && (mHeight >= this.srcHeight)) {
      return;
    }
    String path = this.file.getPath();
    int[] mh = resize(mWidth, mHeight, isCut);
    Small.resize(path, mh[0], mh[1]);
    if (isCut)
    {
      Cut cut = new Cut(this.file);
      cut.cut(mWidth, mHeight);
    }
  }

  public void cut(int width, int height)
  {
    cut(width, height, null);
  }

  public void cut(int width, int height, String position)
  {
    if (Format.isEmpty(position)) {
      position = "center,center";
    }
    int[] xy = parserPosition(width, height, position);
    cut(width, height, xy[0], xy[1]);
  }

  public void cut(int width, int height, int x, int y)
  {
    Cut cut = new Cut(this.file);
    cut.cut(width, height, x, y);
  }

  public void watermark(String waterText, int fontsize)
  {
    watermark(waterText, fontsize, null);
  }

  public void watermark(String waterText, int fontsize, String position)
  {
    if (Format.isEmpty(position))
      position = "right-10,bottom-10";
  }

  public void watermark(String waterText, int fontsize, int x, int y)
  {
  }

  public void watermark(File waterImg)
  {
    watermark(waterImg, null);
  }

  public void watermark(File waterImg, String position)
  {
    if (Format.isEmpty(position)) {
      position = "right-10,bottom-10";
    }
    Img img = new Img(waterImg);

    int[] xy = parserPosition(img.srcWidth, img.srcHeight, position);
    watermark(waterImg, xy[0], xy[1]);
  }

  public void watermark(File waterImg, int x, int y)
  {
    Water.pressImage(waterImg.getPath(), this.file.getPath(), x, y);
  }

  private int[] parserPosition(int width, int height, String position)
  {
    int[] xy = new int[2];

    if (Format.isEmpty(position)) {
      position = "center,center";
    }
    position = position.replaceAll("top", "0").replaceAll("left", "0").replaceAll("right",String.valueOf(this.srcWidth - width));

    String[] xyPosition = position.split(",");
    xyPosition[0] = xyPosition[0].replaceAll("center", String.valueOf((this.srcWidth - width) / 2));
    xyPosition[1] = xyPosition[1].replaceAll("center", String.valueOf((this.srcHeight - height) / 2));

    xy[0] = Parser.parseInt(xyPosition[0]);
    xy[1] = Parser.parseInt(xyPosition[1]);

    return xy;
  }
}