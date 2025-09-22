package domain.seat.services;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.person.entity.Person;
import domain.person.repository.PersonRepository;
import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.exception.ReservationInvalidException;
import domain.seat.exception.SeatNotFoundException;
import domain.seat.repository.SeatRepository;
import domain.websocket.NotificationWebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;


@ApplicationScoped
public class InitPaymentService {
    private final SeatRepository seatRepository;
    private final PersonRepository personRepository;
    private final EmailService emailService;
    private final NotificationWebSocket notificationWebSocket;

    @Inject
    public InitPaymentService(SeatRepository seatRepository, PersonRepository personRepository,
                    EmailService emailService, NotificationWebSocket notificationWebSocket){
        this.seatRepository = seatRepository;
        this.personRepository = personRepository;
        this.emailService = emailService;
        this.notificationWebSocket = notificationWebSocket;
    }

    @Transactional
    public Seat initPayment(Long seatId,
                             String sessionToken,
                             String buyerName,
                             String buyerEmail,
                             String buyerPhone) {
        Seat seat = seatRepository.get(seatId)
            .orElseThrow(SeatNotFoundException::new);

        // verify the same session and not expired
        if (seat.getSeatState() != SeatStateEnum.RESERVING
            || seat.getReservationExpiresAt() == null
            || seat.getReservationExpiresAt().isBefore(LocalDateTime.now())
            || !sessionToken.equals(seat.getReservedBySession())) {
            throw new ReservationInvalidException();
        }                 

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://clientes.eupago.pt/api/v1.02/mbway/create"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "ApiKey a2ya-10aj-wccn-tet8-uprw")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"payment\":{\"amount\":{\"currency\":\"EUR\",\"value\":".concat(seat.getShow().getPrice().toString()).concat("},\"identifier\":\"Test\",\"successUrl\":\"https://eupago.pt\",\"failUrl\":\"https://eupago.pt\",\"backUrl\":\"https://eupago.pt\",\"lang\":\"PT\",\"customerPhone\":\"935269447\",\"countryCode\":\"+351\"},\"customer\":{\"notify\":true,\"name\":\"joao\"}}")))
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response.body());
            String transactionId = root.get("reference").asText();

            System.out.println(transactionId); 
            seat.setTransactionId(transactionId);
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        var person = new Person();
        person.setName(buyerName);
        person.setContactEmail(buyerEmail);
        person.setPhoneNumber(buyerPhone);
        personRepository.save(person);
        seat.setPerson(person);

        seatRepository.save(seat);
        

        return seat;
    }

    @ApplicationScoped
    public void completePayment(String transactionId){

        
        List<Seat> seats = seatRepository.findByTransactionId(transactionId);

        emailService.sendTicketsPdfAsync(seats);
        notificationWebSocket.sendNotification(seats.get(0).getReservedBySession(), "Payment complete!");

        for(Seat seat: seats){
            seat.setSeatState(SeatStateEnum.OCCUPIED);
        
        

            // clear reservation metadata
            seat.setReservedBySession(null);
            seat.setReservationExpiresAt(null);          
               

            seatRepository.save(seat); 
        }


        
    }

    @Transactional
    public List<Seat> initPayment(List<Long> seatIds,
                                String sessionToken,
                                String buyerName,
                                String buyerEmail,
                                String buyerPhone) {

        List<Seat> updatedSeats = new ArrayList<>();

        // Create buyer once
        Person person = new Person();
        person.setName(buyerName);
        person.setContactEmail(buyerEmail);
        person.setPhoneNumber(buyerPhone);
        personRepository.save(person);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.get(seatId)
                .orElseThrow(SeatNotFoundException::new);

            // Session and reservation validity check
            if (seat.getSeatState() != SeatStateEnum.RESERVING
                || seat.getReservationExpiresAt() == null
                || seat.getReservationExpiresAt().isBefore(LocalDateTime.now())
                || !sessionToken.equals(seat.getReservedBySession())) {
                throw new ReservationInvalidException();
            }

            seat.setPerson(person);
            seatRepository.save(seat);
            updatedSeats.add(seat);
        }

        try {
            System.out.println(buyerPhone);
            System.out.println(buyerPhone.split(" ")[1]);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://clientes.eupago.pt/api/v1.02/mbway/create"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "ApiKey a2ya-10aj-wccn-tet8-uprw")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"payment\":{\"amount\":{\"currency\":\"EUR\",\"value\":".concat((updatedSeats.get(0).getShow().getPrice().multiply(new BigDecimal(seatIds.size()))).toString()).concat("},\"identifier\":\"Test\",\"successUrl\":\"https://eupago.pt\",\"failUrl\":\"https://eupago.pt\",\"backUrl\":\"https://eupago.pt\",\"lang\":\"PT\",\"customerPhone\":\""+buyerPhone.split(" ")[1]+"\",\"countryCode\":\"+351\"},\"customer\":{\"notify\":true,\"name\":\""+buyerName+"\"}}")))
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response.body());
            String transactionId = root.get("reference").asText();

            System.out.println(transactionId); 

            for(Seat seat: updatedSeats){
                seat.setTransactionId(transactionId);
                seatRepository.save(seat);
            }

            
            
        } catch (Exception e){
            e.printStackTrace();
            throw new ReservationInvalidException();
        }

        return updatedSeats;
    }

}
