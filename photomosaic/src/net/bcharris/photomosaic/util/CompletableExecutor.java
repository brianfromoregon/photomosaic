package net.bcharris.photomosaic.util;

import java.util.concurrent.Executor;

public interface CompletableExecutor extends Executor
{
	public void awaitCompletionAndShutdown();
}
