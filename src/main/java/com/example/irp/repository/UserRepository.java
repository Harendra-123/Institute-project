package com.example.irp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.irp.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Sirf ye method rakhein, kyunki humne Entity mein 'userName' use kiya hai
    Optional<User> findByUserEmailAndPassword(String userEmail, String password);
}

