import services.Rapid;

public class RunGoGoAnimeScraper {
    public static void main(String[] args) {
        Rapid rapidVideoService = new Rapid();
        rapidVideoService.downloadVideoUsingService();
    }
}
