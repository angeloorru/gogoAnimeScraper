package video_file_downloader.enums;

public enum YouTubeDlRequestOptionEnum {

    IGNORE_ERRORS("ignore-errors"),
    OUTPUT("output"),
    RETRIES("retries");


    private String value;

    public String getValue() {
        return value;
    }

    YouTubeDlRequestOptionEnum(String value) {
        this.value = value;
    }
}
