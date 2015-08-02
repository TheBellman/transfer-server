package net.parttimepolymath.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DataStoreTest {
    private static DataStore instance;

    @BeforeClass
    public static void setUpClass() throws Exception {
        String script = new String(Files.readAllBytes(Paths.get(DataStoreTest.class.getResource("/createTest.sql").toURI())));
        instance = DataStoreFactory.makeDataStore(script);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("clearing test database for in-memory database context");
        DataStoreFactory.shutdownDatabase();
    }

    @Test
    public void testGetClients() {
        List<Client> result = instance.getClients();
        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    public void testGetClient() throws IOException {
        Client result = instance.getClient("JOHN");
        assertNull(result);

        result = instance.getClient(null);
        assertNull(result);

        result = instance.getClient("046b6c7f-0b8a-43b9-b35d-6489e6daee91");
        assertNotNull(result);
        assertNotNull(result.getAccounts());
        assertEquals(4, result.getAccounts().size());
    }

    @Test
    public void testGetAccounts() {
        List<Account> result = instance.getAccounts(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        result = instance.getAccounts("JANET");
        assertNotNull(result);
        assertTrue(result.isEmpty());

        result = instance.getAccounts("046b6c7f-0b8a-43b9-b35d-6489e6daee91");
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    public void testGetAccount() {
        Account result = instance.getAccount(null);
        assertNull(result);

        result = instance.getAccount("show me da money");
        assertNull(result);

        result = instance.getAccount("87a4d7aa-385a-11e5-a151-feff819cdc9f");
        assertNotNull(result);
        assertEquals("USD", result.getCurrency());
        assertFalse(result.isOpen());
    }

    @Test
    public void testGetTransactions() {
        List<Transaction> result = instance.getTransactions(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        result = instance.getTransactions("87a4d7aa-385a-11e5-a151-feff819cdc9f");
        assertNotNull(result);
        assertEquals(5, result.size());
    }

    /*
     * slightly dodgy test to verify that we are getting the expected JSON date conversion happening.
     */
    @Test
    public void testDateFormat() throws JsonProcessingException {
        List<Transaction> result = instance.getTransactions("87a4dd04-11e5-a151-385a-feff819cdc9f");
        assertNotNull(result);

        ObjectWriter ow = new ObjectMapper().writer();
        String json = ow.writeValueAsString(result.get(0));
        assertTrue(StringUtils.contains(json, "\"date\":\"2015-08-08T20:13:17.000Z\""));
    }

    @Test
    public void testAddTransactions() throws Exception {
        Account fromAccount = instance.getAccount("adfd52b2-389e-11e5-a151-feff819cdc9f");
        Account toAccount = instance.getAccount("adfd560e-389e-11e5-a151-feff819cdc9f");
        assertTrue(toAccount.getBalance().compareTo(new BigDecimal("10000.00")) == 0);
        assertTrue(fromAccount.getBalance().compareTo(new BigDecimal("10000.00")) == 0);

        BigDecimal amount = new BigDecimal("100.00");

        Transaction fromTransaction = new Transaction();
        fromTransaction.setAccount(fromAccount);
        fromTransaction.setAmount(amount.negate());
        fromTransaction.setDate(DateTime.now(DateTimeZone.UTC));
        fromTransaction.setReference(toAccount.getAccountId());
        TransactionPK fromKey = new TransactionPK();
        fromKey.setAccountId(fromAccount.getAccountId());
        fromKey.setTxId(UUID.randomUUID().toString());
        fromTransaction.setId(fromKey);

        Transaction toTransaction = new Transaction();
        toTransaction.setAccount(toAccount);
        toTransaction.setAmount(amount);
        toTransaction.setDate(fromTransaction.getDate());
        toTransaction.setReference(fromTransaction.getId().getTxId());
        TransactionPK toKey = new TransactionPK();
        toKey.setAccountId(toAccount.getAccountId());
        toKey.setTxId(UUID.randomUUID().toString());
        toTransaction.setId(toKey);

        instance.addTransactions(fromTransaction, toTransaction);

        fromAccount = fromTransaction.getAccount();
        toAccount = toTransaction.getAccount();

        assertTrue(toAccount.getBalance().compareTo(new BigDecimal("10100.00")) == 0);
        assertTrue(fromAccount.getBalance().compareTo(new BigDecimal("9900.00")) == 0);
    }
}
