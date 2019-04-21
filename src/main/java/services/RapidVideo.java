package services;

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

    private static List<String> urlList = new ArrayList<>();

    /**
     * @desc Parses the gogo anime page and extracts url video links from Rapid Video
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageRapidVideo() {
        urlList =  episodeProcessor.constructUrlForRequest();
        for (String url : urlList) {
            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    Elements elementName = doc.getElementsByClass("rapidvideo");
                    //TODO: Try to merge services together in the same class
                    if (elementName != null && helpers.isEpisodeAvailable(elementName)) {
                        Element link = elementName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Rapid Video]: Sending link " + videoLink + " to youtube-dl");
                        episodeProcessor.downloadVideoWithYouTubeDl(videoLink);
                    }else{
                        urlList.remove(url);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @desc Parses the gogo anime page and extracts url video links from Open Load
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageOpenLoad() {
        for (String url : urlList) {
            if (helpers.isValidUrl(url)) {
                Document document;

                try {
                    document = Jsoup.connect(url).get();

                    Elements elementName = document.getElementsByClass("open");

                    if (elementName != null && helpers.isEpisodeAvailable(elementName)) {
                        Element link = elementName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Open Load]: Sending link " + videoLink + " to youtube-dl");
                        episodeProcessor.downloadVideoWithYouTubeDl(videoLink);
                    } else {
                        //TODO:Log to a txt file the missing episode
                        LOGGER.info("[OpenLoad]: Unable to download file. The file does not exist");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
