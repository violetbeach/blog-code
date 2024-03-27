package com.violetbeach.transfer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
