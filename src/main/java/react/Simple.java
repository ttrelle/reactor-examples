package react;

import static reactor.event.selector.Selectors.$;

import java.util.concurrent.CountDownLatch;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;

/**
 * 
 * @author Tobias Trelle
 */
public class Simple {

	private static final int REPEAT_COUNT = 100;
	
	private static final String[] PAGES = new String[]{ 
		"Reactive_programming", 
		"Reactor_pattern",
		"Spring_Framework"
		};
	
	private static final int N = REPEAT_COUNT * PAGES.length;
	
	private static Environment env = new Environment();
	
	public static void main(String[] argv) throws InterruptedException {
		
		final CountDownLatch latch = new CountDownLatch( N );
		final Reactor r = Reactors
				.reactor()
				.env(env)
				.dispatcher(Environment.THREAD_POOL)
				.get();
	
		// Register Consumer
		r.on( $("download"), new WikipediaDonwloader(latch));
	
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
