package application.offers.testService.message.dto;

import application.base.message.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestServiceDTO extends BaseDTO{

    private String teste;

    public TestServiceDTO(Long id, String teste){
        super(id);
        this.teste = teste;
    }
    
}
