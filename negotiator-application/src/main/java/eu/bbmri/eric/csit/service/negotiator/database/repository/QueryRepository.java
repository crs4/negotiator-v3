package eu.bbmri.eric.csit.service.negotiator.database.repository;

import eu.bbmri.eric.csit.service.negotiator.database.model.Query;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface QueryRepository extends JpaRepository<Query, String> {

  @NotNull
  @Override
  @EntityGraph(value = "query-with-detailed-resources")
  List<Query> findAll();

  @EntityGraph(value = "query-with-detailed-resources")
  Optional<Query> findDetailedById(String id);
}
