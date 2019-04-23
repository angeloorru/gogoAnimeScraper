package services;

import helpers.Helpers;
import video_file_downloader.EpisodeDownloader;

import java.util.List;

abstract class Service {

    EpisodeDownloader downloader = new EpisodeDownloader();
    List<String> urlList = downloader.constructUrlForRequest();
    Helpers helpers = new Helpers();

    abstract void downloadVideoUsingService();
}
