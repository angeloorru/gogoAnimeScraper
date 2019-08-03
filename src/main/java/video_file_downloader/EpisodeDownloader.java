package video_file_downloader;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
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
    private WelcomeScreen welcomeScreen = new WelcomeScreen();

    private String URL_HOME = welcomeScreen.getUrlForDownload();
    private int FROM_EPISODE = welcomeScreen.getNumberOfEpisodeToStartDownload();

    private static int episodeCounter = 1;

    private String URL = buildUrlForDownloadByEpisode(URL_HOME);
    private String seriesTitle = extractFileNameFromHtmlPage();
    private String seriesYear = extractYear();
    private String folderName = buildFolderNameFromHtmlPage();

    private final String directory = buildDownloadDirectory();

    private String[] endpoint;

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

        YoutubeDLResponse response = YoutubeDL.execute(
                request, (progress, etaInSeconds) -> System.out.print("Download progress at: " + progress + " % \r"));
        episodeCounter++;

        if (response.getExitCode() == 0) {
            LOGGER.info("File " + fileName + " downloaded successfully");
            pauseTheDownload(episodeNumber);
        }
    }

    /**
     * @param episodeNumber
     */
    private void pauseTheDownload(String episodeNumber) {
        if (Integer.parseInt(episodeNumber) < totalNumberOfEpisodes) {
            try {
                LOGGER.info("Pausing for 20 seconds now...");
                sleep(20000);
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
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

            Elements videoBody = doc.getElementsByClass(HtmlTagEnum.VIDEO_BODY_TAG.getValue());
            Element hrefContainer = videoBody.select(HtmlTagEnum.HREF_A_TAG.getValue()).last();

            String lastEpisodeAsString = hrefContainer.attr(HtmlTagEnum.LAST_EPISODE_TAG.getValue());

            if (lastEpisodeAsString != null) {
                lastEpisode = Integer.parseInt(lastEpisodeAsString.split("\\.")[0]);
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
        if (title != null) {
            title = title.replace(" ", "_");
            LOGGER.info("File tile [" + title + "] built");
        }
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

        Elements productName = doc.getElementsByClass(HtmlTagEnum.PRODUCT_NAME_TAG.getValue());
        String htmlTitle = productName.select(HtmlTagEnum.TITLE_TAG.getValue()).first().toString();

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
        final String RELEASED = "Released:";

        try {
            LOGGER.info("Attempting to build the file year");
            doc = Jsoup.connect(URL_HOME).get();

            Elements releasedYear = doc.getElementsByClass(HtmlTagEnum.RELEASE_YEAR_TAG.getValue());

            for (Element span : releasedYear) {
                if (span.text().contains(RELEASED)) {
                    year = span.text().replace(" ", "").replace(RELEASED, "");
                }
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        if (year == null || year.equals("0")) {
            return EpisodeDownloaderEnum.YEAR_NOT_AVAILABLE.getValue();
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

        request.setOption(YouTubeDlRequestOptionEnum.IGNORE_ERRORS.getValue());
        request.setOption(YouTubeDlRequestOptionEnum.OUTPUT.getValue(), fileName);
        request.setOption(YouTubeDlRequestOptionEnum.RETRIES.getValue(), 10);

        return request;
    }

    /**
     * @return
     * @desc Build the file path and folder name.
     */
    public String buildDownloadDirectory() {
        String workingDirectory = System.getProperty(EpisodeDownloaderEnum.USER_DIR.getValue());
        String absoluteFilePath = workingDirectory + File.separator;
        String pathToSaveDownloadedFile = null;

        if (EpisodeDownloaderEnum.OPERATING_SYSTEM.getValue().contains(EpisodeDownloaderEnum.MAC.getValue()) ||
                EpisodeDownloaderEnum.OPERATING_SYSTEM.getValue().contains(EpisodeDownloaderEnum.LINUX.getValue())) {

            pathToSaveDownloadedFile = buildPathToSaveFileInUnix(absoluteFilePath);

        } else if (EpisodeDownloaderEnum.OPERATING_SYSTEM.getValue().contains(EpisodeDownloaderEnum.WINDOWS.getValue())) {
            pathToSaveDownloadedFile = buildPathToSaveFileInWindows(absoluteFilePath);
        }

        if (pathToSaveDownloadedFile != null) {
            File pathToDestinationFolder = new File(pathToSaveDownloadedFile + folderName);

            if (createDownloadDirectory(pathToDestinationFolder, isDirectoryCreated(pathToDestinationFolder))) {
                return pathToDestinationFolder.toString();
            }
        } else {
            LOGGER.severe("Cannot recognise the current Operating System");
            System.exit(0);
        }

        return pathToSaveDownloadedFile + folderName;
    }

    /**
     *
     * @param pathToDestinationFolder
     * @return
     */
    private boolean isDirectoryCreated(File pathToDestinationFolder) {
        return pathToDestinationFolder.exists();
    }

    /**
     * @param absoluteFilePath
     * @return
     */
    private String buildPathToSaveFileInUnix(String absoluteFilePath) {
        String pathToSaveDownloadedFile;
        endpoint = absoluteFilePath.split("/");

        pathToSaveDownloadedFile = EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue() +
                endpoint[1] + EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue() +
                endpoint[2] + EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue() +
                EpisodeDownloaderEnum.DESTINATION_PATH.getValue() +
                EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue();

        return pathToSaveDownloadedFile;
    }

    /**
     * @param absoluteFilePath
     * @return
     */
    private String buildPathToSaveFileInWindows(String absoluteFilePath) {
        String pathToSaveDownloadedFile;
        endpoint = absoluteFilePath.split("\\\\");

        pathToSaveDownloadedFile = endpoint[0] + EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue() + endpoint[1] +
                EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue() +
                endpoint[2] + EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue() +
                EpisodeDownloaderEnum.DESTINATION_PATH.getValue() +
                EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue();

        return pathToSaveDownloadedFile;
    }

    /**
     * @param pathToDestinationFolder
     * @param isDirectoryCreated
     * @return
     */
    private boolean createDownloadDirectory(File pathToDestinationFolder, boolean isDirectoryCreated) {
        if (!isDirectoryCreated) {
            return pathToDestinationFolder.mkdir();
        }
        return false;
    }
}