package com.coutinho.userservicemailsender.repository;

import com.coutinho.userservicemailsender.model.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {

    Confirmation findByToken(String token);
}
