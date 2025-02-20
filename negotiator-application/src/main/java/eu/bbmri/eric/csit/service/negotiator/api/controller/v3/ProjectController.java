package eu.bbmri.eric.csit.service.negotiator.api.controller.v3;

import eu.bbmri.eric.csit.service.negotiator.api.dto.project.ProjectCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.project.ProjectDTO;
import eu.bbmri.eric.csit.service.negotiator.database.model.Project;
import eu.bbmri.eric.csit.service.negotiator.service.ProjectService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3")
public class ProjectController {

  @Autowired private ProjectService projectService;

  @Autowired private ModelMapper modelMapper;

  @GetMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  List<ProjectDTO> list() {
    return projectService.findAll().stream()
        .map(project -> modelMapper.map(project, ProjectDTO.class))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
    ProjectDTO retrieve(@Valid @PathVariable String id) {
    return modelMapper.map(projectService.findById(id), ProjectDTO.class);
  }

  @PostMapping(
      value = "/projects",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  ProjectDTO add(@Valid @RequestBody ProjectCreateDTO request) {
    Project projectEntity = projectService.create(request);
    return modelMapper.map(projectEntity, ProjectDTO.class);
  }
}
