package com.securebank.customer.repository;

import com.securebank.customer.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    
    @Query("SELECT uc FROM UserCredential uc JOIN FETCH uc.customer WHERE uc.username = :username")
    Optional<UserCredential> findByUsernameWithCustomer(String username);

    Optional<UserCredential> findByUsername(String username);

    boolean existsByUsername(String username);
}
