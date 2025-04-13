package com.ababaiev.services;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.models.Bill;
import com.ababaiev.models.PendingTransaction;
import com.ababaiev.models.User;
import com.ababaiev.repositories.BillRepo;
import com.ababaiev.repositories.BlockTransactionRepo;
import com.ababaiev.repositories.PendingTransactionRepo;
import com.ababaiev.repositories.UserRepo;
import com.ababaiev.utils.CryptoUtils;
import com.ababaiev.views.bill.models.PayBillModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private BlockTransactionRepo blockTransactionRepo;

    @Autowired
    private PendingTransactionRepo pendingTransactionRepo;

    @Autowired
    private BillRepo billRepo;

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public PendingTransaction createPendingTransaction(PayBillModel payBillModel) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username);
        Bill bill = billRepo.findById(payBillModel.getBillId()).orElseThrow(() -> new BadRequestException("Bill not found"));
        PendingTransaction pendingTransaction = new PendingTransaction();
        pendingTransaction.setAmount(bill.getAmountDue());
        pendingTransaction.setUtilityType(bill.getMeterReading().getUtilityType());
        pendingTransaction.setBill(bill);
        pendingTransaction.setUser(user);
        String hash64 = CryptoUtils.getHash64(getTransactionString(pendingTransaction));

        pendingTransaction.setHash(hash64);
        byte[] signature = CryptoUtils.sign(hash64, payBillModel.getPrivateKey());
        pendingTransaction.setSignature(signature);
        return pendingTransactionRepo.save(pendingTransaction);

    }

    private static String getTransactionString(PendingTransaction pendingTransaction) {
        return "" + pendingTransaction.getUser().getId()
                + pendingTransaction.getAmount()
                + pendingTransaction.getTimestamp()
                + pendingTransaction.getUtilityType()
                + pendingTransaction.getBill().getMeterReading().getReadingValue()
                + (pendingTransaction.getBill().getMeterReading().getPreviousReading() == null? 0: pendingTransaction.getBill().getMeterReading().getPreviousReading().getReadingValue());
    }
}
