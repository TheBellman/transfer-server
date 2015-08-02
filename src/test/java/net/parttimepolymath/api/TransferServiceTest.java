package net.parttimepolymath.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.UUID;

import net.parttimepolymath.controller.Controller;
import net.parttimepolymath.controller.ControllerHolder;
import net.parttimepolymath.controller.Status;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.Client;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransferServiceTest {
    @Mock
    private Controller controller;
    private Account testAccount;
    private Client testClient;
    private TransferResult testResult;
    private TransferService instance;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ControllerHolder.reset();
        ControllerHolder.setController(controller);
        when(controller.getStatus()).thenReturn(new Status("testmode"));
        testAccount = new Account();
        testClient = new Client();
        testResult = new TransferResult(200, "OK", UUID.randomUUID().toString());
        when(controller.getAccount(anyString())).thenReturn(testAccount);
        when(controller.getClient(anyString())).thenReturn(testClient);
        when(controller.doTransfer(any(TransferRequest.class))).thenReturn(testResult);
        instance = new TransferService();
    }

    @Test
    public void testGetAccount() {
        assertEquals(testAccount, instance.getAccount("id"));
    }

    @Test
    public void testGetClient() {
        assertEquals(testClient, instance.getClient("id"));
    }

    @Test
    public void testDoTransfer() {
        assertEquals(testResult, instance.doTransfer(new TransferRequest("id", "id", 10)));
    }
}
