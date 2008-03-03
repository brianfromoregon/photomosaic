package net.bcharris.photomosaic.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingRunnable implements Runnable
{ 
    private final Runnable job;
    private final Logger log = Logger.getLogger(LoggingRunnable.class.getName());
 
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
            log.log(Level.SEVERE, "While running wrapped job.", t);
        }
    }
}
