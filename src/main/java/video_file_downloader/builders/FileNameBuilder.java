package video_file_downloader.builders;

import video_file_downloader.EpisodeDownloader;

public class FileNameBuilder {
    //TODO: Improve line below
    private final EpisodeDownloader episodeDownloader;

    public FileNameBuilder(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    public FileNameBuilder() {
        episodeDownloader = null;
    }

    /**
     * @return The counter used for setting up the current number of episodes
     */
    public String buildFileNameIfLessThanTenEpisodes() {

        String appendBeforeEpisode = "0";

        if (EpisodeDownloader.episodeCounter < 10) {
            return appendBeforeEpisode + EpisodeDownloader.episodeCounter;
        } else {
            return String.valueOf(EpisodeDownloader.episodeCounter);
        }
    }

    /**
     * @param episodeNumber
     * @param seriesTitle
     * @param seriesYear
     * @return The file name
     * @desc Build the file name
     */
    public String buildFileName(int totalNumberOfEpisodes, String episodeNumber, String seriesTitle, String seriesYear) {
        return totalNumberOfEpisodes > 1 ?
                seriesTitle + "_Episode-" + episodeNumber + "_(" + seriesYear + ").mp4" :
                seriesTitle + "_(" + seriesYear + ").mp4";
    }
}