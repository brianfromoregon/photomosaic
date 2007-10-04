package net.bcharris.mosaic.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleCompletableExecutor implements CompletableExecutor
{
	private final ExecutorService service;

	private final List<Future> jobs = Collections.synchronizedList(new LinkedList<Future>());

	private final Log log = LogFactory.getLog(SimpleCompletableExecutor.class);

	private volatile long earliestCompletion = System.currentTimeMillis();

	public SimpleCompletableExecutor(int numThreads)
	{
		this.service = Executors.newFixedThreadPool(numThreads);
	}

	public void execute(Runnable job)
	{
		earliestCompletion = System.currentTimeMillis() + 100;
		jobs.add(service.submit(new LoggingRunnable(job)));
	}

	public void awaitCompletionAndShutdown()
	{
		try
		{
			while (!jobs.isEmpty() || System.currentTimeMillis() <= earliestCompletion)
			{
				if (!jobs.isEmpty())
				{
					Future job = jobs.remove(0);
					try
					{
						job.get();
					}
					catch (InterruptedException e)
					{
						log.debug("Interrupted while calling get() on a Future, will retry later.", e);
						jobs.add(job);
					}
					catch (ExecutionException e)
					{
						// The LoggingRunnable wrapper will produce a better message
						// log.error(e);
					}
					catch (CancellationException e)
					{
						log.error(e);
					}
				}
				else
				{
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{

					}
				}
			}
		}
		finally
		{
			service.shutdown();
		}
	}
}
