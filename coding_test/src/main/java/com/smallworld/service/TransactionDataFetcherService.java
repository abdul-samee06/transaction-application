package com.smallworld.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TransactionDataFetcherService {

    ResponseEntity<Double> getTotalTransactionAmount();
    ResponseEntity<Double> getTotalTransactionAmountSentBy(String senderFullName);
    ResponseEntity<Double> getMaxTransactionAmount();
    ResponseEntity<Long> countUniqueClients();
    ResponseEntity<Boolean> hasOpenComplianceIssues(String clientFullName);
    ResponseEntity<Map<String, Object>> getTransactionsByBeneficiaryName();
    ResponseEntity<Set<Integer>> getUnsolvedIssueIds();
    ResponseEntity<List<String>> getAllSolvedIssueMessages();
    ResponseEntity<List<Object>> getTop3TransactionsByAmount();
    ResponseEntity<Optional<Object>> getTopSender();

}
