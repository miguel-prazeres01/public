package application.show.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import application.show.mapper.ShowMapper;
import application.show.message.request.CreateShowRequest;
import application.show.message.request.GetShowRequest;
import application.show.message.response.CreateShowResponse;
import application.show.message.response.GetAllShowsResponse;
import application.show.message.response.GetShowResponse;
import domain.show.entity.Show;
import domain.show.services.CreateShowService;
import domain.show.services.GetShowService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShowManager {
    
    private final ShowMapper showMapper;
    private final CreateShowService createShowService;
    private final GetShowService getShowService;

    @Inject
    public ShowManager(ShowMapper showMapper, CreateShowService createShowService, GetShowService getShowService){
        this.showMapper = showMapper;
        this.createShowService = createShowService;
        this.getShowService = getShowService;
    }

    
    public CreateShowResponse createShow(CreateShowRequest createShowRequest){
        String name = createShowRequest.getName();
        BigDecimal price = createShowRequest.getPrice();
        LocalDateTime startDate = createShowRequest.getDateTime();
        Long rowNumber = createShowRequest.getRowsNumber();
        Long columnNumber = createShowRequest.getColumnNumber();

        Show show = createShowService.createShow(name, startDate, price, rowNumber, columnNumber);

        return new CreateShowResponse(showMapper.toDto(show));
    }

    public GetAllShowsResponse getAllShows(){
        List<Show> shows = getShowService.getAllShows();

        return new GetAllShowsResponse(showMapper.toDto(shows));
    }

    public GetShowResponse getShow(GetShowRequest getShowRequest){
        Long showId = getShowRequest.getShowId();

        Show show = getShowService.getShowById(showId);

        return new GetShowResponse(showMapper.toDto(show));
    }
}
