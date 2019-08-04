package video_file_downloader.builders;

import com.sapher.youtubedl.YoutubeDLRequest;
import video_file_downloader.EpisodeDownloader;
import video_file_downloader.enums.YouTubeDlRequestOptionEnum;

public class YouTubeDlRequestBuilder {
    private final EpisodeDownloader episodeDownloader;

    public YouTubeDlRequestBuilder(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    /**
     * @param link
     * @param fileName
     * @return
     * @desc Needed for the request setup for youtube-dl.
     * Allow to setup error handlers, file name and number of re-tries
     */
    public YoutubeDLRequest buildYoutubeDLRequest(String link, String fileName) {
        YoutubeDLRequest request = new YoutubeDLRequest(link, episodeDownloader.getDirectory());

        request.setOption(YouTubeDlRequestOptionEnum.IGNORE_ERRORS.getValue());
        request.setOption(YouTubeDlRequestOptionEnum.OUTPUT.getValue(), fileName);
        request.setOption(YouTubeDlRequestOptionEnum.RETRIES.getValue(), 10);

        return request;
    }
}