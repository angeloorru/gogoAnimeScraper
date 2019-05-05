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

    /*
     * @desc Parses the gogo anime page and extracts url video links from Open Load first link
     * for making the request with youtube-dl.
     */
   /* public void downloadVideoFromWebPageOpenLoadFirst() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

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
                        if (urlList.size() > 1) {
                            iterator.remove();
                        }
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
                    //This block skips the call to downloadVideoWithYouTubeDl() hence
                    //episodeCounter in EpisodeDownloader is ++ anyway
                    System.out.println("File is missing. Need a log text file");
                }
            } catch (IOException e) {
                LOGGER.severe("[Message]: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }*/

    /*
     * @desc Parses the gogo anime page and extracts url video links from Open Load first link
     * for making the request with youtube-dl.
     */
   /* private void downloadVideoFromWebPageOpenLoadSecond() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

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
                        if (urlList.size() > 1) {
                            iterator.remove();
                        }
                    } catch (YoutubeDLException e) {
                        LOGGER.severe(e.getMessage());
                        e.printStackTrace();

                        if (deadlockCounter == 3) {
                            LOGGER.severe("[Open Load Second]: Deadlock occurred");
                            System.exit(0);
                        }

                        LOGGER.info("[Open Load Second Error]: Sending job to Rapid Video service");
                        downloadVideoFromWebPageRapidVideo();
                    }
                } else {
                    //This block skips the call to downloadVideoWithYouTubeDl() hence
                    //episodeCounter in EpisodeDownloader is ++ anyway
                    System.out.println("File is missing. Need a log text file");
                }
            } catch (IOException e) {
                LOGGER.severe("[Message]: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }*/

    /**
     * @desc Parses the gogo anime page and extracts url video links from Rapid Video
     * for making the request with youtube-dl.
     */
    public void downloadVideoFromWebPageRapidVideo() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            Document doc;

            try {
                doc = Jsoup.connect(url).get();
                int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);
                String videoLink;

                Elements serviceName = doc.getElementsByClass("rapidvideo");

                if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                    Element link = serviceName.select("a").first();
                    videoLink = link.attr("data-video");

                    LOGGER.info("[Rapid Video]: Sending link " + videoLink + " to youtube-dl");
                    try {
                        episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                        //Shared list must be kept up to date. If done with the url, remove it.
                        if (urlList.size() >= 1) {
                            iterator.remove();
                            //reset counter
                            fileMissing = 0;
                        }
                    } catch (YoutubeDLException e) {
                        LOGGER.severe(e.getMessage());

                        if (deadlockCounter == (NUMBER_OF_SERVICES * 2)) {
                            LOGGER.severe("[Rapid Video]: Deadlock occurred");
                            System.exit(0);
                        }

                        LOGGER.info("[Rapid Video Error]: Sending job to Stream Mango service");
                        deadlockCounter++;
                        downloadVideoFromWebPageStreamMango();
                    }
                } else {
                    if (fileMissing == NUMBER_OF_SERVICES) {
                        writeDataToLogFile(episodeNumber);
                        iterator.remove();
                    }
                    LOGGER.info("[Rapid Video]: Looking for missing file into Stream Mango service");
                    fileMissing++;
                    downloadVideoFromWebPageStreamMango();
                }
            } catch (IOException e) {
                LOGGER.severe("[Rapid Video Message]: " + e.getMessage());
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

            Document doc;

            try {
                doc = Jsoup.connect(url).get();
                int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);
                String videoLink;

                Elements serviceName = doc.getElementsByClass("streamango");

                if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                    Element link = serviceName.select("a").first();
                    videoLink = link.attr("data-video");

                    LOGGER.info("[Stream Mang]: Sending link " + videoLink + " to youtube-dl");
                    try {
                        episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                        //Shared list must be kept up to date. If done with the url, remove it.
                        if (urlList.size() >= 1) {
                            iterator.remove();
                            //reset counter
                            fileMissing = 0;
                        }
                    } catch (YoutubeDLException e) {
                        LOGGER.severe(e.getMessage());

                        if (deadlockCounter == (NUMBER_OF_SERVICES * 2)) {
                            LOGGER.severe("[Stream Mang]: Deadlock occurred");
                            System.exit(0);
                        }

                        LOGGER.info("[Stream Mang Error]: Sending job to Anime service");
                        deadlockCounter++;
                        downloadVideoFromWebPageAnime();
                    }
                } else {
                    if (fileMissing == NUMBER_OF_SERVICES) {
                        writeDataToLogFile(episodeNumber);
                        iterator.remove();
                    }
                    LOGGER.info("[Stream Mango Error]: Looking for missing file into Anime service");
                    fileMissing++;
                    downloadVideoFromWebPageAnime();
                }
            } catch (IOException e) {
                LOGGER.severe("[Stream Mango Message]: " + e.getMessage());
            }
        }
    }


    /**
     * @desc Parses the gogo anime page and extracts url video links from Anime
     * for making the request with youtube-dl.
     */
    private void downloadVideoFromWebPageAnime() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            Document doc;

            try {
                doc = Jsoup.connect(url).get();
                int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);
                String videoLink;

                Elements serviceName = doc.getElementsByClass("anime");

                if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                    Element link = serviceName.select("a").first();
                    videoLink = link.attr("data-video");

                    LOGGER.info("[Anime]: Sending link " + videoLink + " to youtube-dl");
                    try {
                        episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                        //Shared list must be kept up to date. If done with the url, remove it.
                        if (urlList.size() >= 1) {
                            iterator.remove();
                            //reset counter
                            fileMissing = 0;
                        }
                    } catch (YoutubeDLException e) {
                        LOGGER.severe(e.getMessage());

                        if (deadlockCounter == (NUMBER_OF_SERVICES * 2)) {
                            LOGGER.severe("[Anime]: Deadlock occurred");
                            System.exit(0);
                        }

                        LOGGER.info("[Anime Error]: Sending job to VidCdn service");
                        deadlockCounter++;
                        downloadVideoFromWebPageVidCdn();
                    }
                } else {
                    if (fileMissing == NUMBER_OF_SERVICES) {
                        writeDataToLogFile(episodeNumber);
                        iterator.remove();
                    }
                    LOGGER.info("[Anime Error]: Looking for missing file into VidCdn service");
                    fileMissing++;
                    downloadVideoFromWebPageVidCdn();
                }
            } catch (IOException e) {
                LOGGER.severe("[Anime Message]: " + e.getMessage());
            }
        }
    }

    /**
     * @desc Parses the gogo anime page and extracts url video links from VidCdn
     * for making the request with youtube-dl.
     */
    private void downloadVideoFromWebPageVidCdn() {
        Iterator<String> iterator = urlList.iterator();

        while (iterator.hasNext()) {
            String url = iterator.next();

            Document doc;

            try {
                doc = Jsoup.connect(url).get();
                int episodeNumber = helpers.getEpisodeNumberForSettingCounter(url);
                String videoLink;

                Elements serviceName = doc.getElementsByClass("vidcdn");

                if (serviceName != null && helpers.isEpisodeAvailable(serviceName)) {
                    Element link = serviceName.select("a").first();
                    videoLink = link.attr("data-video");

                    LOGGER.info("[VidCdn]: Sending link " + videoLink + " to youtube-dl");
                    try {
                        episodeProcessor.downloadVideoWithYouTubeDl(videoLink, episodeNumber);
                        //Shared list must be kept up to date. If done with the url, remove it.
                        if (urlList.size() >= 1) {
                            iterator.remove();
                            //reset counter
                            fileMissing = 0;
                        }
                    } catch (YoutubeDLException e) {
                        LOGGER.severe(e.getMessage());

                        if (deadlockCounter == (NUMBER_OF_SERVICES * 2)) {
                            LOGGER.severe("[VidCdn]: Deadlock occurred");
                            System.exit(0);
                        }

                        LOGGER.info("[VidCdn Error]: Sending job to Rapid Video service");
                        deadlockCounter++;
                        downloadVideoFromWebPageRapidVideo();
                    }
                } else {
                    if (fileMissing == NUMBER_OF_SERVICES) {
                        writeDataToLogFile(episodeNumber);
                        iterator.remove();
                    }
                    LOGGER.info("[VidCdn Error]: Looking for missing file into Rapid Video service");
                    fileMissing++;
                    downloadVideoFromWebPageRapidVideo();
                }
            } catch (IOException e) {
                LOGGER.severe("[VidCdn Message]: " + e.getMessage());
            }
        }
    }

    /**
     * @param episodeNumber
     * @desc Log file
     */
    private void writeDataToLogFile(int episodeNumber) {
        String filePath = episodeProcessor.buildDownloadDirectory();
        String text = "Episode " + episodeNumber + " was not found.\n";
        try {
            Files.write(Paths.get(filePath + "/logFile.log"), text.getBytes(UTF_8), CREATE, APPEND);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }
}
