package services;

import com.sapher.youtubedl.YoutubeDLException;
import helpers.Helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.EpisodeDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RapidVideo {

    private static final Logger LOGGER = Logger.getLogger(RapidVideo.class.getName());

    EpisodeDownloader episodeProcessor = new EpisodeDownloader();
    Helpers helpers = new Helpers();

    private List<String> urlList = new ArrayList<>();

    /**
     * @desc Parses the gogo anime page and extracts url video links from Rapid Video
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageRapidVideo() {
        urlList = episodeProcessor.constructUrlForRequest();
        for (String url : urlList) {
            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);

                    Elements serviceName = doc.getElementsByClass("rapidvideo");
                    //TODO: Try to merge services together in the same class
                    if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                        Element link = serviceName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Rapid Video]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();
                            //TODO: Add a counter for terminating all operations if entered in deadlock
                            //Try to use the following service
                            LOGGER.info("[Rapid Video Error]: Sending job to Open Load service");
                            downloadVideoFromWebPageOpenLoad();
                        }
                    } else {
                        //TODO:Log missing episode to a text file: errors.txt
                        System.out.println("File is missing. Need a log text file");
                    }
                } catch (IOException e) {
                    LOGGER.severe("[Message]: " + e.getMessage());
                }
            }
        }
    }

    /**
     * @desc Parses the gogo anime page and extracts url video links from Open Load
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageOpenLoad() {
        urlList = episodeProcessor.constructUrlForRequest();
        for (String url : urlList) {
            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);

                    Elements serviceName = doc.getElementsByClass("open");
                    //TODO: Try to merge services together in the same class
                    if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                        Element link = serviceName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Open Load]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();
                            //TODO: Add a counter for terminating all operations if entered in deadlock
                            //Use the following service
                            downloadVideoFromWebPageRapidVideo();
                        }
                    } else {
                        //TODO:Log missing episode to a text file: errors.txt
                        System.out.println("File is missing. Need a log text file");
                    }
                } catch (IOException e) {
                    LOGGER.severe("[Message]: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
