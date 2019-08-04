package video_file_downloader;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.builders.FileNameBuilder;
import video_file_downloader.builders.FileYearBuilder;
import video_file_downloader.builders.SaveDirectoryBuilder;
import video_file_downloader.builders.YouTubeDlRequestBuilder;
import video_file_downloader.enums.HtmlTagEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;


public class EpisodeDownloader {

    private static final Logger LOGGER = Logger.getLogger(EpisodeDownloader.class.getName());
    private final SaveDirectoryBuilder saveDirectoryBuilder = new SaveDirectoryBuilder(this);
    private final FileYearBuilder fileYearBuilder = new FileYearBuilder(this);
    private final YouTubeDlRequestBuilder youTubeDlRequestBuilder = new YouTubeDlRequestBuilder(this);
    private final FileNameBuilder fileNameBuilder = new FileNameBuilder(this);
    private WelcomeScreen welcomeScreen = new WelcomeScreen();

    private String urlHome = welcomeScreen.getUrlForDownload();
    private int FROM_EPISODE = welcomeScreen.getNumberOfEpisodeToStartDownload();

    public static int episodeCounter = 1;

    private String URL = buildUrlForDownloadByEpisode(urlHome);
    private String seriesTitle = extractFileNameFromHtmlPage();
    private String seriesYear = fileYearBuilder.extractYear();
    private String folderName = buildFolderNameFromHtmlPage();

    private final String directory = saveDirectoryBuilder.buildDownloadDirectory();

    private String[] endpoint;

    private int totalNumberOfEpisodes = getTotalNumberOfEpisodeFromHtmlPage();

    public String getFolderName() {
        return folderName;
    }

    public String getUrlHome() {
        return urlHome;
    }

    public String[] getEndpoint() {
        return endpoint;
    }

    public String getDirectory() {
        return directory;
    }

    public int getTotalNumberOfEpisodes() {
        return totalNumberOfEpisodes;
    }

    public void setEndpoint(String[] endpoint) {
        this.endpoint = endpoint;
    }

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

        setEpisodeCounter(episodeNumberFromService);

        String episodeNumber = fileNameBuilder.buildFileNameIfLessThanTenEpisodes();
        String fileName = fileNameBuilder.buildFileName(String.valueOf(episodeCounter), seriesTitle, seriesYear);

        YoutubeDLRequest request = youTubeDlRequestBuilder.buildYoutubeDLRequest(link, fileName);

        LOGGER.info("For Link " + link + ": Attempting to download " +
                fileName + " [ Episode " + episodeCounter + " of " + totalNumberOfEpisodes + " ]");

        YoutubeDLResponse response = executeYouTubeDLRequest(request);

        askToPauseDownloadWhenFinishedSuccessfully(episodeNumber, fileName, response);
    }

    /**
     * @param request
     * @return
     * @throws YoutubeDLException
     */
    private YoutubeDLResponse executeYouTubeDLRequest(YoutubeDLRequest request) throws YoutubeDLException {
        YoutubeDLResponse response = YoutubeDL.execute(
                request, (progress, etaInSeconds) -> System.out.print("Download progress at: " + progress + " % \r"));
        episodeCounter++;
        return response;
    }

    /**
     * @param episodeNumber
     * @param fileName
     * @param response
     */
    private void askToPauseDownloadWhenFinishedSuccessfully(
            String episodeNumber, String fileName, YoutubeDLResponse response) {

        if (response.getExitCode() == 0) {
            LOGGER.info("File " + fileName + " downloaded successfully");
            pauseTheDownload(episodeNumber);
        }
    }

    /**
     * @param episodeNumberFromService
     */
    private void setEpisodeCounter(int episodeNumberFromService) {
        if (episodeCounter < episodeNumberFromService) {
            episodeCounter = episodeNumberFromService;
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
     * @return The total number of episodes
     */
    private int getTotalNumberOfEpisodeFromHtmlPage() {
        int lastEpisode = 0;
        Document doc;

        try {
            doc = Jsoup.connect(urlHome).get();

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
     * @return
     * @desc Builds the folder name from the episode title.
     */
    private String buildFolderNameFromHtmlPage() {
        String folderName = null;
        try {
            folderName = getInfoForTitleAndFolderFromHtmlPage();
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
            title = getInfoForTitleAndFolderFromHtmlPage();
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
    private String getInfoForTitleAndFolderFromHtmlPage() throws IOException {
        Document doc;
        String title;

        doc = Jsoup.connect(urlHome).get();

        Elements productName = doc.getElementsByClass(HtmlTagEnum.PRODUCT_NAME_TAG.getValue());
        String htmlTitle = productName.select(HtmlTagEnum.TITLE_TAG.getValue()).first().toString();

        doc = Jsoup.parse(htmlTitle);

        title = doc.body().text();
        title = title.replace(" (Dub)", "");
        title = title.replaceAll(": ", ":");
        return title;
    }
}