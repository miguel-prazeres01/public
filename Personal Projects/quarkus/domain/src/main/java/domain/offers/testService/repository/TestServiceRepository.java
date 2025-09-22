package domain.offers.testService.repository;

import domain.base.repository.BaseRepository;
import domain.offers.testService.entity.TestService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface TestServiceRepository extends BaseRepository<TestService>{
    
}
