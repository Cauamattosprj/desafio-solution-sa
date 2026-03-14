package com.cauamattosprj.solutionsa.repositories;

import com.cauamattosprj.solutionsa.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findAllByUserId(UUID userId);

    Optional<Address> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("UPDATE Address a SET a.main = false WHERE a.user.id = :userId AND a.id <> :excludeId")
    void unsetMainByUserId(@Param("userId") UUID userId, @Param("excludeId") UUID excludeId);
}