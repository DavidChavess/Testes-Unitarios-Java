package br.ce.wcaquino.runners;

import static org.mockito.Mockito.withSettings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParallelRunner extends BlockJUnit4ClassRunner {

	public ParallelRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		setScheduler(new ThreadPoll());
	}
	
	private static class ThreadPoll implements RunnerScheduler{
		private ExecutorService exe;
		
		public ThreadPoll() {
			exe = Executors.newFixedThreadPool(2);			
		}
		
		@Override
		public void schedule(Runnable run) {
			exe.submit(run);
		}

		@Override
		public void finished() {
			exe.shutdown();
			try {
				exe.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		}
		
	}

}
