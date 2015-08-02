package net.parttimepolymath.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.parttimepolymath.controller.ControllerHolder;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.Client;

/**
 * Small Jersey service to report the status of the server.
 * 
 * @author robert
 */
@Path("/transfer/1.0")
public final class TransferService {
    @GET
    @Path("account/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountId") String accountId) {
        return ControllerHolder.getController().getAccount(accountId);
    }

    @GET
    @Path("client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Client getClient(@PathParam("clientId") String clientId) {
        return ControllerHolder.getController().getClient(clientId);
    }

    @POST
    @Path("transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransferResult doTransfer(final TransferRequest request) {
        return ControllerHolder.getController().doTransfer(request);
    }
}
