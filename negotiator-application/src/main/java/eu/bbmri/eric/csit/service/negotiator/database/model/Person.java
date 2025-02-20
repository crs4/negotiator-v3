package eu.bbmri.eric.csit.service.negotiator.database.model;

import com.sun.istack.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
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
@Table(name = "person")
@NamedEntityGraph(
    name = "person-detailed",
    attributeNodes = {@NamedAttributeNode("roles")})
public class Person extends BaseEntity {

  @ManyToMany
  @JoinTable(
      name = "person_resource_link",
      joinColumns = @JoinColumn(name = "person_id"),
      inverseJoinColumns = @JoinColumn(name = "resource_id"))
  @Exclude
  Set<Resource> resources;

  @ManyToMany
  @JoinTable(
      name = "person_project_link",
      joinColumns = @JoinColumn(name = "person_id"),
      inverseJoinColumns = @JoinColumn(name = "project_id"))
  @Exclude
  Set<Project> projects;

  @OneToMany(mappedBy = "person")
  @Exclude
  Set<PersonRequestRole> roles = new HashSet<>();

  @Column(unique = true)
  @NotNull
  private String authSubject;

  @Column(unique = true)
  @NotNull
  private String authName;

  @Column(unique = true)
  @NotNull
  private String authEmail;

  private String password; // can be null if the user is authenticated via OIDC

  private byte[] personImage;

  private String organization;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
  private Set<Authority> authorities;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return Objects.equals(getAuthSubject(), person.getAuthSubject())
        && Objects.equals(getAuthName(), person.getAuthName())
        && Objects.equals(getAuthEmail(), person.getAuthEmail())
        && Objects.equals(getPassword(), person.getPassword())
        && Arrays.equals(getPersonImage(), person.getPersonImage())
        && Objects.equals(getOrganization(), person.getOrganization());
  }

  @Override
  public int hashCode() {
    int result =
        Objects.hash(
            getAuthSubject(), getAuthName(), getAuthEmail(), getPassword(), getOrganization());
    result = 31 * result + Arrays.hashCode(getPersonImage());
    return result;
  }
}
