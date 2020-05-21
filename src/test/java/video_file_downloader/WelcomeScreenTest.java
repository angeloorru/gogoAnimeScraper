package video_file_downloader;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class WelcomeScreenTest {

    private final WelcomeScreen welcomeScreen = new WelcomeScreen();

    @Test
    public void testThatTheUrlIsValidForDownload() {
        assertTrue(
                "",
                welcomeScreen.validateUrlForDownload(
                        "https://www19.gogoanime.io/category/lupin-the-3rd-dragon-of-doom"
                ));
    }

    @Test
    public void assertThatTheUrlIsNotValidForDownload() {
        assertFalse(
                "",
                welcomeScreen.validateUrlForDownload(
                        "http://vidstreaming.io/load.php?id=MjgzOQ==&title=Saint+Seiya+Omega+Episode+18"
                ));
    }

    @Test
    public void assertThatNoOfEpisodeIsValidInteger() {
        assertTrue("", welcomeScreen.getNumberOfEpisodeToStartDownload(1));
        assertTrue("", welcomeScreen.getNumberOfEpisodeToStartDownload(10));
        assertTrue("", welcomeScreen.getNumberOfEpisodeToStartDownload(100));
    }

    @Test
    public void assertThatNoOfEpisodeIsNotValidInteger() {
        assertFalse("", welcomeScreen.getNumberOfEpisodeToStartDownload(0));
        assertFalse("", welcomeScreen.getNumberOfEpisodeToStartDownload(-1));
        assertFalse("", welcomeScreen.getNumberOfEpisodeToStartDownload(-10));
    }

}