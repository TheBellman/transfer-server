package net.parttimepolymath.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import net.parttimepolymath.api.TransferRequest;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.Client;
import net.parttimepolymath.model.DataStore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ControllerImplTest {
    @Mock
    private DataStore dataStore;

    private Controller instance;

    private Account testAccount;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        instance = new ControllerImpl(dataStore);
        testAccount = new Account();
        testClient = new Client();
        when(dataStore.getAccount(anyString())).thenReturn(testAccount);
        when(dataStore.getClient(anyString())).thenReturn(testClient);
    }

    @Test
    public void testGetStatus() {
        assertNotNull(instance.getStatus());
        assertEquals("inactive", instance.getStatus().getStatus());
    }

    @Test
    public void testActivate() {
        assertNotNull(instance.getStatus());
        assertEquals("inactive", instance.getStatus().getStatus());
        instance.activate();
        assertEquals("active", instance.getStatus().getStatus());
    }

    @Test
    public void testGetClient() {
        instance.activate();
        assertEquals(testClient, instance.getClient("id"));
    }

    @Test
    public void testGetAccount() {
        instance.activate();
        assertEquals(testAccount, instance.getAccount("id"));
    }

    @Test
    public void testNullRequest() {
        instance.activate();
        assertEquals(TransferProcessor.BAD_RESULT, instance.doTransfer(null));
    }

    @Test
    public void testBadRequest() {
        instance.activate();
        assertEquals(TransferProcessor.BAD_RESULT, instance.doTransfer(new TransferRequest("", "", 0)));
        assertEquals(TransferProcessor.BAD_RESULT, instance.doTransfer(new TransferRequest("", "acct id", 0)));
        assertEquals(TransferProcessor.BAD_RESULT, instance.doTransfer(new TransferRequest("acct id", "", 0)));
    }

    @Test
    public void testNotActive() {
        instance.getStatus().setStatus("inactive");
        assertEquals(Controller.UNAVAILABLE, instance.doTransfer(new TransferRequest("acct id", "other id", 0)));
    }
}
