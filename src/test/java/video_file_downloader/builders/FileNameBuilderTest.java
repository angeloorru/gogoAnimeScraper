package video_file_downloader.builders;

import org.junit.Test;
import video_file_downloader.EpisodeDownloader;

import static junit.framework.TestCase.assertEquals;


public class FileNameBuilderTest {
    FileNameBuilder fileNameBuilder = new FileNameBuilder();

    @Test
    public void testBuildFileNameIfLessThanTenEpisodes() {
        EpisodeDownloader.episodeCounter = 6;
        assertEquals("06", fileNameBuilder.buildFileNameIfLessThanTenEpisodes());
    }

    @Test
    public void testBuildFileNameIfTenEpisodes() {
        EpisodeDownloader.episodeCounter = 10;
        assertEquals("10", fileNameBuilder.buildFileNameIfLessThanTenEpisodes());
    }

    @Test
    public void testBuildFileNameIfMoreThanTenEpisodes() {
        EpisodeDownloader.episodeCounter = 15;
        assertEquals("15", fileNameBuilder.buildFileNameIfLessThanTenEpisodes());
    }

    @Test
    public void testBuildFileName(){
        String episodeNumber = "04";
        String seriesTitle = "TestSeriesTitle";
        String seriesYear = "2020";

        assertEquals("TestSeriesTitle_Episode-04_(2020).mp4",
                fileNameBuilder.buildFileName(25, episodeNumber,seriesTitle,seriesYear));

        assertEquals("TestSeriesTitle_(2020).mp4",
                fileNameBuilder.buildFileName(1, episodeNumber,seriesTitle,seriesYear));
    }
}