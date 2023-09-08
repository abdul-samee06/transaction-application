package com.smallworld.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallworld.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public double getTotalTransactionAmount() {
        double totalAmount = 0;
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null) {
                for (TransactionDTO transactionDto : transactions) {
                    totalAmount += transactionDto.getAmount();
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return totalAmount;
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    @Override
    public double getTotalTransactionAmountSentBy(String senderFullName) {
        double totalAmount = 0;
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction: transactions){
                    if(transaction.getSenderFullName().equals(senderFullName)){
                        totalAmount+=transaction.getAmount();
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return totalAmount;
    }

    /**
     * Returns the highest transaction amount
     */
    @Override
    public double getMaxTransactionAmount() {

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
        } catch (Exception e){
            e.printStackTrace();
        }
        return maxAmount;
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    @Override
    public long countUniqueClients() {
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
        } catch (Exception e){
            e.printStackTrace();
        }

        return uniqueClients.size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    @Override
    public boolean hasOpenComplianceIssues(String clientFullName) {
        try{
            List<TransactionDTO> transactions = getTransactions();
            if(transactions!=null){
                for(TransactionDTO transaction:transactions){
                    if((clientFullName.equals(transaction.getSenderFullName())
                            || clientFullName.equals(transaction.getBeneficiaryFullName()))
                            && transaction.getIssueSolved().equals(Boolean.FALSE)){
                        return true;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    @Override
    public Map<String, Object> getTransactionsByBeneficiaryName() {
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
        } catch (Exception e){
            e.printStackTrace();
        }

        return transactionMapByBeneficiaryName;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    @Override
    public Set<Integer> getUnsolvedIssueIds() {
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return unsolvedIssueIds;
    }

    /**
     * Returns a list of all solved issue messages
     */
    @Override
    public List<String> getAllSolvedIssueMessages() {

        List<String> solvedIssueMessages = new ArrayList<>();

        try{
            List<TransactionDTO> transactions = getTransactions();
            for(TransactionDTO transaction : transactions){
                if(transaction.getIssueSolved().equals(Boolean.TRUE)){
                    solvedIssueMessages.add(transaction.getIssueMessage());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return solvedIssueMessages;
    }

    /**
     * Returns the 3 transactions with highest amount sorted by amount descending
     */
    @Override
    public List<Object> getTop3TransactionsByAmount() {
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

        } catch (Exception e){
            e.printStackTrace();
        }

        return highestAmountTransactions;
    }

    /**
     * Returns the sender with the most total sent amount
     */
    @Override
    public Optional<Object> getTopSender() {
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.ofNullable(topSender);
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
