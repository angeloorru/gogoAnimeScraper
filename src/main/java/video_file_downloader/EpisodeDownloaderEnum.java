package video_file_downloader;

public enum EpisodeDownloaderEnum {
    DESTINATION_PATH("Desktop"),
    SEPARATOR_UNIX("/"),
    SEPARATOR_WINDOWS("\\"),
    OPERATING_SYSTEM(System.getProperty("os.name").toLowerCase()),
    MAC("mac"),
    LINUX("linux"),
    WINDOWS("windows"),
    USER_DIR("user.dir"),
    YEAR_NOT_AVAILABLE("0000");

    private String value;

    public String getValue() {
        return value;
    }

    EpisodeDownloaderEnum(String value) {
        this.value = value;
    }
}