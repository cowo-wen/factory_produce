package com.xx.util.checker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public abstract class Checker
{
  public abstract boolean check(String paramString);

  public static final String bytesToHexString(byte[] bytes)
  {
    StringBuffer sb = new StringBuffer(bytes.length);
    String sTemp = null;
    for (int i = 0; i < bytes.length; i++)
    {
      sTemp = Integer.toHexString(0xFF & bytes[i]);
      if (sTemp.length() < 2)
      {
        sb.append(0);
      }
      sb.append(sTemp);
    }
    return sb.toString().toUpperCase();
  }

  public static final String toType(byte[] image)
  {
    if ((image == null) || (image.length <= 16))
      return null;
    byte[] bytes = new byte[16];

    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = image[i];
    }
    return bytesToHexString(bytes);
  }

  public static final String toType(File file)
  {
    try
    {
      FileInputStream fis = new FileInputStream(file);
      String type = toType(fis);
      fis.close();
      fis = null;
      return type;
    }
    catch (IOException e) {
    }
    return null;
  }

  public static final String toType(InputStream inputStream)
  {
    try
    {
      File file = File.createTempFile("tmp", ".fid");
      FileOutputStream fos = new FileOutputStream(file);
      IOUtils.copy(inputStream, fos);
      fos.close();
      fos = null;

      InputStream is = new FileInputStream(file);

      byte[] bytes = new byte[16];
      is.read(bytes);
      is.close();
      is = null;

      file.delete();

      return bytesToHexString(bytes);
    }
    catch (IOException e) {
    }
    return null;
  }

  public boolean check(InputStream inputStream)
  {
    return check(toType(inputStream));
  }

  public boolean check(File file)
  {
    try
    {
      InputStream is = new FileInputStream(file);
      boolean b = check(is);
      is.close();
      is = null;
      return b;
    }
    catch (IOException e) {
    }
    return false;
  }

  public static final boolean checkImg(String type)
  {
    boolean b = (new Bmp().check(type)) || (new Gif().check(type)) || (new Jpg().check(type)) || (new Png().check(type));
    return b;
  }

  private static final String toTypeName(String type)
  {
    if (new Gif().check(type))
      return "gif";
    if (new Bmp().check(type))
      return "bmp";
    if (new Jpg().check(type))
      return "jpg";
    if (new Office().check(type))
      return "office";
    if (new Pdf().check(type))
      return "pdf";
    if (new Png().check(type))
      return "png";
    if (new Rar().check(type))
      return "rar";
    if (new Rtf().check(type))
      return "rtf";
    if (new Swf().check(type))
      return "swf";
    if (new Zip().check(type)) {
      return "zip";
    }
    return "";
  }

  public static final String toTypeName(byte[] image)
  {
    String type = toType(image);
    return toTypeName(type);
  }

  public static final String toTypeName(File image)
  {
    String type = toType(image);
    return toTypeName(type);
  }

  public static final String toTypeName(InputStream image)
  {
    String type = toType(image);
    return toTypeName(type);
  }

  public static final Checker instance(String type)
  {
    String clz = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();

    String name = Checker.class.getName();
    String path = name.substring(0, name.lastIndexOf(".") + 1) + clz;
    try
    {
      return (Checker)Class.forName(path).newInstance();
    }
    catch (InstantiationException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }

    return null;
  }
}