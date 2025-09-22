package domain.person.entity;

import domain.base.entity.BaseEntity;
import domain.phoneValidator.PhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "person"
)
@Entity
public class Person extends BaseEntity{
    
    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @PhoneNumber
    @Column(name = "phoneNumber")
    private String phoneNumber;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, length = 100, name = "contact_email")
    private String contactEmail;
}

