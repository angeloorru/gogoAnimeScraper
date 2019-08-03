package video_file_downloader.enums;

public enum ServiceEnum {

    NUMBER_OF_SERVICES(4),
    MINIMUM_ENTRY_IN_THE_LIST(1),
    DEADLOCK(0),
    DEFAULT_FILE_MISSING(0);


    private Integer value;

    public Integer getValue() {
        return value;
    }

    ServiceEnum(Integer value) {
        this.value = value;
    }
}
