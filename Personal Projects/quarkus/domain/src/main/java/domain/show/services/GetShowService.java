package domain.show.services;

import java.util.List;

import domain.show.entity.Show;
import domain.show.exception.ShowNotFoundException;
import domain.show.repository.ShowRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetShowService {
    private final ShowRepository showRepository;

    @Inject
    public GetShowService(ShowRepository showRepository){
        this.showRepository = showRepository;
    }

    public Show getShowById(Long id){
        return showRepository.get(id).orElseThrow(ShowNotFoundException::new);
    }

    public List<Show> getAllShows(){
        return showRepository.findAllEntities();
    }
}
