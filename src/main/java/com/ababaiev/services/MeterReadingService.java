package com.ababaiev.services;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.models.Bill;
import com.ababaiev.models.MeterReading;
import com.ababaiev.models.User;
import com.ababaiev.models.UtilityType;
import com.ababaiev.repositories.BillRepo;
import com.ababaiev.repositories.MeterReadingRepo;
import com.ababaiev.repositories.UserRepo;
import com.ababaiev.utils.CryptoUtils;
import com.ababaiev.views.profile.models.CreateReadingModel;
import com.ababaiev.views.profile.models.ReadingGridModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MeterReadingService {
    @Autowired
    private MeterReadingRepo meterReadingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BillRepo billRepo;

    public List<ReadingGridModel> findAllForCurrentUser(UtilityType utilityType) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MeterReading> readings = this.meterReadingRepo.findAllByUser_UsernameAndUtilityTypeOrderByTimestampDesc(username, utilityType);

        return readings.stream().map(this::mapToGridModel).collect(Collectors.toList());
    }

    @Transactional
    public MeterReading createMeterReading(CreateReadingModel model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username);
        var previousReading = meterReadingRepo.findTopByUser_UsernameAndUtilityTypeOrderByTimestampDesc(username, model.getUtilityType());
        int previousAmount = previousReading == null ? 0 : previousReading.getReadingValue();
        if (model.getReading() < previousAmount) {
            throw new BadRequestException("New reading must be greater than or equal to the previous reading (" + previousAmount + ").");
        }
        double difference = model.getReading() - previousAmount;
        MeterReading meterReading = new MeterReading();
        meterReading.setReadingValue(model.getReading());
        meterReading.setUtilityType(model.getUtilityType());
        meterReading.setPreviousReading(previousReading);
        meterReading.setTimestamp(LocalDateTime.now());
        meterReading.setUser(user);

        Bill bill = new Bill();
        bill.setBillingDate(LocalDateTime.now());
        bill.setAmountDue(model.getUtilityType().getRate() * difference);
        bill.setMeterReading(meterReading);
        bill.setHash(CryptoUtils.getHash64(getBillString(bill)));

        meterReading.setBill(bill);
        meterReading.setHash(CryptoUtils.getHash64(getReadingString(meterReading)));

        log.info("User {} submitted a new {} reading: {} (diff: {})",
                username, model.getUtilityType(), model.getReading(), difference);

        return meterReadingRepo.save(meterReading);
    }

    private String getReadingString(MeterReading meterReading) {

        return "" + meterReading.getUtilityType()
                + meterReading.getTimestamp()
                + meterReading.getReadingValue()
                + (meterReading.getPreviousReading() == null ? "0" : meterReading.getPreviousReading().getHash());
    }

    private String getBillString(Bill bill) {
        return "" + bill.getAmountDue() + bill.getBillingDate() + bill.getMeterReading().getUtilityType() + bill.getMeterReading().getHash();
    }

    private ReadingGridModel mapToGridModel(MeterReading meterReading) {
        ReadingGridModel gridModel = new ReadingGridModel();
        gridModel.setReadingValue(meterReading.getReadingValue());
        gridModel.setTimestamp(meterReading.getTimestamp());
        return gridModel;
    }
}
