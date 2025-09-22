package application.seat.controller;

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

public interface SeatController {
    GetShowSeatsResponse getShowSeats(GetShowSeatsRequest getShowSeatsRequest);

    ReserveShowResponse reserveShow(ReserveShowRequest request);

    ReserveShowsResponse reserveShows(ReserveShowsRequest request);

    InitPaymentShowResponse initPaymentShow(InitPaymentShowRequest initPaymentShowRequest);

    InitPaymentSeatsResponse initPaymentSeats(InitPaymentSeatsRequest initPaymentSeatsRequest);

    CancelSeatResponse cancelSeat(CancelSeatRequest cancelSeatRequest);

    CancelSeatsResponse cancelSeats(CancelSeatsRequest cancelSeatsRequest);
}
