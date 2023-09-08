package com.smallworld.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TransactionDataFetcherService {

    double getTotalTransactionAmount();
    double getTotalTransactionAmountSentBy(String senderFullName);
    double getMaxTransactionAmount();
    long countUniqueClients();
    boolean hasOpenComplianceIssues(String clientFullName);
    Map<String, Object> getTransactionsByBeneficiaryName();
    Set<Integer> getUnsolvedIssueIds();
    List<String> getAllSolvedIssueMessages();
    List<Object> getTop3TransactionsByAmount();
    Optional<Object> getTopSender();

}
