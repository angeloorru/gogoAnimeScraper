package services;

import com.sapher.youtubedl.YoutubeDLException;
import helpers.Helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import video_file_downloader.EpisodeDownloader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


public class Service {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    private EpisodeDownloader episodeProcessor = new EpisodeDownloader();
    private List<String> urlList = episodeProcessor.constructUrlForRequest();
    private Helpers helpers = new Helpers();
    private int deadlockCounter = 0;

    /**
     * @desc Parses the gogo anime page and extracts url video links from Rapid Video
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageRapidVideo() {
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

                        LOGGER.info("[Rapid Video]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                            //Shared list must be kept up to date. If done with the url, remove it.
                            iterator.remove();
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();

                            if (deadlockCounter == 3) {
                                LOGGER.severe("[Rapid Video]: Deadlock occurred");
                                System.exit(0);
                            }

                            LOGGER.info("[Rapid Video Error]: Sending job to Open Load First service");
                            downloadVideoFromWebPageOpenLoadFirst();
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

    /**
     * @desc Parses the gogo anime page and extracts url video links from Open Load first link
     * for making the request with youtube-dl.
     */
    private void downloadVideoFromWebPageOpenLoadFirst() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);

                    Elements serviceName = doc.getElementsByClass("open");

                    if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                        Element link = serviceName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Open Load First]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                            //Shared list must be kept up to date. If done with the url, remove it.
                            iterator.remove();
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();

                            if (deadlockCounter == 3) {
                                LOGGER.severe("[Open Load First]: Deadlock occurred");
                                System.exit(0);
                            }

                            LOGGER.info("[Open Load First Error]: Sending job to Open Load Second service");
                            downloadVideoFromWebPageOpenLoadSecond();
                        }
                    } else {
                        //TODO:Log missing episode to a text file: errors.txt
                        //This block skips the call to downloadVideoWithYouTubeDl() hence
                        //episodeCounter in EpisodeDownloader is ++ anyway
                        System.out.println("File is missing. Need a log text file");
                    }
                } catch (IOException e) {
                    LOGGER.severe("[Message]: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @desc Parses the gogo anime page and extracts url video links from Open Load first link
     * for making the request with youtube-dl.
     */
    private void downloadVideoFromWebPageOpenLoadSecond() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);

                    Elements serviceName = doc.getElementsByClass("open");

                    if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                        Element link = serviceName.select("a").last();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Open Load Second]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                            //Shared list must be kept up to date. If done with the url, remove it.
                            iterator.remove();
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();

                            if (deadlockCounter == 3) {
                                LOGGER.severe("[Open Load Second]: Deadlock occurred");
                                System.exit(0);
                            }

                            LOGGER.info("[Open Load Second Error]: Sending job to Stream Mango service");
                            downloadVideoFromWebPageStreamMango();
                        }
                    } else {
                        //TODO:Log missing episode to a text file: errors.txt
                        //This block skips the call to downloadVideoWithYouTubeDl() hence
                        //episodeCounter in EpisodeDownloader is ++ anyway
                        System.out.println("File is missing. Need a log text file");
                    }
                } catch (IOException e) {
                    LOGGER.severe("[Message]: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @desc Parses the gogo anime page and extracts url video links from StreamMango
     * for making the request with youtube-dl.
     */
    private void downloadVideoFromWebPageStreamMango() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            if (helpers.isValidUrl(url)) {
                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);

                    Elements serviceName = doc.getElementsByClass("streamango");

                    if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                        Element link = serviceName.select("a").first();
                        String videoLink = link.attr("data-video");

                        LOGGER.info("[Stream Mango]: Sending link " + videoLink + " to youtube-dl");
                        try {
                            episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                            //Shared list must be kept up to date. If done with the url, remove it.
                            iterator.remove();
                        } catch (YoutubeDLException e) {
                            LOGGER.severe(e.getMessage());
                            e.printStackTrace();

                            if (deadlockCounter == 3) {
                                LOGGER.severe("[Stream Mango]: Deadlock occurred");
                                System.exit(0);
                            }

                            LOGGER.info("[Stream Mango]: Sending job to Rapid Video service");
                            downloadVideoFromWebPageRapidVideo();
                        }
                    } else {
                        //TODO:Log missing episode to a text file: errors.txt
                        //This block skips the call to downloadVideoWithYouTubeDl() hence
                        //episodeCounter in EpisodeDownloader is ++ anyway
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
