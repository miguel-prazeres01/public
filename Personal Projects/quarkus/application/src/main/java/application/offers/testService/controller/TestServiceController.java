package application.offers.testService.controller;

import application.offers.testService.message.response.TestServiceResponse;

public interface TestServiceController {
    TestServiceResponse createTestService(String teste);
}
