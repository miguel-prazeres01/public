package infrastructure.base.scheduler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.repository.SeatRepository;
import domain.seat.services.InitPaymentService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ScheduleCleanSeats {
    private final SeatRepository seatRepository;
    private final InitPaymentService initPaymentService;

    @Inject
    public ScheduleCleanSeats(SeatRepository seatRepository, InitPaymentService initPaymentService){
        this.seatRepository = seatRepository;
        this.initPaymentService = initPaymentService;
    }

    @Scheduled(every = "60s")
    @Transactional
    public void expireReservations() {
        System.out.println("cleaning seats");
        List<Seat> expired = seatRepository.findAllBySeatStateAndReservationExpiresAtBefore(
            SeatStateEnum.RESERVING, LocalDateTime.now());
        for (Seat s : expired) {
            System.out.println("cleaning seat");
            s.setSeatState(SeatStateEnum.EMPTY);
            s.setReservedBySession(null);
            s.setReservationExpiresAt(null);
            s.setTransactionId(null);

            seatRepository.save(s);
        }
    }

    @Scheduled(every = "20s")
    @Transactional
    public void refreshPayment() {
        System.out.println("refresh payments");

        List<String> alreadyPaid = new ArrayList<>();
        List<Seat> expired = seatRepository.findAllBySeatsPending(
            SeatStateEnum.RESERVING, LocalDateTime.now());

        System.out.println(expired);
        for (Seat s : expired) {
            System.out.println("reserving");
            if(s.getTransactionId()!=null && !alreadyPaid.contains(s.getTransactionId())){
                HttpResponse<String> response;
                System.out.println("paying");
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://clientes.eupago.pt/clientes/rest_api/multibanco/info"))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"chave\":\"CHAVE\",\"referencia\":\""+s.getTransactionId()+"\",\"entidade\":\"ENTIDADE\"}"))
                    .build();
                
                try {
                    response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());
                    ObjectMapper mapper = new ObjectMapper();

                    JsonNode root = mapper.readTree(response.body());
                    String state = root.get("estado_referencia").asText();

                    System.out.println(state);

                    if(state.equals("paga")){

                        initPaymentService.completePayment(s.getTransactionId());
                    }
                    alreadyPaid.add(s.getTransactionId());

                    
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                
                
                //if(response.body().)
                
            }
        }
    }
}
