package video_file_downloader.builders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.EpisodeDownloader;
import video_file_downloader.enums.EpisodeDownloaderEnum;
import video_file_downloader.enums.HtmlTagEnum;

import java.io.IOException;
import java.util.logging.Logger;

public class FileYearBuilder {
    private static final Logger LOGGER = Logger.getLogger(FileYearBuilder.class.getName());

    private final EpisodeDownloader episodeDownloader;

    public FileYearBuilder(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    /**
     * @return
     * @desc Extracts the year of the serie by scraping the html page
     */
    public String extractYear() {
        String year = null;
        Document doc;

        try {
            LOGGER.info("Attempting to build the file year");
            doc = Jsoup.connect(episodeDownloader.getUrlHome()).get();

            Elements releasedYear = doc.getElementsByClass(HtmlTagEnum.RELEASE_YEAR_TAG.getValue());
            year = searchForYearString(releasedYear);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        if (isReleasedYearAvailable(year)) {
            year = EpisodeDownloaderEnum.YEAR_NOT_AVAILABLE.getValue();
        }
        LOGGER.info("File year [" + year + "] built");
        return year;
    }

    /**
     * @param year
     * @return
     */
    private boolean isReleasedYearAvailable(String year) {
        return year == null || year.equals("0");
    }

    /**
     * @param releasedYear
     * @return
     */
    private String searchForYearString(Elements releasedYear) {
        String year = null;

        for (Element span : releasedYear) {
            if (span.text().contains(EpisodeDownloaderEnum.RELEASED.getValue())) {
                year = span.text().replace(" ", "")
                        .replace(EpisodeDownloaderEnum.RELEASED.getValue(), "");
            }
        }
        return year;
    }
}