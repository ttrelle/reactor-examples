package react;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import reactor.event.Event;
import reactor.function.Consumer;

/**
 * Downloads a given page from Wikipedia.
 * 
 * @author Tobias Trelle
 */
public class WikipediaDownloader implements Consumer<Event<String>> {

	private static final String BASE_URL = "http://en.wikipedia.org/wiki/";
	
	private static final String PAGES_FOLDER = "target/pages";
	
	private CountDownLatch latch;
	
	private File pagesRoot;
	
	public WikipediaDownloader(CountDownLatch latch) {
		this.latch = latch;
		pagesRoot = new File( PAGES_FOLDER );
		pagesRoot.mkdirs();
	}

	@Override
	public void accept(Event<String> event) {
		final String page = event.getData();
		
		try(
				InputStream in = new URL( BASE_URL + page ).openStream(); 
				OutputStream out = new FileOutputStream( new File( pagesRoot, page + ".html")) 
		) {
			int b;
			while ( (b = in.read()) != -1 ) {
				out.write(b);
			}
			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
