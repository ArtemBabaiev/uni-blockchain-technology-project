package com.ababaiev.services;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.models.*;
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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Bill> bills = billRepo.findAllById(payBillModel.getBillIds());
        double totalAmount = bills.stream().mapToDouble(Bill::getAmountDue).sum();

        if (user.getBalance() < totalAmount) {
            throw new BadRequestException("Not enough funds on balance");
        }

        PendingTransaction pendingTransaction = new PendingTransaction();
        pendingTransaction.setAmount(totalAmount);
        pendingTransaction.setBills(bills);
        pendingTransaction.setTimestamp(LocalDateTime.now());
        pendingTransaction.setUser(user);

        for (Bill bill: bills) {
            bill.setPendingTransaction(pendingTransaction);
        }

        String hash64 = CryptoUtils.getHash64(getTransactionString(pendingTransaction));
        pendingTransaction.setHash(hash64);

        byte[] signature = CryptoUtils.sign(hash64, payBillModel.getPrivateKey());
        pendingTransaction.setSignature(signature);

        billRepo.saveAll(bills);
        return pendingTransactionRepo.save(pendingTransaction);

    }

    private static String getTransactionString(PendingTransaction pendingTransaction) {
        return pendingTransaction.getUser().getId()
                + pendingTransaction.getAmount()
                + pendingTransaction.getTimestamp()
                + pendingTransaction.getUser().getId()
                + pendingTransaction.getBills().stream().sorted(Comparator.comparing(Bill::getBillingDate)).map(b -> b.getId().toString()).collect(Collectors.joining (""));
    }

    public static String getTransactionString(BlockTransaction blockTransaction) {
        return blockTransaction.getUser().getId()
                + blockTransaction.getAmount()
                + blockTransaction.getTimestamp()
                + blockTransaction.getUser().getId()
                + blockTransaction.getBills().stream().sorted(Comparator.comparing(Bill::getBillingDate)).map(b -> b.getId().toString()).collect(Collectors.joining (""));
    }
}
