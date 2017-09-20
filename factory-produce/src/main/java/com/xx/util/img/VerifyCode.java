package com.xx.util.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xx.util.string.Format;

public final class VerifyCode
{
  private static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  private static final int WIDTH = 250;
  private static final int HEIGHT = 80;
  private static final int LENGTH = 6;
  private static final Random random = new Random();

  private static final VerifyCode vc = new VerifyCode();

  private String generateVerifyCode(int verifySize)
  {
    return generateVerifyCode(verifySize, VERIFY_CODES);
  }

  private String generateVerifyCode(int verifySize, String sources)
  {
    if ((sources == null) || (sources.length() == 0))
    {
      sources = VERIFY_CODES;
    }
    int codesLen = sources.length();
    Random rand = new Random(System.currentTimeMillis());
    StringBuilder verifyCode = new StringBuilder(verifySize);
    for (int i = 0; i < verifySize; i++)
    {
      verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
    }
    return verifyCode.toString();
  }
  @SuppressWarnings("unused")
  private String outputVerifyImage(int w, int h, File outputFile, int verifySize) throws IOException
  {
    String verifyCode = generateVerifyCode(verifySize);
    outputImage(w, h, outputFile, verifyCode);
    return verifyCode;
  }
  @SuppressWarnings("unused")
  private String outputVerifyImage(int w, int h, OutputStream os, int verifySize)
    throws IOException
  {
    String verifyCode = generateVerifyCode(verifySize);
    outputImage(w, h, os, verifyCode);
    return verifyCode;
  }

  private void outputImage(int w, int h, File outputFile, String code)
    throws IOException
  {
    if (outputFile == null)
    {
      return;
    }
    File dir = outputFile.getParentFile();
    if (!dir.exists())
    {
      dir.mkdirs();
    }
    try
    {
      outputFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(outputFile);
      outputImage(w, h, fos, code);
      fos.close();
    }
    catch (IOException e)
    {
      System.out.println(code);
      throw e;
    }
  }

  private void outputImage(int w, int h, OutputStream os, String code)
    throws IOException
  {
    outputImage(w, h, os, code, false);
  }

  private void outputImage(int w, int h, OutputStream os, String code, boolean isRotation)
    throws IOException
  {
    int verifySize = code.length();
    BufferedImage image = new BufferedImage(w, h, 1);
    Random rand = new Random();
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Color[] colors = new Color[5];
    Color[] colorSpaces = { Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW };
    float[] fractions = new float[colors.length];
    for (int i = 0; i < colors.length; i++)
    {
      colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
      fractions[i] = rand.nextFloat();
    }
    Arrays.sort(fractions);

    g2.setColor(Color.GRAY);
    g2.fillRect(0, 0, w, h);

    g2.setColor(Color.WHITE);

    g2.fillRect(0, 2, w, h - 4);

    Random random = new Random();
    g2.setColor(getRandColor(160, 200));
    for (int i = 0; i < w / 12; i++)
    {
      int x = random.nextInt(w - 1);
      int y = random.nextInt(h - 1);
      int xl = random.nextInt(LENGTH) + 1;
      int yl = random.nextInt(12) + 1;
      g2.drawLine(x, y, x + xl + 40, y + yl + 20);
    }

    float yawpRate = 0.05F;
    int area = (int)(yawpRate * w * h);
    for (int i = 0; i < area; i++)
    {
      int x = random.nextInt(w);
      int y = random.nextInt(h);
      int rgb = getRandomIntColor();
      image.setRGB(x, y, rgb);
    }

    g2.setColor(Color.DARK_GRAY);
    int fontSize = (int)(h * 0.95D);
    Font font = new Font("Algerian", 2, fontSize);
    g2.setFont(font);
    char[] chars = code.toCharArray();
    for (int i = 0; i < verifySize; i++)
    {
      AffineTransform affine = new AffineTransform();
      if (isRotation)
        affine.setToRotation(0.7853981633974483D * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), w / verifySize * i + fontSize / 2, h / 2);
      g2.setTransform(affine);
      g2.drawChars(chars, i, 1, (w - (h - fontSize)) / verifySize * i, h / 2 + fontSize / 2 - (h - fontSize) / 2);
    }

    g2.dispose();
    ImageIO.write(image, "gif", os);
  }

  private Color getRandColor(int fc, int bc)
  {
    if (fc > 255)
      fc = 255;
    if (bc > 255)
      bc = 255;
    int r = fc + random.nextInt(bc - fc);
    int g = fc + random.nextInt(bc - fc);
    int b = fc + random.nextInt(bc - fc);
    return new Color(r, g, b);
  }

  private int getRandomIntColor()
  {
    int[] rgb = getRandomRgb();
    int color = 0;
    for (int c : rgb)
    {
      color <<= 8;
      color |= c;
    }
    return color;
  }

  private int[] getRandomRgb()
  {
    int[] rgb = new int[3];
    for (int i = 0; i < 3; i++)
    {
      rgb[i] = random.nextInt(255);
    }
    return rgb;
  }

  @SuppressWarnings("unused")
  private void shear(Graphics g, int w1, int h1, Color color)
  {
    shearX(g, w1, h1, color);
    shearY(g, w1, h1, color);
  }

  private void shearX(Graphics g, int w1, int h1, Color color)
  {
    int period = random.nextInt(2);

    boolean borderGap = true;
    int frames = 1;
    int phase = random.nextInt(2);

    for (int i = 0; i < h1; i++)
    {
      double d = (period >> 1) * Math.sin(i / period + 6.283185307179586D * phase / frames);
      g.copyArea(0, i, w1, 1, (int)d, 0);
      if (!borderGap)
        continue;
      g.setColor(color);
      g.drawLine((int)d, i, 0, i);
      g.drawLine((int)d + w1, i, w1, i);
    }
  }

  private void shearY(Graphics g, int w1, int h1, Color color)
  {
    int period = random.nextInt(40) + 10;

    boolean borderGap = true;
    int frames = 20;
    int phase = 7;
    for (int i = 0; i < w1; i++)
    {
      double d = (period >> 1) * Math.sin(i / period + 6.283185307179586D * phase / frames);
      g.copyArea(i, 0, 1, h1, 0, (int)d);
      if (!borderGap)
        continue;
      g.setColor(color);
      g.drawLine(i, (int)d, i, 0);
      g.drawLine(i, (int)d + h1, i, h1);
    }
  }

  public static String getStringCode(int length, String sources)
  {
    sources = Format.isEmpty(sources) ? VERIFY_CODES : sources;
    length = length > 0 ? length : LENGTH;
    String verifyCode = vc.generateVerifyCode(length, sources);
    return verifyCode;
  }

  public static String getStringCode(int length)
  {
    return getStringCode(length, null);
  }

  public static String getStringCode()
  {
    return getStringCode(0);
  }

  public static String getNumCode()
  {
    return getStringCode(LENGTH, "0123456789");
  }

  public static Img getImgCode(int width, int height, int length, String sources)
    throws IOException
  {
    width = width > 50 ? width : WIDTH;
    height = height > 20 ? height : HEIGHT;
    sources = Format.isEmpty(sources) ? VERIFY_CODES : sources;
    length = length > 0 ? length : LENGTH;

    String verifyCode = vc.generateVerifyCode(length, sources);
    File file = File.createTempFile(verifyCode.replaceAll("tmp", ""), ".jpg");
    vc.outputImage(width, height, file, verifyCode);
    String base64String = Base64.encoder(file);
    file.delete();
    file = null;

    return vc.getVerifyCode(verifyCode, base64String, width, height);
  }

  public static Img getImgCode(int width, int height, int length)
    throws IOException
  {
    return getImgCode(width, height, length, null);
  }

  public static Img getImgCode(int width, int height)
    throws IOException
  {
    return getImgCode(width, height, 0);
  }

  public static Img getImgCode()
    throws IOException
  {
    return getImgCode(0, 0);
  }

  private Img getVerifyCode(String verifyCode, String base64String, int width, int height)
  {
    return new Img(verifyCode, base64String, width, height);
  }

  public static void main(String[] args)
    throws IOException
  {
    Img img = getImgCode(0, 0, 4, "1234567890");
    System.out.println(img.verifyCode);
    System.out.println(img.base64String);
  }

  public class Img
  {
    public final String verifyCode;
    public final String base64String;
    public final int width;
    public final int height;

    private Img(String verifyCode, String base64String, int width, int height)
    {
      this.verifyCode = verifyCode;
      this.base64String = base64String;
      this.width = width;
      this.height = height;
    }
  }
}