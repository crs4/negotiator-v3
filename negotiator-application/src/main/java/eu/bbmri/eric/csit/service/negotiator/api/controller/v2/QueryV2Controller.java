package eu.bbmri.eric.csit.service.negotiator.api.controller.v2;

import eu.bbmri.eric.csit.service.negotiator.api.dto.query.CollectionV2DTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.query.QueryCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.query.QueryCreateV2DTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.query.ResourceDTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.query.QueryV2DTO;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.database.model.Query;
import eu.bbmri.eric.csit.service.negotiator.database.model.Request;
import eu.bbmri.eric.csit.service.negotiator.service.QueryService;
import eu.bbmri.eric.csit.service.negotiator.service.RequestService;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class QueryV2Controller {

  @Value("${negotiator.redirectPath:/gui/request}")
  private String REDIRECT_PATH;

  private final QueryService queryService;

  private final RequestService requestService;

  private final ModelMapper modelMapper;

  public QueryV2Controller(
      QueryService queryService, RequestService requestService, ModelMapper modelMapper) {
    this.queryService = queryService;
    this.requestService = requestService;
    this.modelMapper = modelMapper;

    // Mapper from v2 Query to V3 Query
    TypeMap<QueryCreateV2DTO, QueryCreateDTO> v2ToV3Map =
        modelMapper.createTypeMap(QueryCreateV2DTO.class, QueryCreateDTO.class);

    Converter<Set<CollectionV2DTO>, Set<ResourceDTO>> collectionV2ToResourceV3 =
        q -> convertCollectionV2ToResourceV3(q.getSource());

    v2ToV3Map.addMappings(
        mapper ->
            mapper
                .using(collectionV2ToResourceV3)
                .map(QueryCreateV2DTO::getCollections, QueryCreateDTO::setResources));

    // Mapper from v2 Query to V3 Query
    TypeMap<Query, QueryV2DTO> queryToV3Response =
        modelMapper.createTypeMap(Query.class, QueryV2DTO.class);

    Converter<String, String> queryToRedirectUri = q -> convertIdToRedirectUri(q.getSource());
    queryToV3Response.addMappings(
        mapper ->
            mapper.using(queryToRedirectUri).map(Query::getId, QueryV2DTO::setRedirectUri));
  }

  private String convertIdToRedirectUri(String queryId) {
    Query query = queryService.findById(queryId);
    Request request = query.getRequest();
    String baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    if (request == null) {
      return "%s%s/jsonQuery=%s".formatted(baseURL, REDIRECT_PATH, queryId);
    } else {
      return "%s%s/queryId=%s&jsonQuery=%s".formatted(baseURL, REDIRECT_PATH, request.getId(), queryId);
    }
  }

  private Set<ResourceDTO> convertCollectionV2ToResourceV3(Set<CollectionV2DTO> collections) {
    Map<String, ResourceDTO> resources = new HashMap<>();
    collections.forEach(
        collection -> {
          String biobankId = collection.getBiobankId();

          ResourceDTO biobankResource;
          if (resources.containsKey(biobankId)) {
            biobankResource = resources.get(biobankId);
          } else {
            biobankResource = new ResourceDTO();
            biobankResource.setId(biobankId);
            biobankResource.setType("biobank");
            biobankResource.setChildren(new HashSet<>());
            resources.put(biobankId, biobankResource);
          }
          ResourceDTO collectionResource = new ResourceDTO();
          collectionResource.setType("collection");
          collectionResource.setId(collection.getCollectionId());
          biobankResource.getChildren().add(collectionResource);
        });

    return new HashSet<>(resources.values());
  }

  @PostMapping(
      value = "/api/directory/create_query",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<QueryV2DTO> add(@Valid @RequestBody QueryCreateV2DTO queryRequest) {
    QueryCreateDTO v3Request = modelMapper.map(queryRequest, QueryCreateDTO.class);
    Query queryEntity;
    boolean created;
    if (queryRequest.getToken() != null && !queryRequest.getToken().isEmpty()) {
      // Update an old query or add a new one to a request
      String[] tokens = queryRequest.getToken().split("__search__");
      try {
        // If the request was not found in V2, a new query was created
        requestService.findById(tokens[0]);
        created = false;
        if (tokens.length == 1) {
          queryEntity = queryService.create(v3Request);
          requestService.addQueryToRequest(tokens[0], queryEntity);
        } else { // Updating an old query: the requestToken can be ignored
          queryEntity = queryService.update(tokens[1], v3Request);
        }

      } catch (EntityNotFoundException ex) {
        queryEntity = queryService.create(v3Request);
        created = true;
      }
    } else {
      queryEntity = queryService.create(v3Request);
      created = true;
    }
    QueryV2DTO response = modelMapper.map(queryEntity, QueryV2DTO.class);
    if (created) {
      return ResponseEntity.created(URI.create(response.getRedirectUri())).body(response);
    } else {
      return ResponseEntity.accepted().header("Location", response.getRedirectUri()).body(response);
    }
  }
}
