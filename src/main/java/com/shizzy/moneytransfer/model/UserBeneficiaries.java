package com.shizzy.moneytransfer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBeneficiaries {

    @Id
    private String userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private List<Beneficiary> beneficiaries;
}