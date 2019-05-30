package com.justinaspetuchovas.health.model.user;

import com.justinaspetuchovas.health.common.ValidationConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity that represents a user.
 */
@Entity
@Table(indexes = {
    @Index(name = "username_index", columnList = "username", unique = true),
    @Index(name = "first_name_index", columnList = "first_name"),
    @Index(name = "last_name_index", columnList = "last_name")
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "UUID(16)", updatable = false, nullable = false)
  private UUID id;

  @Column(columnDefinition = "VARCHAR_IGNORECASE(" + ValidationConstants.USERNAME_LENGTH_MAX + ")")
  private String username;

  @Column(length = ValidationConstants.PASSWORD_LENGTH_MAX)
  private String password;

  @Column(
      name = "first_name",
      columnDefinition = "VARCHAR_IGNORECASE(" + ValidationConstants.NAME_LENGTH_MAX + ")"
  )
  private String firstName;

  @Column(
      name = "last_name",
      columnDefinition = "VARCHAR_IGNORECASE(" + ValidationConstants.NAME_LENGTH_MAX + ")"
  )
  private String lastName;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastPasswordResetDate;

  @Column(length = ValidationConstants.ROLE_LENGTH_MAX)
  @Enumerated(EnumType.STRING)
  private Role role;

  public User() {
  }

  public User(
      String username,
      String password,
      String firstName,
      String lastName,
      Date lastPasswordResetDate,
      Role role
  ) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.lastPasswordResetDate = lastPasswordResetDate;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getLastPasswordResetDate() {
    return lastPasswordResetDate;
  }

  public void setLastPasswordResetDate(Date lastPasswordResetDate) {
    this.lastPasswordResetDate = lastPasswordResetDate;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    User user = (User) object;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
