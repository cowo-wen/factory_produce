package com.app.util;

public class StaticBean
{
    

	public static final int YES = 1;
	
	public static final int NO = 2;
	
    /**
     * 
     * 
     * @author 是否有效
     */
    public static enum YES_OR_NO
    {
        yes
        {

            public String toString()
            {

                return String.valueOf(YES);
            }
        },
        no
        {

            public String toString()
            {

            	 return String.valueOf(NO);
            }
        };
    }

    
}
