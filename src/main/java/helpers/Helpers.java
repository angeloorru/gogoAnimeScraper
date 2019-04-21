package helpers;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.select.Elements;

import java.util.logging.Logger;

public class Helpers {

    private static final Logger LOGGER = Logger.getLogger(Helpers.class.getName());

    /**
     * @param url The video link
     * @return boolean
     * @desc Check if the passed url is valid
     */
    public boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(url)) {
            return true;
        }
        LOGGER.severe("The URL is not valid");
        return false;
    }

    /**
     * @param elements
     * @return
     * @desc Checks that the episode actually exists. If doesn't html div content should be null.
     */
    public boolean isEpisodeAvailable(Elements elements) {
        if (elements.size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * @param url
     * @return
     * @desc Extracts the episode number from link used for resetting the counter when an episode is not found.
     */
    public int getEpisodeNumberForSettingCounter(String url) {
        return Integer.valueOf(url.substring(url.length() - 2));
    }
}
