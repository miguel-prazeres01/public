package infrastructure.base.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.entity.SeatTypeEnum;
import domain.seat.repository.SeatRepository;
import domain.show.entity.Show;
import domain.show.repository.ShowRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataSeeder {

    @Inject
    ShowRepository showRepository;

    @Inject
    SeatRepository seatRepository;

    @Transactional
    public void seed(@Observes StartupEvent event) {
        System.out.println("🚀 Starting seeding...");
        if (showRepository.findAllEntities().isEmpty()) {
            System.out.println("📦 Inserting show and seat...");
            Show show = new Show();
            show.setName("Verdade e Consequência (31 Maio) - Grupo de Teatro - Boca de Cena - Minde");
            show.setDateTime(LocalDateTime.parse("2025-05-31T21:30:00"));
            show.setPrice(new BigDecimal("10.00"));
            showRepository.save(show);

            Show show1 = new Show();
            show1.setName("Verdade e Consequência (1 Junho) - Grupo de Teatro - Boca de Cena - Minde");
            show1.setDateTime(LocalDateTime.parse("2025-06-01T17:00:00"));
            show1.setPrice(new BigDecimal("10.00"));
            showRepository.save(show1);

            ArrayList<String> lettersPlateia =  new ArrayList<String>();    
            lettersPlateia.add("A");
            lettersPlateia.add("B");
            lettersPlateia.add("C");
            lettersPlateia.add("D");
            lettersPlateia.add("E");
            lettersPlateia.add("F");
            lettersPlateia.add("G");
            lettersPlateia.add("H");
            lettersPlateia.add("I");
            lettersPlateia.add("J");
            lettersPlateia.add("K");
            lettersPlateia.add("L");

            ArrayList<String> lettersBalcao =  new ArrayList<String>();
            
            lettersBalcao.add("A");
            lettersBalcao.add("B");
            lettersBalcao.add("C");
            lettersBalcao.add("D");
            lettersBalcao.add("E");

            
            for (int i = 0; i < lettersPlateia.size() ; i++){
                for (long j = 16L; j > 0L; j--){
                    Seat seat = new Seat();
                    seat.setSeatNumber(j);
                    seat.setRow(lettersPlateia.get(i));
                    seat.setSeatState(SeatStateEnum.EMPTY);
                    seat.setSeatType(SeatTypeEnum.PLATEIA);
                    seat.setShow(show);
                    seatRepository.save(seat);
                }
            }

            for (int i = 0; i < lettersBalcao.size() ; i++){
                for (long j = 16L; j > 0L; j--){
                    Seat seat = new Seat();
                    seat.setSeatNumber(j);
                    seat.setRow(lettersBalcao.get(i));
                    seat.setSeatState(SeatStateEnum.EMPTY);
                    seat.setSeatType(SeatTypeEnum.BALCAO);
                    seat.setShow(show);
                    seatRepository.save(seat);
                }

            }

            /*Show show1 = new Show();
            show1.setName("Verdade e Consequência (1 Junho) - Grupo de Teatro - Boca de Cena - Minde");
            show1.setDateTime(LocalDateTime.parse("2025-07-01T20:30:00"));
            show1.setPrice(new BigDecimal("10.00"));
            showRepository.save(show1);*/

            ArrayList<String> lettersPlateia1 =  new ArrayList<String>();    
            lettersPlateia1.add("A");
            lettersPlateia1.add("B");
            lettersPlateia1.add("C");
            lettersPlateia1.add("D");
            lettersPlateia1.add("E");
            lettersPlateia1.add("F");
            lettersPlateia1.add("G");
            lettersPlateia1.add("H");
            lettersPlateia1.add("I");
            lettersPlateia1.add("J");
            lettersPlateia1.add("K");
            lettersPlateia1.add("L");

            ArrayList<String> lettersBalcao1 =  new ArrayList<String>();
            
            lettersBalcao1.add("A");
            lettersBalcao1.add("B");
            lettersBalcao1.add("C");
            lettersBalcao1.add("D");
            lettersBalcao1.add("E");

            
            for (int i = 0; i < lettersPlateia1.size() ; i++){
                for (long j = 16L; j > 0L; j--){
                    Seat seat = new Seat();
                    seat.setSeatNumber(j);
                    seat.setRow(lettersPlateia1.get(i));
                    seat.setSeatState(SeatStateEnum.EMPTY);
                    seat.setSeatType(SeatTypeEnum.PLATEIA);
                    seat.setShow(show1);
                    seatRepository.save(seat);
                }
            }

            for (int i = 0; i < lettersBalcao1.size() ; i++){
                for (long j = 16L; j > 0L; j--){
                    Seat seat = new Seat();
                    seat.setSeatNumber(j);
                    seat.setRow(lettersBalcao1.get(i));
                    seat.setSeatState(SeatStateEnum.EMPTY);
                    seat.setSeatType(SeatTypeEnum.BALCAO);
                    seat.setShow(show1);
                    seatRepository.save(seat);
                }

            }


            System.out.println("✅ Seeding complete.");
        } else {
            System.out.println("ℹ️ Data already exists, skipping seeding.");
        }
    }
}
