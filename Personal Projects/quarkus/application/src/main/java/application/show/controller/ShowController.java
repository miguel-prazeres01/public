package application.show.controller;

import application.show.message.request.GetShowRequest;
import application.show.message.response.GetAllShowsResponse;
import application.show.message.response.GetShowResponse;

public interface ShowController {

    GetShowResponse getShow(GetShowRequest getShowRequest);

    GetAllShowsResponse getAllShows();
}
