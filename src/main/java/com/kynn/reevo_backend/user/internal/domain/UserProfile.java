package com.kynn.reevo_backend.user.internal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
public class UserProfile {

    @Id
    private UUID accountId;   // PK + FK

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    private String displayName;
    private String avatarUrl;
    private String bio;

    @Column(precision = 12, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    private LocalDate dob;
    private String gender;
}