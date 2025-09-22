package infrastructure.offers.testService.repository;

import domain.offers.testService.entity.TestService;
import domain.offers.testService.repository.TestServiceRepository;
import infrastructure.base.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestServiceRepositoryImpl extends BaseRepositoryImpl<TestService> implements TestServiceRepository{
    
}
