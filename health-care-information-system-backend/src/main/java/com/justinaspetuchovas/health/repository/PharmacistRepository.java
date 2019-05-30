package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.user.pharmacist.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for {@link Pharmacist} entities.
 */
public interface PharmacistRepository extends JpaRepository<Pharmacist, UUID> {
}
