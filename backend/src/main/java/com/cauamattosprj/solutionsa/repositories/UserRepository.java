package com.cauamattosprj.solutionsa.repositories;

import com.cauamattosprj.solutionsa.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}