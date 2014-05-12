package react;

import static reactor.event.selector.Selectors.$;

import java.util.concurrent.CountDownLatch;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;

/**
 * Simple example for events processing with Project Reactor.
 * 
 * @author Tobias Trelle
 */
public class Simple {

	/** Repeat count for download events. */
	private static final int REPEAT_COUNT = 1;
	
	/** Names of Wikipedia pages to download. */
	private static final String[] PAGES = new String[]{ 
		"Reactive_programming", 
		"Reactor_pattern",
		"Spring_Framework"
		};
	
	private static final int N = REPEAT_COUNT * PAGES.length;
	
	/** Static Reactor environment. */
	private static Environment env = new Environment();
	
	public static void main(String[] argv) throws InterruptedException {
		
		final CountDownLatch latch = new CountDownLatch( N );
		
		// Create reactor
		final Reactor r = Reactors
				.reactor()
				.env(env)
				.dispatcher(Environment.THREAD_POOL)
				.get();
	
		// Register Consumer
		r.on( $("download"), new WikipediaDownloader(latch));
	
		long start = now();
		
		// Publish Event(s)
		for (int i =0; i < REPEAT_COUNT; i++) {
			for (String page : PAGES) {
				r.notify( "download", Event.wrap(page) );
			}
		}
		latch.await();
		
		long end = now() - start;
		System.out.println("runtime [ms]: " + end);
		System.out.println("average [ms]: " + ( end/N ));
	}
	
	private static long now() { return System.currentTimeMillis(); }
	
}
