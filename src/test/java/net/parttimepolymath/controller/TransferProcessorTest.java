package net.parttimepolymath.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import net.parttimepolymath.api.TransferRequest;
import net.parttimepolymath.api.TransferResult;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.DataStore;
import net.parttimepolymath.model.Transaction;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransferProcessorTest {
    @Mock
    private DataStore dataStore;

    private Account testAccountFrom;
    private Account testAccountTo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testAccountFrom = new Account();
        testAccountFrom.setOpen(true);
        testAccountTo = new Account();
        testAccountTo.setOpen(true);
    }

    @Test
    public void testNoDestAccount() {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);

        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);
        when(dataStore.getAccount("dest id")).thenReturn(null);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(404, result.getResultCode());
        assertEquals("To Account not found", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void tesClosedDestAccount() {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);

        testAccountTo.setOpen(false);
        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);
        when(dataStore.getAccount("dest id")).thenReturn(testAccountTo);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(404, result.getResultCode());
        assertEquals("To Account not open", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void testNoSourceAccount() {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);

        when(dataStore.getAccount("source id")).thenReturn(null);
        when(dataStore.getAccount("dest id")).thenReturn(testAccountTo);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(404, result.getResultCode());
        assertEquals("From Account not found", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void testClosedSourceAccount() {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);

        testAccountFrom.setOpen(false);
        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);
        when(dataStore.getAccount("dest id")).thenReturn(testAccountTo);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(404, result.getResultCode());
        assertEquals("From Account not open", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void testSameAccount() {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "source id", 1000), dataStore);

        testAccountFrom.setOpen(false);
        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(400, result.getResultCode());
        assertEquals("Request is not well-formed", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void testInsufficientFunds() {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);
        testAccountFrom.setBalance(BigDecimal.ZERO);
        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);
        when(dataStore.getAccount("dest id")).thenReturn(testAccountTo);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(520, result.getResultCode());
        assertEquals("Insufficient funds", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void testFailure() throws Exception {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);
        testAccountFrom.setBalance(BigDecimal.valueOf(2000, 2));
        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);
        when(dataStore.getAccount("dest id")).thenReturn(testAccountTo);
        doThrow(new Exception()).when(dataStore).addTransactions(any(Transaction.class), any(Transaction.class));

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(503, result.getResultCode());
        assertEquals("Internal Error", result.getResultMessage());
        assertTrue(StringUtils.isBlank(result.getTransactionId()));
    }

    @Test
    public void testSuccess() throws Exception {
        TransferProcessor instance = new TransferProcessor(new TransferRequest("source id", "dest id", 1000), dataStore);
        testAccountFrom.setBalance(BigDecimal.valueOf(2000, 2));
        when(dataStore.getAccount("source id")).thenReturn(testAccountFrom);
        when(dataStore.getAccount("dest id")).thenReturn(testAccountTo);

        TransferResult result = instance.execute();
        assertNotNull(result);
        assertEquals(200, result.getResultCode());
        assertEquals("OK", result.getResultMessage());
        assertFalse(StringUtils.isBlank(result.getTransactionId()));
    }

}
