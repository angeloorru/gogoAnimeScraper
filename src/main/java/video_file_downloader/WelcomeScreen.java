package video_file_downloader;

import helpers.Helpers;

import java.util.Scanner;

class WelcomeScreen {

    private final Helpers helpers = new Helpers();

    protected String getUrlForDownload() {
        String urlForDownload;

        while (true) {
            System.out.println("Please enter Url for download:\n");
            Scanner scanner = new Scanner(System.in);

            urlForDownload = scanner.nextLine();
            urlForDownload = urlForDownload.replace(" ", "");

            if (helpers.isValidUrl(urlForDownload)) {
                if (validateUrlForDownload(urlForDownload)) return urlForDownload;
            } else {
                System.out.println("The url is not valid. Please enter a valid Url.\n");
            }
        }
    }

    protected boolean validateUrlForDownload(String urlForDownload) {
        if (urlForDownload.contains("/category/")) {
            return true;
        } else {
            System.out.println("\nNot the correct GoGoAnime URL.\nExample of correct url is: https://www2.gogoanime.io/category/....\n");
            System.out.println("The correct URL, can be seen in the web page where all the episodes are listed.\n");
        }
        return false;
    }

    protected int askForStartingNumberOfEpisode() {
        int episodeNumber;

        while (true) {
            System.out.println("From which episode do you want to start the download?:\n");

            Scanner scanner = new Scanner(System.in);
            episodeNumber = scanner.nextInt();

            if (getNumberOfEpisodeToStartDownload(episodeNumber)) return episodeNumber;
        }
    }

    protected boolean getNumberOfEpisodeToStartDownload(int episodeNumber) {
        if (episodeNumber > 0) {
            return true;
        } else {
            System.out.println("This is not a valid number. Please enter a valid number.\n");
        }
        return false;
    }
}
