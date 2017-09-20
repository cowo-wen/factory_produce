package com.xx.util.string;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Format
{
  public static final String comma = ",";
  private static Pattern dateWithHour = Pattern.compile("^(\\d{4}\\D{1,}[0-1]{0,1}\\d\\D{1,}\\d{1,2}\\D*?) ?(\\d{1,2}\\D{1,}\\d{1,2}\\D{1,}\\d{1,2}\\D*?)?$");

  private static Pattern dateWithOutspilter = Pattern.compile("^(\\d{4})(\\d)(\\d)(\\d{0,2}) ?(\\d{1,2}\\D{1,}\\d{1,2}\\D{1,}\\d{1,2}\\D*?)?$");

  public static final long[] objectTolong(Object[] arr)
  {
    long[] result = null;
    if ((arr == null) || (arr.length == 0))
    {
      return result;
    }
    result = new long[arr.length];
    for (int i = 0; i < arr.length; i++)
    {
      result[i] = Long.parseLong(arr[i].toString());
    }
    return result;
  }

  public static final String nullToTrim(String str)
  {
    if (str == null)
    {
      str = "";
    }

    return str.trim();
  }

  public static final String arrToStr(Object[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(boolean[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(byte[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(char[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(String[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(double[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(float[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(int[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(long[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToStr(short[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = tmp.substring(1, tmp.length() - 1);
    }
    return tmp;
  }

  public static final String arrToSql(Object[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(boolean[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(byte[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(char[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(String[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(double[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(float[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(int[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(long[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String arrToSql(short[] arr)
  {
    String tmp = null;
    if (arr != null)
    {
      tmp = Arrays.toString(arr);
      tmp = "$${" + tmp.substring(1, tmp.length() - 1) + "}$$";
    }
    return tmp;
  }

  public static final String[] matchParams(String condition)
  {
    Pattern p = Pattern.compile("\\{\\w+\\}");
    Matcher m = p.matcher(condition);
    String sp = "";
    StringBuffer s = new StringBuffer();
    while (m.find())
    {
      s.append(sp).append(m.group(0).replaceAll("\\{|\\}", "'"));
      sp = ",";
    }
    if (s.length() == 0)
    {
      return null;
    }
    return s.toString().split(",");
  }

  public static final String toString(Object value, String format)
  {
    if (((value instanceof Float)) || ((value instanceof Double)))
    {
      if (isEmpty(format))
      {
        format = "######0.00";
      }
      DecimalFormat df = new DecimalFormat(format);
      return df.format(value);
    }
    if (((value instanceof java.util.Date)) || ((value instanceof java.sql.Date)) || ((value instanceof Timestamp)))
    {
      java.util.Date date = (java.util.Date)value;
      Calendar c1 = Calendar.getInstance();
      c1.setTime(date);

      Calendar c2 = Calendar.getInstance();

      if (isEmpty(format))
      {
        if ((c1.get(6) == c2.get(6)) && (c1.get(1) == c2.get(1)))
        {
          format = "hh:mm:ss";
        }
        else if ((c1.get(2) == c2.get(2)) && (c1.get(1) == c2.get(1)))
        {
          format = "dd hh:mm";
        }
        else if (c1.get(1) == c2.get(1))
        {
          format = "MM-dd";
        }
        else
        {
          format = "yy-MM-dd";
        }
      }
      SimpleDateFormat sf = new SimpleDateFormat(format);

      return sf.format((java.util.Date)value);
    }

    if ((value == null) || ("".equals(value.toString().trim())))
      return "";
    return value.toString();
  }

  public static final String toString(Object value)
  {
    return toString(value, null);
  }

  public static final String quote(Object value)
  {
    if (value == null)
    {
      return null;
    }

    if (((value instanceof Number)) || ((value instanceof Boolean)))
    {
      return value.toString();
    }

    return "$$" + value.toString() + "$$";
  }

  public static final String quote(Object value, String quote)
  {
    if (value == null)
    {
      return null;
    }

    if (((value instanceof Number)) || ((value instanceof Boolean)))
    {
      return value.toString();
    }

    return quote + value.toString() + quote;
  }

  public static Timestamp strToDate(String datestr)
  {
    Timestamp ts = null;

    datestr = nullToTrim(datestr);
    datestr = datestr.replaceAll("  ", " ");

    if (isEmpty(datestr))
    {
      java.util.Date d = new java.util.Date();
      ts = new Timestamp(d.getTime());
      return ts;
    }

    try
    {
      Matcher matcher = dateWithHour.matcher(datestr);
      if (matcher.matches())
      {
        String date = matcher.group(1);
        String time = matcher.group(2);
        if (isEmpty(time))
        {
          datestr = datestr.replaceAll("\\D{1,}", "-").trim();
          if (datestr.endsWith("-"))
          {
            datestr = datestr.substring(0, datestr.length() - 1);
          }

          SimpleDateFormat dfWithOutDate = new SimpleDateFormat("yyyy-MM-dd");
          java.util.Date d = dfWithOutDate.parse(datestr);
          ts = new Timestamp(d.getTime());
        }
        else if ((!isEmpty(date)) && (!isEmpty(time)))
        {
          date = date.replaceAll("\\D{1,}", "-").trim();
          if (date.endsWith("-"))
          {
            date = date.substring(0, date.length() - 1);
          }
          time = time.replaceAll("\\D{1,}", ":").trim();
          if (time.endsWith(":"))
          {
            time = time.substring(0, time.length() - 1);
          }
          datestr = date + " " + time;

          SimpleDateFormat dfWithDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          java.util.Date d = dfWithDate.parse(datestr);
          ts = new Timestamp(d.getTime());
        }
      }

      matcher = dateWithOutspilter.matcher(datestr);
      if (matcher.matches())
      {
        String ss = "";
        if (matcher.group(2).equals("1"))
        {
          ss = matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4);
        }
        else
        {
          String y = matcher.group(1);
          String m = matcher.group(2) + matcher.group(3) + matcher.group(4);
          ss = y + "0" + m;
        }

        ss = ss.replaceAll("\\D{1,}", "-").trim();
        if (ss.endsWith("-"))
        {
          ss = ss.substring(0, ss.length() - 1);
        }

        if (isEmpty(matcher.group(5)))
        {
          SimpleDateFormat dfWithOutDate = new SimpleDateFormat("yyyyMMdd");
          java.util.Date d = dfWithOutDate.parse(ss);
          ts = new Timestamp(d.getTime());
        }
        else
        {
          String time = matcher.group(5);
          time = time.replaceAll("\\D{1,}", ":").trim();
          if (time.endsWith(":"))
          {
            time = time.substring(0, time.length() - 1);
          }
          datestr = ss + " " + time;
          SimpleDateFormat dfWithOutSplit = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
          java.util.Date d = dfWithOutSplit.parse(datestr);
          ts = new Timestamp(d.getTime());
        }
      }
    }
    catch (ParseException e)
    {
      java.util.Date d = new java.util.Date();
      ts = new Timestamp(d.getTime());
    }
    finally
    {
    	if(ts != null) return ts; 
    }
    java.util.Date d = new java.util.Date();
    ts = new Timestamp(d.getTime());

     return ts;
  }

  public static boolean isEmpty(String str)
  {
    return (str == null) || ("".equals(str.trim())) || ("null".equals(str.trim())) || ("undefined".equals(str.trim()));
  }

  public static boolean isEmpty(Object obj)
  {
    return (obj == null) || ("".equals(obj.toString().trim())) || ("null".equals(obj.toString().trim())) || ("undefined".equals(obj.toString().trim()));
  }

  public static boolean isNumeric(String str)
  {
    Pattern pattern = Pattern.compile("[0-9]*");
    return pattern.matcher(str).matches();
  }

  public static boolean noEmpty(String str)
  {
    return !isEmpty(str);
  }

  public static boolean strToBoolean(String str)
  {
    return (str == null) || ("".equals(str.trim())) ? false : Boolean.parseBoolean(str);
  }

  public static boolean strToBoolean(String str, boolean defaultVal)
  {
    return (str == null) || ("".equals(str.trim())) ? defaultVal : Boolean.parseBoolean(str);
  }

  public static int strToInt(String str)
  {
    return strToInt(str, 0);
  }

  public static int strToInt(String str, int defaultVal)
  {
    return (str == null) || ("".equals(str.trim())) ? defaultVal : Integer.parseInt(matchNumber(str));
  }

  public static long strToLong(String str)
  {
    return strToLong(str, 0L);
  }

  public static Double[] strToDoubleArray(String strIds)
  {
    return strToDoubleArray(strIds, ",");
  }

  public static Double[] strToDoubleArray(String strIds, String split)
  {
    if ((strIds == null) || ("".equals(strIds.trim())) || (strIds.trim().equals("null")) || (strIds.trim().equals("undefined")))
    {
      return null;
    }

    strIds = format(strIds, split);
    String[] ids = strIds.split(split);
    Double[] id = new Double[ids.length];
    int i = 0; for (int length = ids.length; i < length; i++)
    {
      id[i] = Double.valueOf(Double.parseDouble(matchNumber(ids[i].trim())));
    }
    return id;
  }

  public static int[] strTointArray(String strIds)
  {
    return strTointArray(strIds, ",");
  }

  public static int[] strTointArray(String strIds, String split)
  {
    if ((strIds == null) || ("".equals(strIds.trim())) || (strIds.trim().equals("null")) || (strIds.trim().equals("undefined")))
    {
      return null;
    }

    strIds = format(strIds, split);
    String[] ids = strIds.split(split);
    int[] id = new int[ids.length];
    int i = 0; for (int length = ids.length; i < length; i++)
    {
      id[i] = Integer.parseInt(matchNumber(ids[i].trim()));
    }
    return id;
  }

  public static Integer[] strToIntArray(String strIds)
  {
    return strToIntArray(strIds, ",");
  }

  public static Integer[] strToIntArray(String strIds, String split)
  {
    if ((strIds == null) || ("".equals(strIds.trim())) || (strIds.trim().equals("null")) || (strIds.trim().equals("undefined")))
    {
      return null;
    }

    strIds = format(strIds, split);
    String[] ids = strIds.split(split);
    Integer[] id = new Integer[ids.length];
    int i = 0; for (int length = ids.length; i < length; i++)
    {
      id[i] = Integer.valueOf(Integer.parseInt(matchNumber(ids[i].trim())));
    }
    return id;
  }

  public static long[] strTolongArray(String strIds)
  {
    return strTolongArray(strIds, ",");
  }

  public static long[] strTolongArray(String strIds, String split)
  {
    if ((strIds == null) || ("".equals(strIds.trim())) || (strIds.trim().equals("null")) || (strIds.trim().equals("undefined")))
    {
      return null;
    }

    strIds = format(strIds, split);
    String[] ids = strIds.split(split);
    long[] id = new long[ids.length];
    int i = 0; for (int length = ids.length; i < length; i++)
    {
      if (!ids[i].trim().matches("\\d+"))
        continue;
      id[i] = Long.parseLong(matchNumber(ids[i].trim()));
    }

    return id;
  }

  public static Long[] strToLongArray(String strIds)
  {
    return strToLongArray(strIds, ",");
  }

  public static Long[] strToLongArray(String strIds, String split)
  {
    if ((strIds == null) || ("".equals(strIds.trim())) || (strIds.trim().equals("null")) || (strIds.trim().equals("undefined")))
    {
      return null;
    }

    strIds = format(strIds, split);
    String[] ids = strIds.split(split);
    Long[] id = new Long[ids.length];
    int i = 0; for (int length = ids.length; i < length; i++)
    {
      id[i] = Long.valueOf(Long.parseLong(matchNumber(ids[i].trim())));
    }
    return id;
  }

  public static List<Long> strToLongList(String param)
  {
    List<Long> ids = new ArrayList<Long>();

    if ((param == null) || (param.trim().equals("")) || (param.trim().equals("null")) || (param.trim().equals("undefined")))
    {
      return ids;
    }
    String[] idArray = strToStrArray(param, ",");
    try
    {
      for (String id : idArray)
      {
        if ((id == null) || ("".equals(id.trim())))
        {
          continue;
        }
        ids.add(Long.valueOf(Long.parseLong(matchNumber(id))));
      }
    }
    catch (NumberFormatException e)
    {
      ids = null;
    }
    return ids;
  }

  public static Set<Long> strToLongSet(String param)
  {
    Set<Long> ids = new HashSet<Long>();

    if ((param == null) || (param.trim().equals("")) || (param.trim().equals("null")) || (param.trim().equals("undefined")))
    {
      return ids;
    }
    String[] idArray = strToStrArray(param, ",");
    try
    {
      for (String id : idArray)
      {
        if ((id == null) || ("".equals(id.trim())))
        {
          continue;
        }
        ids.add(Long.valueOf(Long.parseLong(matchNumber(id))));
      }
    }
    catch (NumberFormatException e)
    {
      ids = null;
    }
    return ids;
  }

  public static String[] strToStrArr(String str)
  {
    return strToStrArr(str, ",");
  }

  public static String[] strToStrArr(String str, String split)
  {
    str = format(str, split);
    return str.split(split);
  }

  public static String[] strToStrArray(String str)
  {
    return strToStrArray(str, ",");
  }

  public static String[] strToStrArray(String str, String split)
  {
    if ((str == null) || ("".equals(str.trim())) || (str.trim().equals("null")) || (str.trim().equals("undefined")))
    {
      return null;
    }

    str = format(str, split);
    return str.split(split);
  }

  public static long strToLong(String str, long defaultVal)
  {
    return (str == null) || ("".equals(str.trim())) ? defaultVal : Long.parseLong(matchNumber(str));
  }

  public static double strToDouble(String str)
  {
    return strToDouble(str, 0.0D);
  }

  public static double strToDouble(String str, double defaultVal)
  {
    return (str == null) || ("".equals(str.trim())) ? defaultVal : Double.parseDouble(matchNumber(str));
  }

  public static String formatJSON(String json)
  {
    return json.replaceAll("undefined", "\"\"");
  }

  private static String format(String exp, String split)
  {
    if (isEmpty(exp))
    {
      return "";
    }

    String tmp = exp;

    if (split.indexOf("\\") == 0)
    {
      split = split.substring(1);
    }

    int lenSplit = split.length();

    if (tmp.length() >= lenSplit)
    {
      if (split.equals(tmp.substring(0, lenSplit)))
      {
        tmp = tmp.substring(lenSplit);
      }
    }

    int len = tmp.length();
    if (len > lenSplit)
    {
      if (split.equals(tmp.substring(len - lenSplit, len)))
      {
        tmp = tmp.substring(0, len - lenSplit);
      }
    }

    return tmp;
  }

  public static String matchNumber(String str)
  {
    if (isEmpty(str)) {
      return "0";
    }
    Pattern p = Pattern.compile("^(\\d+)(.*)", 2);
    Matcher m = p.matcher(str);
    return m.find() ? m.group(1) : "0";
  }

  public static OutputStream assemblyHeader(HttpServletRequest request, HttpServletResponse response, String fileName)
    throws Exception
  {
    response.reset();
    response.setContentType("application/x-msdownload");
    String agent = request.getHeader("User-Agent");
    if ((agent != null) && (agent.indexOf("MSIE") != -1))
    {
      fileName = URLEncoder.encode(fileName, "UTF-8");
    }
    else
    {
      fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
    }

    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    return response.getOutputStream();
  }

  public static String trimParam(String str)
  {
    String reguler = "[0-9]{1,8}(_[-0-9]{1,8}){0,20}";
    String uri = str.replaceAll("-" + reguler + "|" + reguler, "{*}");
    return uri;
  }

  public static List<String> getParam(String str)
  {
    List<String> list = new ArrayList<String>();
    if (str.contains("${"))
    {
      String PATTERN = "\\$\\{[a-z]*[0-9]*}";
      Pattern p = Pattern.compile(PATTERN);
      Matcher m = p.matcher(str);
      while (m.find())
      {
        String g = m.group();
        g = g.replaceAll("\\$\\{", "");
        g = g.replaceAll("\\}", "");
        list.add(g);
      }
    }
    return list;
  }

  public static boolean isMobile(String str)
  {
    Pattern p = null;
    Matcher m = null;
    boolean b = false;
    p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$");
    m = p.matcher(str);
    b = m.matches();
    return b;
  }

  public static void main(String[] args)
  {
    String str = "尊敬的${currentuser}于${nowtime}登录";
    List<String> list = new ArrayList<String>();
    String PATTERN = "\\$\\{[a-z]*[0-9]*}";
    Pattern p = Pattern.compile(PATTERN);
    Matcher m = p.matcher(str);
    while (m.find())
    {
      String g = m.group();
      g = g.replaceAll("\\$\\{", "");
      g = g.replaceAll("}", "");
      System.out.println(g);
      list.add(g);
    }

    System.out.println("finish");
  }
}