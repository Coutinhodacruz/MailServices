package com.coutinho.userservicemailsender.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "confirmations")
public class Confirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void generateTokenAndCreateDate() {
        this.token = UUID.randomUUID().toString();
        this.createDate = LocalDateTime.now();
    }

    public Confirmation(User user) {
        this.user = user;
    }
}
