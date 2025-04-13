package com.ababaiev.repositories;

import com.ababaiev.models.MeterReading;
import com.ababaiev.models.UtilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeterReadingRepo extends JpaRepository<MeterReading, UUID> {
    List<MeterReading> findAllByUser_UsernameAndUtilityTypeOrderByTimestampDesc(String username, UtilityType utilityType);

    MeterReading findTopByUser_UsernameAndUtilityTypeOrderByTimestampDesc(String username, UtilityType utilityType);

}
