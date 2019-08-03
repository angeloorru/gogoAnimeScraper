package helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HelpersTest {

    private Helpers helpers = new Helpers();

    @Test
    public void testThatTheUrlIsValid() {
        assertTrue("", helpers.isValidUrl("http://www.google.co.uk"));
        assertTrue("", helpers.isValidUrl("http://google.co.uk"));
        assertTrue("", helpers.isValidUrl("https://google.com"));
        assertTrue("", helpers.isValidUrl("http://vidstreaming.io/load.php?id=MjgzOQ==&title=Saint+Seiya+Omega+Episode+18"));
    }

    @Test
    public void testThatTheUrlIsNotValid() {
        assertFalse("", helpers.isValidUrl("htpp://www.google.co.uk"));
        assertFalse("", helpers.isValidUrl(null));
    }


    @Test
    public void testThatAnEpisodeIsAvailable() throws IOException {
        Document doc;
        doc = Jsoup.connect("https://www2.gogoanime.io/saint-seiya-omega-episode-75").get();
        Elements elements = doc.getElementsByClass("vidcdn");

        assertTrue("", helpers.isEpisodeAvailable(elements));
    }

    @Test
    public void testThatAnEpisodeIsNotAvailable() throws IOException {
        Document doc;
        doc = Jsoup.connect("https://www2.gogoanime.io/saint-seiya-omega-episode-1").get();
        Elements elements = doc.getElementsByClass("test");

        assertFalse("", helpers.isEpisodeAvailable(elements));
    }

    @Test
    public void testThatEpisodeNumberIsExtractedSuccessfully() {
        assertEquals(1, helpers.getEpisodeNumberForSettingCounter("https://www2.gogoanime.io/saint-seiya-omega-episode-1"));
    }

    @Test
    public void testThatEpisodeNumberIsNotExtractedSuccessfully() {
        assertEquals(-1, helpers.getEpisodeNumberForSettingCounter("https://www2.gogoanime.io/saint-seiya-omega-episode-"));
    }
}