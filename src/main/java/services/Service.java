package services;

import com.sapher.youtubedl.YoutubeDLException;
import helpers.Helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.EpisodeDownloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;


public class Service {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
    private static final int NUMBER_OF_SERVICES = 4;

    private EpisodeDownloader episodeProcessor = new EpisodeDownloader();
    private List<String> urlList = episodeProcessor.constructUrlForRequest();
    private Helpers helpers = new Helpers();
    private int deadlockCounter = 0;
    private int fileMissing = 0;

    /**
     * @desc Extracts the url links of the videos.
     */
    public void extractVideoLinksFromWebPage() {
        Iterator<String> iterator = urlList.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();

            Document doc;

            try {
                doc = Jsoup.connect(url).get();
                int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);
                List<String> urlLinks = getDivElementsFromService();

                sendUrlVideoDataToYouTubeDl(iterator, doc, episodeNumber, urlLinks);

            } catch (IOException e) {
                LOGGER.severe("[Service Message]: " + e.getMessage());
            }
        }
    }

    /**
     * @param iterator
     * @param doc
     * @param episodeNumber
     * @param urlLinks
     * @desc Accepts a video url to make the request to yoytube-dl.
     */
    private void sendUrlVideoDataToYouTubeDl(Iterator<String> iterator, Document doc,
                                             int episodeNumber, List<String> urlLinks) {

        for (String service : urlLinks) {
            Elements serviceName = doc.getElementsByClass("rapidvideo");

            if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                String videoLink = getVideoLinkUrl(serviceName);
                LOGGER.info("[" + service + "]: Sending link " + videoLink + " to youtube-dl");
                try {
                    episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                    removeUrlAndResetFileCounter(iterator);
                } catch (YoutubeDLException e) {
                    LOGGER.severe(e.getMessage());
                    deadlockCounter++;
                    exitSystemWhenInDeadlock(service);
                    LOGGER.info("[" + service + " Error]: Sending job to next available service");
                }
            } else {
                fileMissing++;
                writeDataToLogFile(episodeNumber, iterator);
                LOGGER.info("[" + service + "]: Looking for missing file in the next available service");
            }
        }
    }

    /**
     * @param service
     * @desc If after many tries the resource is not available it kills the app.
     * The service will typically throw an exception to increment the counter.
     */
    private void exitSystemWhenInDeadlock(String service) {
        if (deadlockCounter == (NUMBER_OF_SERVICES * 2)) {
            LOGGER.severe("[" + service + "]: Deadlock occurred");
            System.exit(0);
        }
    }

    /**
     * @param iterator
     * @desc Once the file is downloaded the url link is removed from the list.
     * The list is removed so that we can keep track of the missing files and
     * carry on downloading the other available files.
     * The file missing counter is set back to 0.
     */
    private void removeUrlAndResetFileCounter(Iterator<String> iterator) {
        //Shared list must be kept up to date. If done with the url, remove it.
        if (urlList.size() >= 1) {
            iterator.remove();
            //reset counter
            fileMissing = 0;
        }
    }

    /**
     * @param serviceName
     * @return The url link used for the download.
     * @desc Parses the below html tags to extract the video url.
     */
    private String getVideoLinkUrl(Elements serviceName) {
        String videoLink;
        Element link = serviceName.select("a").first();
        videoLink = link.attr("data-video");
        return videoLink;
    }

    /**
     * @return The list of services.
     * @desc A static list of all the available Go Go Anime services
     */
    private List<String> getDivElementsFromService() {
        List<String> services = new ArrayList<>();
        /*
         ** Ideally list should be populated by parsing html tags,
         ** but not all of them are currently supported by youtube-dl
         */
        services.add("rapidvideo");
        services.add("streamango");
        services.add("anime");
        services.add("vidcdn");

        return services;
    }

    /**
     * @param episodeNumber
     * @desc Log file
     */
    private void writeDataToLogFile(int episodeNumber, Iterator<String> iterator) {
        if (fileMissing == NUMBER_OF_SERVICES) {
            String filePath = episodeProcessor.buildDownloadDirectory();
            String text = "Episode " + episodeNumber + " was not found.\n";
            try {
                Files.write(Paths.get(filePath + "/logFile.log"), text.getBytes(UTF_8), CREATE, APPEND);
                iterator.remove();
                fileMissing = 0;
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }
}
