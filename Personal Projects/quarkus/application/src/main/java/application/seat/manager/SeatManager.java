package application.seat.manager;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import application.seat.mapper.SeatMapper;
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
import domain.seat.entity.Seat;
import domain.seat.services.CancelSeatService;
import domain.seat.services.GetSeatService;
import domain.seat.services.InitPaymentService;
import domain.seat.services.ReserveSeatService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SeatManager {
    private final SeatMapper seatMapper;
    private final InitPaymentService initPaymentService;
    private final ReserveSeatService reserveSeatService;
    private final GetSeatService getSeatService;
    private final CancelSeatService cancelSeatService;

    @Inject
    public SeatManager(SeatMapper seatMapper, InitPaymentService initPaymentService, ReserveSeatService reserveSeatService, GetSeatService getSeatService, CancelSeatService cancelSeatService){
        this.seatMapper = seatMapper;
        this.initPaymentService = initPaymentService;
        this.reserveSeatService = reserveSeatService;
        this.getSeatService = getSeatService;
        this.cancelSeatService = cancelSeatService;
    }

    public ReserveShowResponse reserveShow(ReserveShowRequest request){
        Long seatId = request.getSeatId();
        Pair<Seat, String> p = reserveSeatService.reserveSeat(seatId);

        return new ReserveShowResponse(seatMapper.toDto(p.getLeft()), p.getRight());
    }

    public ReserveShowsResponse reserveShows(ReserveShowsRequest request){
        List<Long> seatIds = request.getSeatIds();
        Pair<List<Seat>, String> p = reserveSeatService.reserveSeats(seatIds);

        return new ReserveShowsResponse(seatMapper.toDto(p.getLeft()), p.getRight());
    }

    public InitPaymentShowResponse initPaymentShow(InitPaymentShowRequest initPaymentShowRequest, String token){

        Long seatId = initPaymentShowRequest.getSeatId();
        String buyerName = initPaymentShowRequest.getBuyerName();
        String buyerEmail = initPaymentShowRequest.getBuyerEmail();
        String buyerPhone = initPaymentShowRequest.getBuyerPhone();
        Seat seat = initPaymentService.initPayment(seatId, token, buyerName, buyerEmail, buyerPhone);

        return new InitPaymentShowResponse(seatMapper.toDto(seat));
    }

    public InitPaymentSeatsResponse initPaymentSeats(InitPaymentSeatsRequest initPaymentSeatRequest, String token){

        List<Long> seatIds = initPaymentSeatRequest.getSeatId();
        String buyerName = initPaymentSeatRequest.getBuyerName();
        String buyerEmail = initPaymentSeatRequest.getBuyerEmail();
        String buyerPhone = initPaymentSeatRequest.getBuyerPhone();
        List<Seat> seats = initPaymentService.initPayment(seatIds, token, buyerName, buyerEmail, buyerPhone);

        return new InitPaymentSeatsResponse(seatMapper.toDto(seats));
    }

    public GetShowSeatsResponse getShowSeats(GetShowSeatsRequest getShowSeatsRequest){
        Long showId = getShowSeatsRequest.getShowId();

        List<Seat> seats = getSeatService.getAllSeats(showId);

        return new GetShowSeatsResponse(seatMapper.toDto(seats));
    }

    public CancelSeatResponse cancelSeat(CancelSeatRequest cancelSeatRequest, String token){
        Long showId = cancelSeatRequest.getSeatId();

        Seat seat = cancelSeatService.cancelSeat(showId, token);

        return new CancelSeatResponse(seatMapper.toDto(seat));

    }

    public CancelSeatsResponse cancelSeats(CancelSeatsRequest cancelSeatsRequest, String token){
        List<Long> showIds = cancelSeatsRequest.getSeatIds();

        List<Seat> seat = cancelSeatService.cancelSeats(showIds, token);

        return new CancelSeatsResponse(seatMapper.toDto(seat));

    }
}
