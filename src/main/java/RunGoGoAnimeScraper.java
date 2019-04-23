import services.RapidVideo;

public class RunGoGoAnimeScraper {
    public static void main(String[] args) {
        RapidVideo rapidVideoService = new RapidVideo();
        rapidVideoService.downloadVideoUsingService();
    }
}
