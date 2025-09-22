package application.offers.testService.mapper;

import application.base.converter.Mapper;
import application.offers.testService.message.dto.TestServiceDTO;
import domain.offers.testService.entity.TestService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestServiceMapper implements Mapper<TestService, TestServiceDTO>{
    public TestServiceDTO toDto(TestService offer){
        return new TestServiceDTO(offer.getId(), offer.getTest());
    }
}
