package video_file_downloader.enums;

public enum AvailableServicesEnum {

    RAPID_VIDEO("rapidvideo"),
    STREAMANGO("streamango"),
    ANIME("anime"),
    VIDCDN("vidcdn");

    private String value;

    public String getValue() {
        return value;
    }

    AvailableServicesEnum(String value) {
        this.value = value;
    }
}
