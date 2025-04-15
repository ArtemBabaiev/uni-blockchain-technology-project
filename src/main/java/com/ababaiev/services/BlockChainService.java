package com.ababaiev.services;

import com.ababaiev.models.*;
import com.ababaiev.repositories.*;
import com.ababaiev.utils.CryptoUtils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlockChainService {
    @Autowired
    BlockTransactionRepo blockTransactionRepo;

    @Autowired
    PendingTransactionRepo pendingTransactionRepo;

    @Autowired
    BlockRepo blockRepo;

    @Autowired
    BillRepo billRepo;

    @Autowired
    private UserRepo userRepo;

    private static final PrivateKey privateKey;
    private static final PublicKey publicKey;

    static {
        try {
            byte[] privateBytes = BlockChainService.class.getClassLoader().getResourceAsStream("private.pem").readAllBytes();
            byte[] publicBytes = BlockChainService.class.getClassLoader().getResourceAsStream("public.pub").readAllBytes();
            privateKey = CryptoUtils.loadPrivateKey(privateBytes);
            publicKey = CryptoUtils.loadPublicKey(publicBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    @Scheduled(fixedRate = 20000, initialDelay = 20000) // 5 min
    void createBlock(){
        log.info("Creating block");
        List<PendingTransaction> pendingTransactionList = pendingTransactionRepo.findTop10ByOrderByTimestampAsc();

        if(pendingTransactionList.isEmpty()){
            log.info("No pending transactions");
            return;
        }

        Map<PendingTransaction, BlockTransaction> transactionMap = new HashMap<>();
        Map<String, User> users = new HashMap<>();

        for(var pendingTransaction : pendingTransactionList){
            var user = users.get(pendingTransaction.getUser().getId());
            if (user == null){
                users.put(pendingTransaction.getUser().getId(), pendingTransaction.getUser());
                user = users.get(pendingTransaction.getUser().getId());
            }
            boolean valid = CryptoUtils.verify(pendingTransaction.getHash(), pendingTransaction.getSignature(), pendingTransaction.getUser().getPublicKey());

            if (!valid || user.getBalance() < pendingTransaction.getAmount()){
                transactionMap.put(pendingTransaction, null);
                continue;
            }

            user.setBalance(user.getBalance() - pendingTransaction.getAmount());
            BlockTransaction blockTransaction = getBlockTransaction(pendingTransaction);
            transactionMap.put(pendingTransaction, blockTransaction);
        }

        Block latestBlock = getLatestBlock();
        String latestBlockHash64 = latestBlock.getHash();



        if (!validateLatestBlock(latestBlock)){
            throw new RuntimeException("Block verification failed");
        }

        List<BlockTransaction> blockTransactionList = transactionMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
        String transactionHash64 = buildMerkleRoot(blockTransactionList);

        String newBlockHash = "";
        long nonce = 0;
        String concat = null;
        for (; ; nonce++) {
            concat = transactionHash64 + latestBlockHash64 + nonce;
            newBlockHash = CryptoUtils.getHash64(concat);
            if (newBlockHash.startsWith("00")){
                break;
            }
        }
        Block newBlock = new Block();
        newBlock.setHash(newBlockHash);
        newBlock.setNonce(nonce);
        newBlock.setPreviousHash(latestBlockHash64);
        newBlock.setTimestamp(LocalDateTime.now());


        newBlock.setSignature(CryptoUtils.sign(newBlockHash, privateKey));
        log.info("Saving new block");

        newBlock = blockRepo.save(newBlock);

        List<Bill> affectedBills = new ArrayList<>();
        for (var entry : transactionMap.entrySet()) {
            var pendingTransaction = entry.getKey();
            var blockTransaction = entry.getValue();
            pendingTransaction.getBills().forEach(bill -> {
                bill.setPendingTransaction(null);
                bill.setBlockTransaction(blockTransaction);
                affectedBills.add(bill);
            });

            if (blockTransaction != null) {
                blockTransaction.setBlock(newBlock);
            }
        }
        billRepo.saveAll(affectedBills);
        pendingTransactionRepo.deleteAll(pendingTransactionList);
        blockTransactionRepo.saveAll(blockTransactionList);
        userRepo.saveAll(users.values());
    }

    private boolean validateLatestBlock(Block latestBlock) {
        if (latestBlock.isGenesisBlock()){
            return true;
        }
        for (var transaction : latestBlock.getTransactions()) {
            boolean valid = CryptoUtils.verify(transaction.getHash(), transaction.getSignature(), transaction.getUser().getPublicKey());
            if (!valid) {
                return false;
            }
        }

        String calculatedHash = CryptoUtils.getHash64(buildMerkleRoot(latestBlock.getTransactions()) + latestBlock.getPreviousHash() + latestBlock.getNonce());
        return CryptoUtils.verify(calculatedHash, latestBlock.getSignature(), publicKey.getEncoded());
    }

    private Block getLatestBlock() {
        var block = blockRepo.findTopByOrderByTimestampDesc();
        if (block == null) {
            block = new Block();
            block.setNonce(0);
            block.setHash("0");
            block.setGenesisBlock(true);
        }
        return block;
    }

    private static BlockTransaction getBlockTransaction(PendingTransaction transaction) {
        BlockTransaction blockTransaction = new BlockTransaction();
        blockTransaction.setTimestamp(transaction.getTimestamp());
        blockTransaction.setAmount(transaction.getAmount());
        blockTransaction.setHash(transaction.getHash());
        blockTransaction.setSignature(transaction.getSignature());
        blockTransaction.setUser(transaction.getUser());
        blockTransaction.setBills(transaction.getBills());

        return blockTransaction;
    }

    private String buildMerkleRoot(Collection<BlockTransaction> transactions) {
        List<String> hashes = transactions.stream().map(BlockTransaction::getHash).collect(Collectors.toList());
        while (hashes.size() > 1) {
            List<String> newHashes = new ArrayList<>();
            for (int i = 0; i < hashes.size(); i+=2) {
                if (i + 1 < hashes.size()) {
                    newHashes.add(CryptoUtils.getHash64(hashes.get(i) + hashes.get(i + 1))); // Hash pairs
                } else {
                    newHashes.add(CryptoUtils.getHash64(hashes.get(i) + hashes.get(i))); // Duplicate last hash if odd
                }
            }
            hashes = newHashes;
        }
        return hashes.get(0);
    }
}
