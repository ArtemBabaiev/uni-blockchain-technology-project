package com.ababaiev.repositories;

import com.ababaiev.models.Bill;
import com.ababaiev.models.MeterReading;
import com.ababaiev.models.UtilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepo extends JpaRepository<Bill, UUID> {
    List<Bill> findAllByMeterReading_User_UsernameAndMeterReading_UtilityTypeOrderByBillingDateDesc(String username, UtilityType utilityType);
}
