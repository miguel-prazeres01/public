package infrastructure.seat.rest;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import application.seat.controller.SeatController;
import application.seat.manager.SeatManager;
import application.seat.message.request.CancelSeatRequest;
import application.seat.message.request.CancelSeatsRequest;
import application.seat.message.request.GetShowSeatsRequest;
import application.seat.message.request.InitPaymentSeatsRequest;
import application.seat.message.request.InitPaymentShowRequest;
import application.seat.message.request.ReserveShowRequest;
import application.seat.message.request.ReserveShowsRequest;
import application.seat.message.response.CancelSeatResponse;
import application.seat.message.response.CancelSeatsResponse;
import application.seat.message.response.GetShowSeatsResponse;
import application.seat.message.response.InitPaymentSeatsResponse;
import application.seat.message.response.InitPaymentShowResponse;
import application.seat.message.response.ReserveShowResponse;
import application.seat.message.response.ReserveShowsResponse;
import infrastructure.base.interceptor.Logged;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/seats")
@Tag(name = "Seats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeatRestController implements SeatController{

    @Inject
    JsonWebToken jwt;
    
    private final SeatManager seatManager;

    @Inject
    public SeatRestController(SeatManager seatManager){
        this.seatManager = seatManager;
    }

    @POST
    @Path("/reserve")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Reserves a seat on a show", description = "Reserves a seat on a show to start the payment", operationId = "reserveSeat")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ReserveShowResponse.class)))
    @Override
    public ReserveShowResponse reserveShow(ReserveShowRequest reserveShowRequest){
        return seatManager.reserveShow(reserveShowRequest);
    }

    @POST
    @Path("/reserves")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Reserves a seat on a show", description = "Reserves a seat on a show to start the payment", operationId = "reserveSeat")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ReserveShowsResponse.class)))
    @Override
    public ReserveShowsResponse reserveShows(ReserveShowsRequest reserveShowsRequest){
        return seatManager.reserveShows(reserveShowsRequest);
    }

    @POST
    @PermitAll
    @Path("/cancel")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cancel a seat on a show", description = "Cancel a seat that was being reserved", operationId = "cancelSeat")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CancelSeatResponse.class)))
    @Override
    public CancelSeatResponse cancelSeat(CancelSeatRequest cancelSeatRequest){
        return seatManager.cancelSeat(cancelSeatRequest, jwt.getName());
    }

    @POST
    @PermitAll
    @Path("/cancelSeats")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cancel a seat on a show", description = "Cancel a seat that was being reserved", operationId = "cancelSeat")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CancelSeatsResponse.class)))
    @Override
    public CancelSeatsResponse cancelSeats(CancelSeatsRequest cancelSeatsRequest){
        return seatManager.cancelSeats(cancelSeatsRequest, jwt.getName());
    }

    @POST
    @PermitAll
    @Path("/initPayment")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Inits a payment for a seat", description = "Inits a payment for a seat", operationId = "initPayment")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InitPaymentShowResponse.class)))
    @Override
    public InitPaymentShowResponse initPaymentShow(InitPaymentShowRequest initPaymentShowRequest){        

        return seatManager.initPaymentShow(initPaymentShowRequest, jwt.getName());
    }

    @POST
    @PermitAll
    @Path("/initPaymentSeats")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Inits a payment for a seat", description = "Inits a payment for a seat", operationId = "initPayment")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = InitPaymentSeatsResponse.class)))
    @Override
    public InitPaymentSeatsResponse initPaymentSeats(InitPaymentSeatsRequest initPaymentSeatsRequest){
        return seatManager.initPaymentSeats(initPaymentSeatsRequest, jwt.getName());
    }

    @POST
    @Path("/getByShow")
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the seats on a show", description = "Get the seats on a show", operationId = "getShowSeats")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GetShowSeatsResponse.class)))
    @Override
    public GetShowSeatsResponse getShowSeats(GetShowSeatsRequest getShowSeatsRequest){
        return seatManager.getShowSeats(getShowSeatsRequest);
    }

}
