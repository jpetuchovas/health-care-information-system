package com.justinaspetuchovas.health.model.user.pharmacist;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.prescription.PurchaseFact;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity that represents a pharmacist.
 */
@Entity
@Table(name = "pharmacists")
public class Pharmacist extends User {
  @Column(length = ValidationConstants.WORKPLACE_LENGTH_MAX)
  private String workplace;

  @OneToMany(mappedBy = "pharmacist")
  private Set<PurchaseFact> purchaseFacts = new HashSet<>();

  public Pharmacist() {
  }

  public Pharmacist(
      String username,
      String password,
      String firstName,
      String lastName,
      Date lastPasswordResetDate,
      String workplace
  ) {
    super(username, password, firstName, lastName, lastPasswordResetDate, Role.PHARMACIST);
    this.workplace = workplace;
  }

  public String getWorkplace() {
    return workplace;
  }

  public void setWorkplace(String workplace) {
    this.workplace = workplace;
  }

  public Set<PurchaseFact> getPurchaseFacts() {
    return purchaseFacts;
  }

  public void addPurchaseFact(PurchaseFact purchaseFact) {
    purchaseFacts.add(purchaseFact);
  }
}
