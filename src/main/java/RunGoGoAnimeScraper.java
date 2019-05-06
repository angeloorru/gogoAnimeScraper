import services.Service;

public class RunGoGoAnimeScraper {
    public static void main(String[] args) {

        System.out.println("*****************************************************\n");
        System.out.println("Welcome to Go Go Anime Downloader Application\n");
        System.out.println("*****************************************************\n");

        Service service = new Service();
        service.extractVideoLinksFromWebPage();

        System.out.println("*******************************************");
        System.out.println("\n\n All files have been saved in the Desktop folder.");
        System.out.println("*******************************************");
    }
}
