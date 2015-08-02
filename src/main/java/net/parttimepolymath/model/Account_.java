package net.parttimepolymath.model;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-08-01T15:52:59.752+0100")
@StaticMetamodel(Account.class)
public class Account_ {
	public static volatile SingularAttribute<Account, String> accountId;
	public static volatile SingularAttribute<Account, BigDecimal> balance;
	public static volatile SingularAttribute<Account, String> currency;
	public static volatile SingularAttribute<Account, Byte> open;
	public static volatile SingularAttribute<Account, Client> client;
	public static volatile ListAttribute<Account, Transaction> transactions;
}
