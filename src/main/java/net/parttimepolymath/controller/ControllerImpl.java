package net.parttimepolymath.controller;

import net.parttimepolymath.api.TransferRequest;
import net.parttimepolymath.api.TransferResult;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.Client;
import net.parttimepolymath.model.DataStore;

import org.apache.commons.lang3.StringUtils;

public class ControllerImpl implements Controller {
    /**
     * the system status.
     */
    private final Status status = new Status("inactive");
    /**
     * injected DataStore.
     */
    private final DataStore dataStore;

    /**
     * primary constructor.
     * 
     * @param store a DataStore to inject.
     */
    public ControllerImpl(final DataStore store) {
        dataStore = store;
    }

    @Override
    public Status getStatus() {
        status.updateCount();
        return status;
    }

    @Override
    public void activate() {
        status.setStatus("active");
    }

    @Override
    public Account getAccount(final String accountId) {
        if (!StringUtils.equals("active", status.getStatus())) {
            return null;
        }
        status.updateCount();
        return dataStore.getAccount(accountId);
    }

    @Override
    public Client getClient(final String clientId) {
        if (!StringUtils.equals("active", status.getStatus())) {
            return null;
        }
        status.updateCount();
        return dataStore.getClient(clientId);
    }

    @Override
    public TransferResult doTransfer(final TransferRequest request) {
        if (!StringUtils.equals("active", status.getStatus())) {
            return UNAVAILABLE;
        }
        TransferProcessor processor = new TransferProcessor(request, dataStore);
        status.updateCount();
        return processor.execute();
    }
}
