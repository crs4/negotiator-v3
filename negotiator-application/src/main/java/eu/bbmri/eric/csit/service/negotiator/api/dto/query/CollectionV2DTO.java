package eu.bbmri.eric.csit.service.negotiator.api.dto.query;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionV2DTO {
  @NotNull private String collectionId;

  @Nullable private String biobankId;
}
