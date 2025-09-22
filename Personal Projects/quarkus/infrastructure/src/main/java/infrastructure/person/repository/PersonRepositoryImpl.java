package infrastructure.person.repository;

import domain.person.entity.Person;
import domain.person.repository.PersonRepository;
import infrastructure.base.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonRepositoryImpl extends BaseRepositoryImpl<Person> implements PersonRepository{
    
}
