package net.parttimepolymath;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.parttimepolymath.api.TransferRequest;
import net.parttimepolymath.api.TransferResult;
import net.parttimepolymath.controller.Status;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.Transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * test harness to exercise the API running against a specific port. Note that this assumes we are running
 * the service on the same localhost as the test.
 * 
 * @author robert
 */
public class Tester {
    /**
     * date output formatter.
     */
    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    /**
     * the Jersey client being used.
     */
    private final transient Client client;
    /**
     * the base reference for all end points.
     */
    private final transient WebTarget baseTarget;

    /**
     * primary constructor.
     * 
     * @param port the port we hope to find the service on.
     */
    public Tester(final int port) {
        final JacksonJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jacksonJsonProvider.setMapper(new JodaMapper());

        ClientConfig cc = new ClientConfig(jacksonJsonProvider).register(JacksonFeature.class).register(JodaModule.class)
                .property(ClientProperties.CONNECT_TIMEOUT, Integer.toString(5000))
                .property(ClientProperties.READ_TIMEOUT, Integer.toString(5000));

        client = ClientBuilder.newClient(cc);
        baseTarget = client.target("http://localhost:" + port);
    }

    /**
     * run the various tests sequentially.
     */
    public void execute() {
        checkStatus();
        checkGetAccount();
        checkGetClient();
        checkDetails();
        checkTransfer();
    }

    /**
     * fetch an object from the web target by making the call and deserialising the result.
     * 
     * @param testMethod the name of the invoking method.
     * @param returnType the type of object the call should return.
     * @param target the target of the call.
     * @param T the type of object we are returning.
     * @return the object, if it can be found and deserialised, or null otherwise.
     */
    private <T> T fetchObjectFromTarget(final String testMethod, final Class<T> returnType, final WebTarget target) {
        try {
            System.out.println(testMethod + "() trying " + target.getUri().toString());
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            return parseResponse(testMethod, response, returnType);
        } catch (Exception ex) {
            System.out.println(testMethod + "() serious failure: " + ex.getMessage());
            return null;
        }
    }

    /**
     * post an object to a web target, and deal with the result.
     * 
     * @param returnType the type of data returned from the call.
     * @param target the target of the call.
     * @param payLoad the object to serialise and post.
     * @param T the type of object we are returning.
     * @return the response object if it can be found and deserialised, or null otherwise
     */
    private <T> T postObjectToTarget(final String testMethod, final Class<T> returnType, final WebTarget target, final Object payLoad) {
        try {
            System.out.println(testMethod + "() trying " + target.getUri().toString());
            Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(payLoad));
            return parseResponse(testMethod, response, returnType);
        } catch (Exception ex) {
            System.out.println(testMethod + "() serious failure: " + ex.getMessage());
            return null;
        }
    }

    /**
     * parse a response to extract an object, if possible.
     * 
     * @param testMethod the name of the invoking method.
     * @param response the response being parsed
     * @param returnType the type of object the call should return.
     * @return the object it if can be deserialised from the response, null otherwise.
     */
    private <T> T parseResponse(final String testMethod, final Response response, final Class<T> returnType) {
        if (response.getStatus() == 200) {
            T object = response.readEntity(returnType);
            System.out.println(testMethod + "() read " + object.toString());
            return object;
        } else {
            System.out.println(testMethod + "() FAILED - HTTP response = " + response.getStatus());
            return null;
        }

    }

    private void checkStatus() {
        Status status = fetchObjectFromTarget("checkStatus", Status.class, baseTarget.path("/status"));
        Validate.notNull(status);
    }

    private void checkGetAccount() {
        Account account = fetchObjectFromTarget("checkGetAccount", Account.class,
                baseTarget.path("/transfer/1.0/account/87a4dd04-385a-11e5-a151-feff819cdc9f"));
        Validate.notNull(account);

        account = fetchObjectFromTarget("checkGetAccount", Account.class, baseTarget.path("/transfer/1.0/account/EXPECTTOFAIL"));
        Validate.isTrue(account == null);
    }

    private void checkGetClient() {
        net.parttimepolymath.model.Client client = fetchObjectFromTarget("checkGetClient", net.parttimepolymath.model.Client.class,
                baseTarget.path("/transfer/1.0/client/146b6c7f-0b8a-43b9-b35d-6489e6daee92"));
        Validate.notNull(client);

        client = fetchObjectFromTarget("checkGetClient", net.parttimepolymath.model.Client.class,
                baseTarget.path("/transfer/1.0/client/EXPECTTOFAIL"));
        Validate.isTrue(client == null);
    }

    private void checkDetails() {
        dump(fetchObjectFromTarget("checkDetails", net.parttimepolymath.model.Client.class,
                baseTarget.path("/transfer/1.0/client/146b6c7f-0b8a-43b9-b35d-6489e6daee92")));
        dump(fetchObjectFromTarget("checkDetails", net.parttimepolymath.model.Client.class,
                baseTarget.path("/transfer/1.0/client/046b6c7f-0b8a-43b9-b35d-6489e6daee91")));
    }

    private void checkTransfer() {
        Account fromAccount = fetchObjectFromTarget("checkGetAccount", Account.class,
                baseTarget.path("/transfer/1.0/account/87a4dd04-385a-11e5-a151-feff819cdc9f"));
        Account toAccount = fetchObjectFromTarget("checkGetAccount", Account.class,
                baseTarget.path("/transfer/1.0/account/46fd58da-385a-11e5-a151-feff819cdc9f"));
        System.out.println("-- Starting from balance = " + fromAccount.getBalance().toString());
        System.out.println("-- Starting to   balance = " + toAccount.getBalance().toString());

        TransferRequest request = new TransferRequest(fromAccount.getAccountId(), toAccount.getAccountId(), 10000);
        TransferResult result = postObjectToTarget("checkTransfer", TransferResult.class, baseTarget.path("/transfer/1.0/transfer"),
                request);
        Validate.notNull(result);

        if (result.getResultCode() == 200) {
            Validate.notBlank(result.getTransactionId());
        } else {
            Validate.isTrue(StringUtils.isBlank(result.getTransactionId()));
        }

        System.out.println(result.toString());

        fromAccount = fetchObjectFromTarget("checkGetAccount", Account.class,
                baseTarget.path("/transfer/1.0/account/87a4dd04-385a-11e5-a151-feff819cdc9f"));
        toAccount = fetchObjectFromTarget("checkGetAccount", Account.class,
                baseTarget.path("/transfer/1.0/account/46fd58da-385a-11e5-a151-feff819cdc9f"));
        System.out.println("-- Final from balance = " + fromAccount.getBalance().toString());
        System.out.println("-- Final to   balance = " + toAccount.getBalance().toString());
    }

    private void dump(final net.parttimepolymath.model.Client client) {
        System.out.println("------------- Client Dump ----------------");
        System.out.println(String.format("Client ID   = %s", client.getClientId()));
        System.out.println(String.format("Client Name = %s", client.getName()));
        for (Account account : client.getAccounts()) {
            System.out.println(String.format("  Account ID = %s", account.getAccountId()));
            System.out.println(String.format("  Balance    = %.3f", account.getBalance().floatValue()));
            System.out.println(String.format("  Currency   = %s", account.getCurrency()));
            for (Transaction transaction : account.getTransactions()) {
                System.out.println(String.format("    Transaction ID = %s", transaction.getId().getTxId()));
                System.out.println(String.format("    Reference      = %s", transaction.getReference()));
                System.out.println(String.format("    Date           = %s", formatter.print(transaction.getDate())));
                System.out.println(String.format("    Amount         = %.3f", transaction.getAmount()));
            }
        }
        System.out.println("-----------------------------------------");
    }
}
