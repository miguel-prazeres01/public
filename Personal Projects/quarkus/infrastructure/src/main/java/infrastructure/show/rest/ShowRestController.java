package infrastructure.show.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import application.show.controller.ShowController;
import application.show.manager.ShowManager;
import application.show.message.request.GetShowRequest;
import application.show.message.response.GetAllShowsResponse;
import application.show.message.response.GetShowResponse;
import infrastructure.base.interceptor.Logged;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/shows")
@Tag(name = "Shows")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShowRestController implements ShowController{
    
    private final ShowManager showManager;

    @Inject
    public ShowRestController(ShowManager showManager){
        this.showManager = showManager;
    }

    @GET
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the shows", description = "Get all the shows", operationId = "getShows")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GetAllShowsResponse.class)))
    @Override
    public GetAllShowsResponse getAllShows(){
        return showManager.getAllShows();
    }

    @POST
    @Logged
    @Path("/allshows")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a show", description = "Get a show", operationId = "getShow")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GetShowResponse.class)))
    @Override
    public GetShowResponse getShow(GetShowRequest getShowRequest){
        return showManager.getShow(getShowRequest);
    }
}
