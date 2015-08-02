package net.parttimepolymath.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-08-01T16:01:49.224+0100")
@StaticMetamodel(Client.class)
public class Client_ {
	public static volatile SingularAttribute<Client, String> clientId;
	public static volatile SingularAttribute<Client, String> name;
	public static volatile ListAttribute<Client, Account> accounts;
}
