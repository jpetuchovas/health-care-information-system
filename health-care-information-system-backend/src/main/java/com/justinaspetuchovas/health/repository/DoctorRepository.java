package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.user.doctor.Doctor;
import com.justinaspetuchovas.health.model.user.doctor.DoctorProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Repository for {@link Doctor} entities.
 */
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
  @Query(
      "SELECT doctor.id AS id, "
          + "doctor.firstName AS firstName, "
          + "doctor.lastName AS lastName, "
          + "doctor.username AS username, "
          + "doctor.specialization AS specialization "
          + "FROM Doctor AS doctor "
          + "WHERE doctor.firstName LIKE :firstName "
          + "AND doctor.lastName LIKE :lastName "
          + "AND doctor.username LIKE :username "
          + "AND doctor.specialization LIKE :specialization"
  )
  Slice<DoctorProjection> filterDoctorsByStartPattern(
      @Param("firstName") String firstName,
      @Param("lastName") String lastName,
      @Param("username") String username,
      @Param("specialization") String specialization,
      Pageable pageable
  );

  default Slice<DoctorProjection> filterDoctors(
      String firstName,
      String lastName,
      String username,
      String specialization,
      Pageable pageable
  ) {
    return filterDoctorsByStartPattern(
        firstName + "%",
        lastName + "%",
        username + "%",
        specialization + "%",
        pageable
    );
  }
}
