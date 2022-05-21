package com.monisha.moviecentralservice;

interface movieCentralService {
    Bundle getmovieList();
    Bundle getmovieInfo(int movieId);
    String getmovieUrl(int movieId);
    Bundle getmovieThumbnail(int movieId);
}