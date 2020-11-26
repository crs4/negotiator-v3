package eu.bbmri.eric.csit.service.model.user;

import eu.bbmri.eric.csit.service.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Data
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "person")
public class Person extends BaseEntity {

    private String auth_subject;
    private String auth_name;
    private String aut_email;
    private String person_image;
    private Boolean is_admin;
    private String organization;
}
