package domain.show.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import domain.seat.entity.Seat;
import domain.seat.entity.SeatStateEnum;
import domain.seat.entity.SeatTypeEnum;
import domain.show.entity.Show;
import domain.show.exception.ShowAlreadyExistsException;
import domain.show.repository.ShowRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateShowService {

    private final ShowRepository showRepository;

    @Inject
    public CreateShowService(ShowRepository showRepository){
        this.showRepository = showRepository;
    }

    
    @Transactional
    public Show createShow(String name, LocalDateTime dateTime, BigDecimal price, Long rowsNumber, Long columnsNumber){
        if(showRepository.findByName(name).isPresent())
            throw new ShowAlreadyExistsException();

        Show show = new Show();
        List<Seat> seats = new ArrayList<>();

        show.setDateTime(dateTime);
        show.setName(name);
        show.setPrice(price);

        for(char row = 'A'; row < 'A' + rowsNumber; row++) {
            for(long column = 1; column <= columnsNumber; column++) {
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setSeatState(SeatStateEnum.EMPTY);
                seat.setRow(String.valueOf(row)); 
                seat.setSeatNumber(column);
                seat.setSeatType(SeatTypeEnum.BALCAO);
        
                seats.add(seat);
            }
        }
        
        show.setSeats(seats);
        showRepository.save(show);

        return show;
    }
    
}
