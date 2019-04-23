package services;

import com.sapher.youtubedl.YoutubeDLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

public class RapidVideo extends Service {
    private static final Logger LOGGER = Logger.getLogger(RapidVideo.class.getName());

    private int deadlockCounter;

    @Override
    public void downloadVideoUsingService() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);

                    Elements serviceName = doc.getElementsByClass("rapidvideo");

                    if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                        Element link = serviceName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[RapidVideo Video]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            downloader.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                            //Shared list must be kept up to date. If done with the url, remove it.
                            iterator.remove();
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();
                            deadlockCounter++;

                            if(deadlockCounter == 3){
                                LOGGER.severe("[Rapid Video]: Deadlock occurred");
                                System.exit(0);
                            }

                            LOGGER.info("[RapidVideo Video Error]: Sending job to Open Load service");
                            OpenLoad openLoad = new OpenLoad();
                            openLoad.downloadVideoUsingService();
                        }
                    } else {
                        //TODO:Log missing episode to a text file: errors.txt
                        //This block skips the call to downloadVideoWithYouTubeDl() hence
                        //episodeCounter in EpisodeDownloader is ++ anyway
                        System.out.println("File is missing. Need a log text file");
                    }
                } catch (IOException e) {
                    LOGGER.severe("[Message]: " + e.getMessage());
                }
            }
        }
    }
}
