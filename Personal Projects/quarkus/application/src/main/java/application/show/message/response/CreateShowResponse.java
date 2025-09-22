package application.show.message.response;

import application.show.message.dto.ShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class CreateShowResponse {
    private ShowDTO showDTO;
}
