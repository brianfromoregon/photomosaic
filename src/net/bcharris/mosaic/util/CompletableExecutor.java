package net.bcharris.mosaic.util;

import java.util.concurrent.Executor;

public interface CompletableExecutor extends Executor
{
	public void awaitCompletionAndShutdown();
}
