package domain.show.repository;

import java.util.Optional;

import domain.base.repository.BaseRepository;
import domain.show.entity.Show;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ShowRepository extends BaseRepository<Show>{
    Optional<Show> findByName(String name);
}
