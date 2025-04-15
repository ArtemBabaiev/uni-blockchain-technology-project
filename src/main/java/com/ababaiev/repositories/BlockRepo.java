package com.ababaiev.repositories;

import com.ababaiev.models.Block;
import com.ababaiev.models.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepo extends JpaRepository<Block, Long> {
    Block findTopByOrderByTimestampDesc();
}
