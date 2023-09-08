package com.smallworld.controller;

import com.smallworld.service.TransactionDataFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public double getTotalTransactionAmount(){
        return transactionDataFetcherService.getTotalTransactionAmount();
    }

    @GetMapping("/total-transaction-amount-sent-by-sender/{senderFullName}")
    public double getTotalTransactionAmountSentBySender(@PathVariable("senderFullName") String senderFullName){
        return transactionDataFetcherService.getTotalTransactionAmountSentBy(senderFullName);
    }

    @GetMapping("/max-transaction-amount")
    public double getMaxTransactionAmount(){
        return transactionDataFetcherService.getMaxTransactionAmount();
    }

    @GetMapping("/count-unique-clients")
    public long countUniqueClients(){
        return transactionDataFetcherService.countUniqueClients();
    }

    @GetMapping("/has-open-compliance-issues/{clientFullName}")
    public boolean hasOpenComplianceIssues(@PathVariable("clientFullName") String clientFullName){
        return transactionDataFetcherService.hasOpenComplianceIssues(clientFullName);
    }

    @GetMapping("/transactions-by-beneficiary-name")
    public Map<String,Object> getTransactionsByBeneficiaryName(){
        return transactionDataFetcherService.getTransactionsByBeneficiaryName();
    }

    @GetMapping("/unsolved-issue-ids")
    public Set<Integer> getUnsolvedIssueIds(){
        return transactionDataFetcherService.getUnsolvedIssueIds();
    }

    @GetMapping("/all-solved-issue-messages")
    public List<String> getAllSolvedIssueMessages(){
        return transactionDataFetcherService.getAllSolvedIssueMessages();
    }

    @GetMapping("/top-3-transactions-by-amount")
    public List<Object> getTop3TransactionsByAmount(){
        return transactionDataFetcherService.getTop3TransactionsByAmount();
    }

    @GetMapping("/top-sender")
    public Optional<Object> getTopSender(){
        return transactionDataFetcherService.getTopSender();
    }
}
