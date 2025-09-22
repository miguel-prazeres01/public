package infrastructure.offers.testService.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import application.offers.testService.controller.TestServiceController;
import application.offers.testService.manager.TestServiceManager;
import application.offers.testService.message.response.TestServiceResponse;
import infrastructure.base.interceptor.Logged;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test-services")
@Tag(name = "TestService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestServiceRestController implements TestServiceController{

    private final TestServiceManager testServiceManager;

    @Inject
    public TestServiceRestController(TestServiceManager testServiceManager){
        this.testServiceManager = testServiceManager;
    }

    @POST
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a Test", description = "test", operationId = "test")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TestServiceResponse.class)))
     @Override
    public TestServiceResponse createTestService(String teste){
        return testServiceManager.createTestService(teste);
    }
    
}
