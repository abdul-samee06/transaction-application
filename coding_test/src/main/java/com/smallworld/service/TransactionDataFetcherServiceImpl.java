package com.smallworld.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallworld.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
public class TransactionDataFetcherServiceImpl implements TransactionDataFetcherService {

    private static final String FILE_PATH = "coding_test/transactions.json";

    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionDataFetcherServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Returns the sum of the amounts of all transactions
     */
    @Override
    public ResponseEntity<Double> getTotalTransactionAmount() {
        double totalAmount = 0;
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null) {
                for (TransactionDTO transactionDto : transactions) {
                    totalAmount += transactionDto.getAmount();
                }
            }
            return ResponseEntity.ok(totalAmount);

        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0.0);
        }
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    @Override
    public ResponseEntity<Double> getTotalTransactionAmountSentBy(String senderFullName) {
        double totalAmount = 0;
        boolean found = false;
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction: transactions){
                    if(transaction.getSenderFullName().equals(senderFullName)){
                        found = true;
                        totalAmount+=transaction.getAmount();
                    }
                }
            }
            if(found){
                return ResponseEntity.ok(totalAmount);
            } else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(totalAmount);
            }
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0.0);
        }
    }

    /**
     * Returns the highest transaction amount
     */
    @Override
    public ResponseEntity<Double> getMaxTransactionAmount() {

        double maxAmount = -1000d;
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction : transactions){
                    if(transaction.getAmount()>maxAmount){
                        maxAmount = transaction.getAmount();
                    }
                }
            }
            return ResponseEntity.ok(maxAmount);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0.0);
        }
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    @Override
    public ResponseEntity<Long> countUniqueClients() {
        List<String> uniqueClients = new ArrayList<>();
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction : transactions){
                    if(!uniqueClients.contains(transaction.getSenderFullName())){
                        uniqueClients.add(transaction.getSenderFullName());
                    }
                    if(!uniqueClients.contains(transaction.getBeneficiaryFullName())){
                        uniqueClients.add(transaction.getBeneficiaryFullName());
                    }
                }
            }
            return ResponseEntity.ok((long) uniqueClients.size());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    @Override
    public ResponseEntity<Boolean> hasOpenComplianceIssues(String clientFullName) {
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction:transactions){
                    if((clientFullName.equals(transaction.getSenderFullName())
                            || clientFullName.equals(transaction.getBeneficiaryFullName()))
                            && transaction.getIssueSolved().equals(Boolean.FALSE)){
                        return ResponseEntity.ok(Boolean.TRUE);
                    }
                }
            }
            return ResponseEntity.ok(Boolean.FALSE);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.FALSE);
        }
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    @Override
    public ResponseEntity<Map<String, Object>> getTransactionsByBeneficiaryName() {
        Map<String, Object> transactionMapByBeneficiaryName = new HashMap<>();
        try{
            List<TransactionDTO> transactions = getTransactions();
            for(TransactionDTO transaction : transactions){
                List<TransactionDTO> beneficiaryTransactions = new ArrayList<>();
                if(transactionMapByBeneficiaryName.containsKey(transaction.getBeneficiaryFullName())){
                    beneficiaryTransactions = (List<TransactionDTO>) transactionMapByBeneficiaryName.get(transaction.getBeneficiaryFullName());
                    beneficiaryTransactions.add(transaction);
                }
                else{
                    beneficiaryTransactions.add(transaction);
                }
                transactionMapByBeneficiaryName.put(transaction.getBeneficiaryFullName(),beneficiaryTransactions);
            }
            return ResponseEntity.ok(transactionMapByBeneficiaryName);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    @Override
    public ResponseEntity<Set<Integer>> getUnsolvedIssueIds() {
        Set<Integer> unsolvedIssueIds = new HashSet<>();
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction : transactions){
                    if(transaction.getIssueSolved().equals(Boolean.FALSE)){
                        unsolvedIssueIds.add(transaction.getIssueId());
                    }
                }
            }
            return ResponseEntity.ok(unsolvedIssueIds);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Returns a list of all solved issue messages
     */
    @Override
    public ResponseEntity<List<String>> getAllSolvedIssueMessages() {

        List<String> solvedIssueMessages = new ArrayList<>();

        try{
            List<TransactionDTO> transactions = getTransactions();
            for(TransactionDTO transaction : transactions){
                if(transaction.getIssueSolved().equals(Boolean.TRUE)){
                    solvedIssueMessages.add(transaction.getIssueMessage());
                }
            }
            return ResponseEntity.ok(solvedIssueMessages);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Returns the 3 transactions with highest amount sorted by amount descending
     */
    @Override
    public ResponseEntity<List<Object>> getTop3TransactionsByAmount() {
        List<Object> highestAmountTransactions = new ArrayList<>();

        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                Collections.sort(transactions, new Comparator<>() {
                    @Override
                    public int compare(TransactionDTO t1, TransactionDTO t2) {
                        return Double.compare(t2.getAmount(),t1.getAmount());
                    }
                });

                for(int i=0; i<3; i++){
                    highestAmountTransactions.add(transactions.get(i));
                }
            }
            return ResponseEntity.ok(highestAmountTransactions);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Returns the sender with the most total sent amount
     */
    @Override
    public ResponseEntity<Optional<Object>> getTopSender() {
        Map<String, Double> sendersWithAmount = new HashMap<>();
        String topSender = "";
        try{
            List<TransactionDTO> transactions = getTransactions();
            double maxAmount =-1000d;
            for(TransactionDTO transaction : transactions){
                Double amount = transaction.getAmount();
                if(sendersWithAmount.containsKey(transaction.getSenderFullName())) {
                    amount += sendersWithAmount.get(transaction.getSenderFullName());
                }
                if(amount>maxAmount){
                    maxAmount=amount;
                    topSender = transaction.getSenderFullName();
                }
                sendersWithAmount.put(transaction.getSenderFullName(), amount);
            }
            return ResponseEntity.ok(Optional.ofNullable(topSender));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private List<TransactionDTO> getTransactions() throws FileNotFoundException {
        List<TransactionDTO> transactionDtoList = null;
        try {
            transactionDtoList = objectMapper.readValue(new File(FILE_PATH), new TypeReference<List<TransactionDTO>>(){});

        } catch (IOException e) {
            throw new FileNotFoundException("File not found");
        }

        return transactionDtoList;
    }
}
