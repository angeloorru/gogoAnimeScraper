package services;

import com.sapher.youtubedl.YoutubeDLException;
import helpers.Helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.EpisodeDownloader;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class RapidVideo {

    private static final Logger LOGGER = Logger.getLogger(RapidVideo.class.getName());

    EpisodeDownloader episodeDownloader = new EpisodeDownloader();
    Helpers helpers = new Helpers();


    /**
     * @desc Parses the gogo anime page and extracts url video links from Rapid Video
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageRapidVideo() {
            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    Elements productName = doc.getElementsByClass("rapidvideo");
                    //TODO: Try to merge services together in the same class
                    if (productName != null && helpers.isEpisodeAvailable(productName)) {
                        Element link = productName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("Trying to download using Rapid Video Service");
                        episodeDownloader.downloadVideoWithYouTubeDl(videoLink);

                        //LOGGER.info("Sending job to Open Load Service");
                        //downloadVideoFromWebPageOpenLoad();
                    }
                     /*else {
                        //TODO:Log to a txt file the missing episode
                        LOGGER.severe("[RapidVideo]: Unable to download file." + " Episode number " +
                                EpisodeDownloader.episodeCounter + " does not exist");
                        //Skip episode
                        EpisodeDownloader.episodeCounter++;
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
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

                    Elements productName = document.getElementsByClass("open");

                    if (helpers.isEpisodeAvailable(productName)) {
                        Element link = productName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[OpenLoad]: Trying to download using Open Load Service");
                        episodeDownloader.downloadVideoWithYouTubeDl(videoLink);
                    }/*else{
                        //TODO:Log to a txt file the missing episode
                        LOGGER.info("[OpenLoad]: Unable to download file. The file does not exist");
                    }*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
