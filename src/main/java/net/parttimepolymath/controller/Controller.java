package net.parttimepolymath.controller;

import net.parttimepolymath.api.TransferRequest;
import net.parttimepolymath.api.TransferResult;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.Client;

public interface Controller {
    /**
     * result to return when the service is not active.
     */
    TransferResult UNAVAILABLE = new TransferResult(503, "Service is unavailable", "");

    /**
     * report the status of the service.
     * 
     * @return the status of the service.
     */
    Status getStatus();

    /**
     * set the service to be active.
     */
    void activate();

    /**
     * retrieve a specified account.
     * 
     * @param accountId
     * @return the client.
     */
    Account getAccount(String accountId);

    /**
     * retrieve a specified client.
     * 
     * @param clientId the client id of interest.
     * @return the client.
     */
    Client getClient(String clientId);

    /**
     * perform a transfer and return the result.
     * 
     * @param request the request to act on.
     * @return the result to return.
     */
    TransferResult doTransfer(TransferRequest request);
}
