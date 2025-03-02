package com.shizzy.moneytransfer.repository;

import com.shizzy.moneytransfer.model.UserBeneficiaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBeneficiariesRepository extends JpaRepository<UserBeneficiaries, String>{
}
