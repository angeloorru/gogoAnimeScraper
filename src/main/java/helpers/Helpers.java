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
     * @return
     * @desc Extracts the episode number from link used for resetting the counter when an episode is not found.
     */
    public int getEpisodeNumberForSettingCounter(String url) {
        String[] endpoint = url.split("/");
        int indexNumber = endpoint.length - 1;
        String number = endpoint[indexNumber];

        number = number.replaceAll("\\D+", "");

        try {
            return Integer.valueOf(number);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
