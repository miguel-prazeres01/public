package domain.offers.testService.services;

import domain.offers.testService.entity.TestService;
import domain.offers.testService.repository.TestServiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestServiceCreateService {
    private final TestServiceRepository testServiceRepository;

    @Inject
    public TestServiceCreateService(TestServiceRepository testServiceRepository){
        this.testServiceRepository = testServiceRepository;
    }

    public TestService createTestService(String teste){
        TestService testService = new TestService();
        testService.setTest(teste);
        testServiceRepository.save(testService);

        return testService;
    }
}
