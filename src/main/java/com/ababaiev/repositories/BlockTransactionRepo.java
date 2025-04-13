package com.ababaiev.repositories;

import com.ababaiev.models.BlockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockTransactionRepo extends JpaRepository<BlockTransaction, Long> {
}
