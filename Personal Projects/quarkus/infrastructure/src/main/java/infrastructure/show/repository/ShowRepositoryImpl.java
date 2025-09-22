package infrastructure.show.repository;

import java.util.Optional;

import domain.show.entity.Show;
import domain.show.repository.ShowRepository;
import infrastructure.base.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShowRepositoryImpl extends BaseRepositoryImpl<Show> implements ShowRepository{
    
    @Override
    public Optional<Show> findByName(String name){
        return findBy("name", name);
    }
}
