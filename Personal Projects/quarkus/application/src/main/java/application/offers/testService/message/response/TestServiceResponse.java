package application.offers.testService.message.response;

import application.offers.testService.message.dto.TestServiceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TestServiceResponse {
    private TestServiceDTO testService;
}
