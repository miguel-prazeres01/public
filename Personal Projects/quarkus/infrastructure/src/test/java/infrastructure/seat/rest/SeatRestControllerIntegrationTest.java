package infrastructure.seat.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.awaitility.Awaitility.await;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.api.connection.ConnectionHolder;

import application.seat.message.request.CancelSeatRequest;
import application.seat.message.request.InitPaymentSeatsRequest;
import application.seat.message.request.InitPaymentShowRequest;
import application.seat.message.request.ReserveShowRequest;
import application.seat.message.request.ReserveShowsRequest;
import application.seat.message.response.CancelSeatResponse;
import application.seat.message.response.InitPaymentSeatsResponse;
import application.seat.message.response.InitPaymentShowResponse;
import application.seat.message.response.ReserveShowResponse;
import application.seat.message.response.ReserveShowsResponse;
import application.show.manager.ShowManager;
import application.show.message.dto.ShowDTO;
import application.show.message.request.CreateShowRequest;
import domain.seat.entity.SeatStateEnum;
import domain.show.repository.ShowRepository;
import infrastructure.show.rest.ShowRestController;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestHTTPEndpoint(SeatRestController.class)
public class SeatRestControllerIntegrationTest {

    @Inject 
    ShowRepository showRepository;

    @Inject 
    ShowManager showManager;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test 
    void create_show(){
        var time = LocalDateTime.now();
        CreateShowRequest createShowRequest = new CreateShowRequest("show2", time, new BigDecimal(10.20), 1L, 4L);
        var response = showManager.createShow(createShowRequest);

        assertNotNull(response.getShowDTO());
        assertNotNull(response.getShowDTO().getId());
        assertEquals(response.getShowDTO().getName(), "show2");
        assertEquals(response.getShowDTO().getDateTime(), time);
        assertEquals(response.getShowDTO().getPrice(), new BigDecimal(10.20));
        assertEquals(response.getShowDTO().getSeats().size(), 4);

        assertNotNull(response.getShowDTO().getSeats().get(0).getId());
        assertEquals(response.getShowDTO().getSeats().get(0).getReservationExpiresAt(),null);
        assertEquals(response.getShowDTO().getSeats().get(0).getSeatState(),SeatStateEnum.EMPTY);
        assertEquals(response.getShowDTO().getSeats().get(0).getRow(),"A");
        assertEquals(response.getShowDTO().getSeats().get(0).getSeatNumber(),1L);
    }
    
    @Test
    void test_reserv_seat() throws Exception {

        

        var time = LocalDateTime.now();
        CreateShowRequest createShowRequest = new CreateShowRequest("show", time, new BigDecimal(10.20), 1L, 4L);
        var response = showManager.createShow(createShowRequest);

        ReserveShowRequest reserveShowRequest = new ReserveShowRequest(response.getShowDTO().getSeats().get(0).getId());

        String payload = objectMapper.writeValueAsString(reserveShowRequest); 
        String secret = "dev-test-secret"; 
        String signature = generateSignature(payload, secret);

        var response2 = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signature)
                .body(reserveShowRequest)
                .post("/reserve")
                .then()
                .statusCode(200)
                .extract()
                .as(ReserveShowResponse.class);

        assertNotNull(response2);
        assertNotNull(response2.getToken());
        assertNotNull(response2.getSeat());
        assertEquals(response2.getSeat().getSeatState(), SeatStateEnum.RESERVING);


        CancelSeatRequest cancelSeatRequest = new CancelSeatRequest(response.getShowDTO().getSeats().get(0).getId());

        String payloadCancel = objectMapper.writeValueAsString(cancelSeatRequest); 
        String signatureCancel = generateSignature(payloadCancel, secret);

        var cancelResponse = given()
                .when()
                .header("Authorization", "Bearer " + response2.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signatureCancel)
                .body(cancelSeatRequest)
                .post("/cancel")
                .then()
                .statusCode(200)
                .extract()
                .as(CancelSeatResponse.class);

        assertNotNull(cancelResponse);
        assertNotNull(cancelResponse.getSeat());
        assertEquals(cancelResponse.getSeat().getSeatState(), SeatStateEnum.EMPTY);

        var response3 = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signature)
                .body(reserveShowRequest)
                .post("/reserve")
                .then()
                .statusCode(200)
                .extract()
                .as(ReserveShowResponse.class);

        assertNotNull(response3);
        assertNotNull(response3.getToken());
        assertNotNull(response3.getSeat());
        assertEquals(response3.getSeat().getSeatState(), SeatStateEnum.RESERVING);

        System.out.println("token1");
        System.out.println(response3.getToken());
        String encodedToken = URLEncoder.encode(response3.getToken(), StandardCharsets.UTF_8);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://localhost:8081/notifications?token=" + encodedToken);
        NotificationClientEndpoint clientEndpoint = new NotificationClientEndpoint();
        container.connectToServer(clientEndpoint, uri);

        InitPaymentShowRequest initPaymentShowRequest = new InitPaymentShowRequest(response.getShowDTO().getSeats().get(0).getId(), "joao", "joaopedroluis01@gmail.com", "+351 912345678");

        String payloadPayment = objectMapper.writeValueAsString(initPaymentShowRequest); 
        String signaturePayment = generateSignature(payloadPayment, secret);

        var response4 = given()
                .when()
                .header("Authorization", "Bearer " + response3.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signaturePayment)
                .body(initPaymentShowRequest)
                .post("/initPayment")
                .then()
                .statusCode(200)
                .extract()
                .as(InitPaymentShowResponse.class);

        System.out.println(response4);

        await().atMost(240, TimeUnit.SECONDS).untilAsserted(() -> {
            String notification = clientEndpoint.getNextMessage();
            System.out.println("Received notification: " + notification);
            assert notification != null : "Expected a notification message";
            assert notification.contains("Payment complete!");
            
        });

        ReserveShowsRequest reserveShowsRequest = new ReserveShowsRequest(Arrays.asList(response.getShowDTO().getSeats().get(1).getId(), response.getShowDTO().getSeats().get(2).getId()));

        String payloadReserves = objectMapper.writeValueAsString(reserveShowsRequest); 
        String signatureReserves = generateSignature(payloadReserves, secret);

        var response10 = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signatureReserves)
                .body(reserveShowsRequest)
                .post("/reserves")
                .then()
                .statusCode(200)
                .extract()
                .as(ReserveShowsResponse.class);

        assertNotNull(response10);
        assertNotNull(response10.getToken());
        assertNotNull(response10.getSeats());
        assertEquals(response10.getSeats().get(0).getSeatState(), SeatStateEnum.RESERVING);

        assertNotNull(response10);
        assertNotNull(response10.getToken());
        assertNotNull(response10.getSeats());
        assertEquals(response10.getSeats().get(1).getSeatState(), SeatStateEnum.RESERVING);

        System.out.println("token2");
        System.out.println(response10.getToken());
        String encodedToken2 = URLEncoder.encode(response10.getToken(), StandardCharsets.UTF_8);
        WebSocketContainer container2 = ContainerProvider.getWebSocketContainer();
        URI uri2 = new URI("ws://localhost:8081/notifications?token=" + encodedToken2);
        NotificationClientEndpoint clientEndpoint2 = new NotificationClientEndpoint();
        container2.connectToServer(clientEndpoint2, uri2);


        InitPaymentSeatsRequest initPaymentSeatsRequest = new InitPaymentSeatsRequest(Arrays.asList(response10.getSeats().get(0).getId(), response10.getSeats().get(1).getId()), "joao", "joaopedroluis01@gmail.com", "+351 912345678");

        String payloadSeats = objectMapper.writeValueAsString(initPaymentSeatsRequest); 
        String signatureSeats = generateSignature(payloadSeats, secret);

        var response11 = given()
                .when()
                .header("Authorization", "Bearer " + response10.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Signature", signatureSeats)
                .body(initPaymentSeatsRequest)
                .post("/initPaymentSeats")
                .then()
                .statusCode(200)
                .extract()
                .as(InitPaymentSeatsResponse.class);

        System.out.println(response11);

        await().atMost(240, TimeUnit.SECONDS).untilAsserted(() -> {
            String notification = clientEndpoint2.getNextMessage();
            System.out.println("Received notification: " + notification);
            assert notification != null : "Expected a notification message";
            assert notification.contains("Payment complete!");
            
        });

        

    }


    private String generateSignature(String payload, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
        }
}
