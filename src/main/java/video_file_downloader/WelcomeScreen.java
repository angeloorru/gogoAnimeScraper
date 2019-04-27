package video_file_downloader;

import helpers.Helpers;

import java.util.Scanner;

public class WelcomeScreen {

    private Helpers helpers = new Helpers();

    String getUrlForDownload() {
        String urlForDownload;

        while (true) {
            System.out.println("Please enter Url for download:\n");
            Scanner scanner = new Scanner(System.in);

            urlForDownload = scanner.nextLine();
            urlForDownload = urlForDownload.replace(" ", "");

            if (helpers.isValidUrl(urlForDownload)) {
                if (urlForDownload.contains("/category/")) {
                    return urlForDownload;
                } else {
                    System.out.println("\nNot a GoGoAnime URL.\nExample of correct url is: https://www2.gogoanime.io/category/....\n");
                    System.out.println("The correct URL, can be seen in the web page where all the episodes are listed.\n");
                }
            } else {
                System.out.println("The url is not valid. Please enter a valid Url.\n");
            }
        }
    }

    int getNumberOfEpisodeToStartDownload() {
        int episodeNumber;

        while (true) {
            System.out.println("From which episode do you want to start the download?:\n");
            Scanner scanner = new Scanner(System.in);
            try {
                episodeNumber = scanner.nextInt();

                return episodeNumber;
            } catch (Exception e) {
                System.out.println("This is not a valid number. Please enter a valid number.\n");
            }
        }
    }
}
