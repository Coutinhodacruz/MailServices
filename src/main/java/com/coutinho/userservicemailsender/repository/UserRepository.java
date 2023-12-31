package com.coutinho.userservicemailsender.repository;

import com.coutinho.userservicemailsender.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


    User findByEmailIgnoreCase(String email);

    Boolean existsByEmail(String email);
}
