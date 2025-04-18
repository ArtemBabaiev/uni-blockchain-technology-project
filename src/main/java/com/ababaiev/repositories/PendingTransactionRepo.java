package com.ababaiev.repositories;

import com.ababaiev.models.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingTransactionRepo extends JpaRepository<PendingTransaction, Long> {
    List<PendingTransaction> findTop10ByOrderByTimestampAsc();
}
