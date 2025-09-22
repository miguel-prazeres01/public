package domain.offers.testService.entity;


import domain.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "testService"
)
@Entity
public class TestService extends BaseEntity{
    private String test;
}
