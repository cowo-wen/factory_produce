package com.xx.util.img;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Cut
{
  private String srcpath;
  private String subpath;
  private int x;
  private int y;
  private int width;
  private int height;
  private int srcWidth;
  private int srcHeight;

  public Cut(File srcFile, File subFile)
  {
    this.srcpath = srcFile.getPath();
    this.subpath = (subFile != null ? subFile.getPath() : this.srcpath);
    Img img = new Img(srcFile);
    this.srcWidth = img.getSrcWidth();
    this.srcHeight = img.getSrcHeight();
  }

  public Cut(File srcFile)
  {
    this(srcFile, null);
  }

  public Cut(String srcPath)
  {
    this(new File(srcPath), null);
  }

  public Cut(String srcPath, String subPath)
  {
    this(new File(srcPath), new File(subPath));
  }

  public void cut(int width, int height)
  {
    if ((width >= this.srcWidth) && (height >= this.srcHeight)) {
      return;
    }
    if (width >= this.srcWidth)
      this.x = 0;
    else {
      this.x = ((this.srcWidth - width) / 2);
    }
    if (height >= this.srcHeight)
      this.y = 0;
    else {
      this.y = ((this.srcHeight - height) / 2);
    }
    cut(width, height, this.x, this.y);
  }

  public void cut(int width, int height, int x, int y)
  {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    try
    {
      cut();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void cut()
    throws IOException
  {
    FileInputStream is = null;
    ImageInputStream iis = null;
    try
    {
      is = new FileInputStream(this.srcpath);

      Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
      ImageReader reader = (ImageReader)it.next();

      iis = ImageIO.createImageInputStream(is);

      reader.setInput(iis, true);

      ImageReadParam param = reader.getDefaultReadParam();

      Rectangle rect = new Rectangle(this.x, this.y, this.width, this.height);

      param.setSourceRegion(rect);

      BufferedImage bi = reader.read(0, param);

      ImageIO.write(bi, "jpg", new File(this.subpath));
    }
    finally
    {
      if (is != null)
        is.close();
      if (iis != null)
        iis.close();
    }
  }

  public static void main(String[] args)
    throws Exception
  {
  }
}