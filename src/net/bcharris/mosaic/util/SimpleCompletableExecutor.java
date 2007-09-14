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

	public SimpleCompletableExecutor(int numThreads)
	{
		this.service = Executors.newFixedThreadPool(numThreads);
	}

	public void execute(Runnable job)
	{
		jobs.add(service.submit(job));
	}

	public void awaitCompletionAndShutdown()
	{
		try
		{
			while (!jobs.isEmpty())
			{
				Future job = jobs.remove(0);
				try
				{
					job.get();
				}
				catch (InterruptedException e)
				{
					jobs.add(job);
				}
				catch (ExecutionException e)
				{
					log.error(e);
				}
				catch (CancellationException e)
				{
					log.error(e);
				}
			}
		}
		finally
		{
			service.shutdown();
		}
	}
}
