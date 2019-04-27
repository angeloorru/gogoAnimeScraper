import services.Service;
import video_file_downloader.WelcomeScreen;

public class RunGoGoAnimeScraper {
    public static void main(String[] args) {

        System.out.println("*****************************************************\n");
        System.out.println("Welcome to Go Go Anime Downloader Application\n");
        System.out.println("*****************************************************\n");

        Service service = new Service();
        service.downloadVideoFromWebPageRapidVideo();

        System.out.println("*******************************************");
        System.out.println("\n\n All files saved in the Desktop folder.");
        System.out.println("*******************************************");
    }
}
