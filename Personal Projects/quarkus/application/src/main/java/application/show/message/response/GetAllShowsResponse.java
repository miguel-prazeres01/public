package application.show.message.response;

import java.util.List;

import application.show.message.dto.ShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class GetAllShowsResponse {
    private List<ShowDTO> shows;
}
