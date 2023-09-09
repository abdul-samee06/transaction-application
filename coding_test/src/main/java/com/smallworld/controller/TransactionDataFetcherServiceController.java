package com.smallworld.controller;

import com.smallworld.service.TransactionDataFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionDataFetcherServiceController {

    private final TransactionDataFetcherService transactionDataFetcherService;

    @Autowired
    public TransactionDataFetcherServiceController(TransactionDataFetcherService transactionDataFetcherService){
        this.transactionDataFetcherService = transactionDataFetcherService;
    }

    @GetMapping("/total-transaction-amount")
    public ResponseEntity<Double> getTotalTransactionAmount(){
        return transactionDataFetcherService.getTotalTransactionAmount();
    }

    @GetMapping("/total-transaction-amount-sent-by-sender/{senderFullName}")
    public ResponseEntity<Double> getTotalTransactionAmountSentBySender(@PathVariable("senderFullName") String senderFullName){
        return transactionDataFetcherService.getTotalTransactionAmountSentBy(senderFullName);
    }

    @GetMapping("/max-transaction-amount")
    public ResponseEntity<Double> getMaxTransactionAmount() {
        return transactionDataFetcherService.getMaxTransactionAmount();
    }

    @GetMapping("/count-unique-clients")
    public ResponseEntity<Long> countUniqueClients(){
       return transactionDataFetcherService.countUniqueClients();
    }

    @GetMapping("/has-open-compliance-issues/{clientFullName}")
    public ResponseEntity<Boolean> hasOpenComplianceIssues(@PathVariable("clientFullName") String clientFullName) {
        return transactionDataFetcherService.hasOpenComplianceIssues(clientFullName);
    }

    @GetMapping("/transactions-by-beneficiary-name")
    public ResponseEntity<Map<String,Object>> getTransactionsByBeneficiaryName(){
        return transactionDataFetcherService.getTransactionsByBeneficiaryName();
    }

    @GetMapping("/unsolved-issue-ids")
    public ResponseEntity<Set<Integer>> getUnsolvedIssueIds(){
        return transactionDataFetcherService.getUnsolvedIssueIds();
    }

    @GetMapping("/all-solved-issue-messages")
    public ResponseEntity<List<String>> getAllSolvedIssueMessages(){
        return transactionDataFetcherService.getAllSolvedIssueMessages();
    }

    @GetMapping("/top-3-transactions-by-amount")
    public ResponseEntity<List<Object>> getTop3TransactionsByAmount(){
        return transactionDataFetcherService.getTop3TransactionsByAmount();
    }

    @GetMapping("/top-sender")
    public ResponseEntity<Optional<Object>> getTopSender(){
        return transactionDataFetcherService.getTopSender();
    }
}
