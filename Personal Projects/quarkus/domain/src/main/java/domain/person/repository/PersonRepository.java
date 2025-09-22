package domain.person.repository;

import domain.base.repository.BaseRepository;
import domain.person.entity.Person;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface PersonRepository extends BaseRepository<Person>{
    
}
