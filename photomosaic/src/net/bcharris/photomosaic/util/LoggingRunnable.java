package net.bcharris.photomosaic.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingRunnable implements Runnable
{ 
    private final Runnable job;
    private final Log log = LogFactory.getLog(LoggingRunnable.class);
 
    public LoggingRunnable(Runnable job)
    {
        this.job = job;
    }
 
    public void run()
    {
        try
        {
            job.run();
        }
        catch (Throwable t)
        {
            log.error("While running wrapped job.", t);
        }
    }
}
