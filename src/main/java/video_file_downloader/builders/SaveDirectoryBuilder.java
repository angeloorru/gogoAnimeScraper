package video_file_downloader.builders;


import video_file_downloader.EpisodeDownloader;
import video_file_downloader.enums.EpisodeDownloaderEnum;

import java.io.File;
import java.util.logging.Logger;

public class SaveDirectoryBuilder {

    private static final Logger LOGGER = Logger.getLogger(SaveDirectoryBuilder.class.getName());

    private final EpisodeDownloader episodeDownloader;

    public SaveDirectoryBuilder(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    /**
     * @return
     * @desc Build the file path and folder name.
     */
    public String buildDownloadDirectory() {
        String workingDirectory = System.getProperty(EpisodeDownloaderEnum.USER_DIR.getValue());
        String absoluteFilePath = workingDirectory + File.separator;
        String pathToSaveDownloadedFile = getPathToSaveFile(absoluteFilePath);

        if (pathToSaveDownloadedFile != null) {
            File pathToDestinationFolder = new File(pathToSaveDownloadedFile + episodeDownloader.getFolderName());

            if (createDownloadDirectory(pathToDestinationFolder, isDirectoryCreated(pathToDestinationFolder))) {
                return pathToDestinationFolder.toString();
            }
        } else {
            LOGGER.severe("Cannot recognise the current Operating System");
            System.exit(0);
        }
        return pathToSaveDownloadedFile + episodeDownloader.getFolderName();
    }

    /**
     * @param absoluteFilePath
     * @return
     */
    private String getPathToSaveFile(String absoluteFilePath) {
        String pathToSaveDownloadedFile = null;

        if (EpisodeDownloaderEnum.OPERATING_SYSTEM.getValue().contains(EpisodeDownloaderEnum.MAC.getValue()) ||
                EpisodeDownloaderEnum.OPERATING_SYSTEM.getValue().contains(EpisodeDownloaderEnum.LINUX.getValue())) {

            pathToSaveDownloadedFile = buildPathToSaveFileInUnix(absoluteFilePath);

        } else if (EpisodeDownloaderEnum.OPERATING_SYSTEM.getValue().contains(EpisodeDownloaderEnum.WINDOWS.getValue())) {
            pathToSaveDownloadedFile = buildPathToSaveFileInWindows(absoluteFilePath);
        }
        return pathToSaveDownloadedFile;
    }

    /**
     * @param pathToDestinationFolder
     * @return
     */
    private boolean isDirectoryCreated(File pathToDestinationFolder) {
        return pathToDestinationFolder.exists();
    }

    /**
     * @param absoluteFilePath
     * @return
     */
    private String buildPathToSaveFileInUnix(String absoluteFilePath) {
        String pathToSaveDownloadedFile;
        episodeDownloader.setEndpoint(absoluteFilePath.split("/"));

        pathToSaveDownloadedFile = EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue() +
                episodeDownloader.getEndpoint()[1] + EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue() +
                episodeDownloader.getEndpoint()[2] + EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue() +
                EpisodeDownloaderEnum.DESTINATION_PATH.getValue() +
                EpisodeDownloaderEnum.SEPARATOR_UNIX.getValue();

        return pathToSaveDownloadedFile;
    }

    /**
     * @param absoluteFilePath
     * @return
     */
    private String buildPathToSaveFileInWindows(String absoluteFilePath) {
        String pathToSaveDownloadedFile;
        episodeDownloader.setEndpoint(absoluteFilePath.split("\\\\"));

        pathToSaveDownloadedFile = episodeDownloader.getEndpoint()[0] +
                EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue() + episodeDownloader.getEndpoint()[1] +
                EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue() + episodeDownloader.getEndpoint()[2] +
                EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue() + EpisodeDownloaderEnum.DESTINATION_PATH.getValue() +
                EpisodeDownloaderEnum.SEPARATOR_WINDOWS.getValue();

        return pathToSaveDownloadedFile;
    }

    /**
     * @param pathToDestinationFolder
     * @param isDirectoryCreated
     * @return
     */
    private boolean createDownloadDirectory(File pathToDestinationFolder, boolean isDirectoryCreated) {
        if (!isDirectoryCreated) {
            return pathToDestinationFolder.mkdir();
        }
        return false;
    }
}