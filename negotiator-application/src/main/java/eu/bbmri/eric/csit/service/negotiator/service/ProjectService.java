package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.api.dto.project.ProjectCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotStorableException;
import eu.bbmri.eric.csit.service.negotiator.database.model.Project;
import eu.bbmri.eric.csit.service.negotiator.database.repository.ProjectRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  @Autowired private ProjectRepository projectRepository;
  @Autowired private ModelMapper modelMapper;

  public Project create(ProjectCreateDTO projectRequest) {
    Project projectEntity = modelMapper.map(projectRequest, Project.class);
    try {
      return projectRepository.save(projectEntity);
    } catch (DataIntegrityViolationException ex) {
      throw new EntityNotStorableException();
    }
  }

  @Transactional
  public Project findById(String id) {
    return projectRepository
        .findDetailedById(id)
        .orElseThrow(() -> new EntityNotFoundException(id));
  }

  @Transactional
  public List<Project> findAll() {
    return projectRepository.findAll();
  }
}
