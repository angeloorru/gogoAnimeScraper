package video_file_downloader;

import com.sapher.youtubedl.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;


public class EpisodeDownloader {

    private static final Logger LOGGER = Logger.getLogger(EpisodeDownloader.class.getName());
    WelcomeScreen welcomeScreen = new WelcomeScreen();

    private String URL_HOME = welcomeScreen.getUrlForDownload();
    private int FROM_EPISODE = welcomeScreen.getNumberOfEpisodeToStartDownload();

    private static final String DESTINATION_PATH = "Desktop";
    private static final String SEPARATOR_UNIX = "/";
    private static final String SEPARATOR_WINDOWS = "\\";
    private static final String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();

    private static int episodeCounter = 1;

    private String URL = buildUrlForDownloadByEpisode(URL_HOME);
    private String seriesTitle = extractFileNameFromHtmlPage();
    private String seriesYear = extractYear();
    private String folderName = buildFolderNameFromHtmlPage();
    private final String directory = buildDownloadDirectory();

    private int totalNumberOfEpisodes = getTotalNumberOfEpisodeFromHtmlPage();

    /**
     * @return Url for the request
     */
    public List<String> constructUrlForRequest() {
        List<String> url = new ArrayList<>();

        LOGGER.info("Retrieving number of episodes");
        int totalNumberOfEpisodes = getTotalNumberOfEpisodeFromHtmlPage();
        LOGGER.info("Found " + totalNumberOfEpisodes + " episodes");

        for (int i = FROM_EPISODE; i <= totalNumberOfEpisodes; i++) {
            String episodesUrl = URL + i;
            url.add(episodesUrl);
        }
        return url;
    }

    /**
     * @param link Used for the logger and YouTube-DL request only
     * @desc Uses youtube-dl java for downloading the video.
     */
    public void downloadVideoWithYouTubeDl(String link, int episodeNumberFromService) throws YoutubeDLException {

        if (episodeCounter < episodeNumberFromService) {
            episodeCounter = episodeNumberFromService;
        }

        String episodeNumber = buildFileNameIfLessThanTenEpisodes();
        String fileName = buildFileName(String.valueOf(episodeCounter), seriesTitle, seriesYear);

        YoutubeDLRequest request = buildYoutubeDLRequest(link, fileName);

        LOGGER.info("For Link " + link + ": Attempting to download " +
                fileName + " [ Episode " + episodeCounter + " of " + totalNumberOfEpisodes + " ]");

        //YoutubeDLResponse response = YoutubeDL.execute(request);

        YoutubeDLResponse response = YoutubeDL.execute(
                request, (progress, etaInSeconds) -> LOGGER.info("Download Progress: " + progress + " %"));
        episodeCounter++;

        if (response.getExitCode() == 0) {
            LOGGER.info("File " + fileName + " downloaded successfully");
            if (Integer.valueOf(episodeNumber) < totalNumberOfEpisodes) {
                try {
                    LOGGER.info("Pausing for 20 seconds now...");
                    sleep(20000);
                } catch (InterruptedException e) {
                    LOGGER.severe(e.getMessage());
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
        return totalNumberOfEpisodes > 1 ?
                seriesTitle + "_Episode-" + episodeNumber + "_(" + seriesYear + ").mp4" :
                seriesTitle + "_(" + seriesYear + ").mp4";
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
            Element hrefContainer = videoBody.select("a").last();

            String lastEpisodeAsString = hrefContainer.attr("ep_end");

            if (lastEpisodeAsString != null) {
                lastEpisode = Integer.valueOf(lastEpisodeAsString.split("\\.")[0]);
            }

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
     * @desc Builds the folder name from the episode title.
     */
    private String buildFolderNameFromHtmlPage() {
        String folderName = null;
        try {
            folderName = getInfoForTitleAndFolder();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        LOGGER.info("Attempting to build the folder name");
        folderName = folderName + " (" + seriesYear + ")";
        LOGGER.info("Folder name [" + folderName + "] built");

        return folderName;
    }

    /**
     * @return
     * @desc Extracts the file name by scraping the html page
     */
    private String extractFileNameFromHtmlPage() {
        String title = null;
        try {
            title = getInfoForTitleAndFolder();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        LOGGER.info("Attempting to build the file title");
        title = title.replace(" ", "_");
        LOGGER.info("File tile [" + title + "] built");
        return title;
    }

    /**
     * @return
     * @throws IOException
     * @desc Scraps tags content useful for building the file name and folder's name
     */
    private String getInfoForTitleAndFolder() throws IOException {
        Document doc;
        String title;

        doc = Jsoup.connect(URL_HOME).get();

        Elements productName = doc.getElementsByClass("anime_info_body_bg");
        String htmlTitle = productName.select("h1").first().toString();

        doc = Jsoup.parse(htmlTitle);

        title = doc.body().text();
        title = title.replace(" (Dub)", "");
        title = title.replaceAll(": ", ":");
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
            LOGGER.severe(e.getMessage());
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
        YoutubeDLRequest request = new YoutubeDLRequest(link, directory);

        request.setOption("ignore-errors");
        request.setOption("output", fileName);
        request.setOption("retries", 10);

        return request;
    }

    /**
     * @return
     * @desc Build the file path and folder name.
     */
    private String buildDownloadDirectory() {
        String workingDirectory = System.getProperty("user.dir");
        String absoluteFilePath = workingDirectory + File.separator;
        String[] endpoint;
        String pathToSaveDownloadedFile;

        if (OPERATING_SYSTEM.contains("mac") || OPERATING_SYSTEM.contains("linux")) {
            endpoint = absoluteFilePath.split("/");
            pathToSaveDownloadedFile = SEPARATOR_UNIX + endpoint[1] + SEPARATOR_UNIX +
                    endpoint[2] + SEPARATOR_UNIX + DESTINATION_PATH + SEPARATOR_UNIX;
        } else {
            endpoint = absoluteFilePath.split("\\\\");
            pathToSaveDownloadedFile = SEPARATOR_WINDOWS + endpoint[1] + SEPARATOR_WINDOWS +
                    endpoint[2] + SEPARATOR_WINDOWS + DESTINATION_PATH + SEPARATOR_WINDOWS;
        }

        File destinationFolder = new File(pathToSaveDownloadedFile + folderName);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }
        return destinationFolder.toString();
    }
}