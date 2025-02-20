package eu.bbmri.eric.csit.service.negotiator.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "form_field")
public class FormField extends AuditEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "form_field_type_id")
  @Exclude
  private FormFieldType formFieldType;

  private String name;
  private String label;
  private String category;
  private String defaultValue;

  @Column(name = "\"constraint\"")
  private String constraint;
}
