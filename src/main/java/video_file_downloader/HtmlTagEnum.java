package video_file_downloader;

public enum HtmlTagEnum {

    VIDEO_BODY_TAG("anime_video_body"),
    HREF_A_TAG("a"),
    LAST_EPISODE_TAG("ep_end"),
    PRODUCT_NAME_TAG("anime_info_body_bg"),
    TITLE_TAG("h1"),
    RELEASE_YEAR_TAG("type"),
    ;


    private String value;

    public String getValue() {
        return value;
    }

    HtmlTagEnum(String value) {
        this.value = value;
    }
}
