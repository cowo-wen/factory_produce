/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2014-12-28
 */
package com.app.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 功能说明：系统定时任务
 * 
 * @author chenwen 2014-12-28
 */
public class SystemTaskThread implements Runnable
{
    public static Log logger = LogFactory.getLog(SystemTaskThread.class);
    private static boolean isRun = false;

    
    
    public SystemTaskThread()
    {
        super();
    }

    @Override
    public void run()
    {
    	while (isRun)
        {
	        try
	        {
	            
	                
	        }
	        catch (Exception e)
	        {
	            logger.error("处理系统定时任务进程异常", e);
	            e.printStackTrace();
	        }
	        finally
	        {
	            try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
        }
    }

    
    
    /**
     * 启动线程
     * 
     * @author chenwen 2015-1-22
     */
    public synchronized static void startThread()
    {
        if (!isRun)
        {
        	isRun = true;
            new Thread(new SystemTaskThread()).start();
        }
    }

}
