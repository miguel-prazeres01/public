package domain.base.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @EqualsAndHashCode.Include
    @Id
    @SequenceGenerator(name = "project_sequence", sequenceName = "project_sequence", allocationSize = 1)
    @GeneratedValue(generator = "project_sequence", strategy = GenerationType.SEQUENCE)
    private Long id;

    public void initialize() {

    }
}
