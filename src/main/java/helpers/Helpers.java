package helpers;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.select.Elements;

public class Helpers {
    /**
     * @param url The video link
     * @return boolean
     * @desc Check if the passed url is valid
     */
    public boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }

    /**
     * @param elements
     * @return
     * @desc Checks that the episode actually exists. If doesn't html div content should be null.
     */
    public boolean isEpisodeAvailable(Elements elements) {
        return elements.size() > 0;
    }

    /**
     * @param url
     * @return int
     * @desc Extracts the episode number from link used for resetting the counter when an episode is not found.
     */
    public int getEpisodeNumberForSettingCounter(String url) {
        String[] endpoint = url.split("/");
        int indexNumber = endpoint.length - 1;
        String number = endpoint[indexNumber];

        //Episode numbers valid up to 9999. Some series One Piece has 1000+ no of episodes
        number = number.substring(number.length()-4).replaceAll("\\D+","");

        try {
            return Integer.parseInt(number) > 0 ? Integer.parseInt(number) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
