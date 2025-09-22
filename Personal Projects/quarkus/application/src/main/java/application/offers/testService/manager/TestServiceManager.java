package application.offers.testService.manager;


import application.offers.testService.mapper.TestServiceMapper;
import application.offers.testService.message.response.TestServiceResponse;
import domain.offers.testService.entity.TestService;
import domain.offers.testService.services.TestServiceCreateService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestServiceManager {
    private TestServiceCreateService testServiceCreateService;
    private TestServiceMapper testServiceMapper;

    @Inject 
    public TestServiceManager(TestServiceCreateService testServiceCreateService, TestServiceMapper testServiceMapper){
        this.testServiceCreateService = testServiceCreateService;
        this.testServiceMapper = testServiceMapper;
    }

    public TestServiceResponse createTestService(String teste){
        TestService testService = testServiceCreateService.createTestService(teste);

        return new TestServiceResponse(testServiceMapper.toDto(testService));
    }
}
