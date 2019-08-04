package services;

import com.sapher.youtubedl.YoutubeDLException;
import helpers.Helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.EpisodeDownloader;
import video_file_downloader.builders.SaveDirectoryBuilder;
import video_file_downloader.enums.AvailableServicesEnum;
import video_file_downloader.enums.HtmlTagEnum;
import video_file_downloader.enums.ServiceEnum;

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

    private EpisodeDownloader episodeProcessor = new EpisodeDownloader();
    private SaveDirectoryBuilder saveDirectoryBuilder = new SaveDirectoryBuilder(episodeProcessor);
    private Helpers helpers = new Helpers();

    private List<String> urlList = episodeProcessor.constructUrlForRequest();

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
                List<String> urlLinks = getServiceFromDivElement();

                if (delegateAvailableServiceToDownloadVideo(iterator, doc, episodeNumber, urlLinks)) {
                    iterator.remove();
                }

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
    private boolean delegateAvailableServiceToDownloadVideo(
            Iterator<String> iterator, Document doc, int episodeNumber, List<String> urlLinks) {

        for (String service : urlLinks) {
            Elements serviceName = doc.getElementsByClass(service);

            if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                String videoLink = getVideoLinkUrl(serviceName);
                LOGGER.info("[Service " + service + "]: Sending link " + videoLink + " to youtube-dl");
                if (sendUrlVideoDataToYouTubeDl(iterator, episodeNumber, service, videoLink)) {
                    return true;
                }
            } else {
                fileMissing++;
                writeDataToLogFile(episodeNumber, iterator);
                LOGGER.info("[Service " + service + "]: Looking for missing file in the next available service");
            }
        }
        return false;
    }

    /**
     * @param iterator
     * @param episodeNumber
     * @param service
     * @param videoLink
     * @return
     */
    private boolean sendUrlVideoDataToYouTubeDl(
            Iterator<String> iterator, int episodeNumber, String service, String videoLink) {

        try {
            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
            resetFileCounter();
            return true;
        } catch (YoutubeDLException e) {
            LOGGER.severe(e.getMessage());
            deadlockCounter++;
            fileMissing++;
            exitSystemWhenInDeadlock(service);
            writeDataToLogFile(episodeNumber, iterator);
            LOGGER.info("[Service " + service + " Error]: Sending job to next available service");
        }
        return false;
    }

    /**
     * @param service
     * @desc If after many tries the resource is not available it kills the app.
     * The service will typically throw an exception to increment the counter.
     */
    private void exitSystemWhenInDeadlock(String service) {
        if (deadlockCounter == (ServiceEnum.NUMBER_OF_SERVICES.getValue() * 2)) {
            LOGGER.severe("[" + service + "]: Deadlock occurred");
            System.exit(ServiceEnum.DEADLOCK.getValue());
        }
    }

    /**
     * @desc Once the file is downloaded the url link is removed from the list.
     * The list is removed so that we can keep track of the missing files and
     * carry on downloading the other available files.
     * The file missing counter is set back to 0.
     */
    private void resetFileCounter() {
        //Shared list must be kept up to date. If done with the url, remove it.
        if (urlList.size() >= ServiceEnum.MINIMUM_ENTRY_IN_THE_LIST.getValue()) {
            //reset counter
            fileMissing = ServiceEnum.DEFAULT_FILE_MISSING.getValue();
        }
    }

    /**
     * @param serviceName
     * @return The url link used for the download.
     * @desc Parses the below html tags to extract the video url.
     */
    private String getVideoLinkUrl(Elements serviceName) {
        String videoLink;
        Element link = serviceName.select(HtmlTagEnum.HREF_A_TAG.getValue()).first();
        videoLink = link.attr(HtmlTagEnum.DATA_VIDEO.getValue());
        return videoLink;
    }

    /**
     * @return The list of services.
     * @desc A static list of all the available Go Go Anime services
     */
    private List<String> getServiceFromDivElement() {
        List<String> services = new ArrayList<>();
        /*
         ** Ideally list should be populated by parsing html tags,
         ** but not all of them are currently supported by youtube-dl...so...
         */
        services.add(AvailableServicesEnum.RAPID_VIDEO.getValue());
        services.add(AvailableServicesEnum.STREAMANGO.getValue());
        services.add(AvailableServicesEnum.ANIME.getValue());
        services.add(AvailableServicesEnum.VIDCDN.getValue());

        return services;
    }

    /**
     * @param episodeNumber
     * @desc Log file
     */
    private void writeDataToLogFile(int episodeNumber, Iterator<String> iterator) {
        if (fileMissing >= ServiceEnum.NUMBER_OF_SERVICES.getValue()) {
            String filePath = saveDirectoryBuilder.buildDownloadDirectory();
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
