package eu.bbmri.eric.csit.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "DataSource")
@Table(name = "data_source")
public class DataSource extends BaseEntity {

  public enum ApiType {
    MOLGENIS
  }

  private String description;

  @NotNull private String name;

  @NotNull private String url;

  @NotNull private String apiUrl;

  @NotNull private String apiUsername;

  @NotNull private String apiPassword;

  @Enumerated(EnumType.STRING)
  @NotNull
  private ApiType apiType;

  @NotNull private String resourceNetwork;

  @NotNull private String resourceBiobank;

  @NotNull private String resourceCollection;

  @NotNull private Boolean syncActive;

  private Boolean sourcePrefix;
}
