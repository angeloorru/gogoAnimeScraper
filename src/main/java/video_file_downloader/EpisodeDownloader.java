package video_file_downloader;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;


public class EpisodeDownloader {

    private static final Logger LOGGER = Logger.getLogger(EpisodeDownloader.class.getName());
    private static final String URL_HOME = "https://www2.gogoanime.io/category/mobile-suit-gundam-seed-destiny-special-edition-dub";
    private static final int FIRST_EPISODE = 1;

    public static int episodeCounter = 1;

    private String URL = buildUrlForDownloadByEpisode(URL_HOME);
    private String seriesTitle = extractFileName();
    private String seriesYear = extractYear();
    private int totalNumberOfEpisodes = getTotalNumberOfEpisodeFromHtmlPage();

    /**
     * @return Url for the request
     */
    public List<String> constructUrlForRequest() {
        List<String> url = new ArrayList<>();

        LOGGER.info("Retrieving number of episodes");
        int totalNumberOfEpisodes = getTotalNumberOfEpisodeFromHtmlPage();
        LOGGER.info("Found " + totalNumberOfEpisodes + " episodes");

        for (int i = FIRST_EPISODE; i <= totalNumberOfEpisodes; i++) {
            String episodesUrl = URL + i;
            url.add(episodesUrl);
        }
        return url;
    }

    /**
     * @param link Used for the logger and YouTube-DL request only
     * @desc Uses youtube-dl java for downloading the video.
     */
    public void downloadVideoWithYouTubeDl(String link) {
        String episodeNumber = buildFileNameIfLessThanTenEpisodes();
        String fileName = buildFileName(episodeNumber, seriesTitle, seriesYear);

        YoutubeDLRequest request = buildYoutubeDLRequest(link, fileName);

        LOGGER.info("For Link " + link + ": Attempting to download " +
                fileName + " [ Episode " + episodeNumber + " of " + totalNumberOfEpisodes + " ]");

        YoutubeDLResponse response = null;
        try {
            response = YoutubeDL.execute(request);
            episodeCounter++;
        } catch (YoutubeDLException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
            //episodeCounter++;
        }

        if (response.getExitCode() == 0) {
            LOGGER.info("File " + fileName + " downloaded successfully");
            if (Integer.valueOf(episodeNumber) < totalNumberOfEpisodes) {
                try {
                    LOGGER.info("Pausing for 15 seconds now...");
                    sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param urlHome
     * @return
     * @desc Takes the main page url and process it for building the download link for a single episode.
     */
    private String buildUrlForDownloadByEpisode(String urlHome) {
        String url;
        url = urlHome.replace("/category", "") + "-episode-";

        return url;
    }

    /**
     * @param episodeNumber
     * @param seriesTitle
     * @param seriesYear
     * @return The file name
     * @desc Build the file name
     */
    private String buildFileName(String episodeNumber, String seriesTitle, String seriesYear) {
        //TODO:Remove contains() as it is useful only for Gundam
        if (seriesTitle.contains("Mobile_Suit")) {
            return seriesTitle + "_Episode-" + episodeNumber + "_(" + seriesYear + ").mp4";
        } else {
            return "Mobile_Suit_" + seriesTitle + "_Episode-" + episodeNumber + "_(" + seriesYear + ").mp4";
        }
    }

    /**
     * @return The total number of episodes
     */
    private int getTotalNumberOfEpisodeFromHtmlPage() {
        int lastEpisode = 0;
        Document doc;

        try {
            doc = Jsoup.connect(URL_HOME).get();

            Elements videoBody = doc.getElementsByClass("anime_video_body");
            Element hrefContainer = videoBody.select("a").first();
            lastEpisode = Integer.valueOf(hrefContainer.attr("ep_end"));

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return lastEpisode;
    }

    /**
     * @return The counter used for setting up the current number of episodes
     */
    private String buildFileNameIfLessThanTenEpisodes() {

        String appendBeforeEpisode = "0";

        if (episodeCounter < 10) {
            return appendBeforeEpisode + episodeCounter;
        } else {
            return String.valueOf(episodeCounter);
        }
    }

    /**
     * @return
     * @desc Extracts the file name by scraping the html page
     */
    private String extractFileName() {
        String title = null;
        Document doc;

        LOGGER.info("Attempting to build the file title");
        try {
            doc = Jsoup.connect(URL_HOME).get();

            Elements productName = doc.getElementsByClass("anime_info_body_bg");
            String htmlTitle = productName.select("h1").first().toString();

            doc = Jsoup.parse(htmlTitle);

            title = doc.body().text();
            title = title.replace(" (Dub)", "");
            title = title.replaceAll(": ", ":");
            title = title.replace(" ", "_");
        } catch (IOException e) {
            e.getMessage();
        }
        LOGGER.info("File tile [" + title + "] built");
        return title;
    }

    /**
     * @return
     * @desc Extracts the year of the serie by scraping the html page
     */
    private String extractYear() {
        String year = null;
        Document doc;
        final String ELEMENT_TO_SEARCH_FOR = "Released:";

        try {
            LOGGER.info("Attempting to build the file year");
            doc = Jsoup.connect(URL_HOME).get();

            Elements releasedYear = doc.getElementsByClass("type");

            for (Element span : releasedYear) {
                if (span.text().contains(ELEMENT_TO_SEARCH_FOR)) {
                    year = span.text().replace(" ", "").replace(ELEMENT_TO_SEARCH_FOR, "");
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }

        if (year == null || year.equals("0")) {
            //TODO: Check info in wikipedia .... or something
            throw new RuntimeException("Year value is empty. You may want to change it manually for now.");
        }
        LOGGER.info("File year [" + year + "] built");
        return year;
    }

    /**
     * @param link
     * @param fileName
     * @return
     * @desc Needed for the request setup for youtube-dl.
     * Allow to setup error handlers, file name and number of re-tries
     */
    private YoutubeDLRequest buildYoutubeDLRequest(String link, String fileName) {
        //TODO: Automate folder creation based on OS environment
        //String directory = "/home/ao/Desktop/Test";

        //TODO:remove
       // String directory = "/home/ao/Desktop/Test1";
        String directory = "/Users/AO/Desktop/Test1";

        YoutubeDLRequest request = new YoutubeDLRequest(link, directory);

        request.setOption("ignore-errors");
        request.setOption("output", fileName);
        request.setOption("retries", 10);

        return request;
    }
}