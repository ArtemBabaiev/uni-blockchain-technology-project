package com.ababaiev.services;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.exceptions.NotFoundException;
import com.ababaiev.models.Bill;
import com.ababaiev.models.UtilityType;
import com.ababaiev.repositories.BillRepo;
import com.ababaiev.views.bill.models.PayBillModel;
import com.ababaiev.views.profile.models.BillGridModel;
import com.ababaiev.views.profile.models.BillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BillService {
    @Autowired
    private BillRepo billRepo;

    public List<BillGridModel> findAllForCurrentUser(UtilityType utilityType) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Bill> bills = this.billRepo.findAllByMeterReading_User_UsernameAndMeterReading_UtilityTypeOrderByBillingDateDesc(username, utilityType);

        return bills.stream().map(this::mapToGridModel).toList();
    }

    private BillGridModel mapToGridModel(Bill bill) {
        BillGridModel billGridModel = new BillGridModel();
        billGridModel.setId(bill.getId());
        billGridModel.setAmountDue(bill.getAmountDue());
        billGridModel.setBillingDate(bill.getBillingDate());
        billGridModel.setStatus(getStatus(bill));
        return billGridModel;
    }

    public Bill getBill(UUID billId) {
        Bill bill = billRepo.findById(billId).orElseThrow(() -> new NotFoundException("Bill not found"));
        var status = getStatus(bill);

        if (status != BillStatus.UNPAID) {
            throw new BadRequestException("Bill should be unpaid");
        }

        return bill;
    }

    private BillStatus getStatus(Bill bill) {
        if (bill.getBlockTransaction() != null) {
            return BillStatus.PAID;
        } else if (bill.getPendingTransaction() != null){
            return BillStatus.PROCESSING;
        } else {
            return BillStatus.UNPAID;
        }
    }
}
