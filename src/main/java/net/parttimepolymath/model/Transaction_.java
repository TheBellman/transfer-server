package net.parttimepolymath.model;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value="Dali", date="2015-08-01T15:52:59.755+0100")
@StaticMetamodel(Transaction.class)
public class Transaction_ {
	public static volatile SingularAttribute<Transaction, TransactionPK> id;
	public static volatile SingularAttribute<Transaction, BigDecimal> amount;
	public static volatile SingularAttribute<Transaction, DateTime> date;
	public static volatile SingularAttribute<Transaction, String> reference;
	public static volatile SingularAttribute<Transaction, Account> account;
}
