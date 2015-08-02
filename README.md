# transfer-server
Proof of concept framework demonstrating Jersey + Jetty + Jackson + JPA. Possibly this should be called J4.

This project builds an executable JAR which can then be run in one of two modes:

- server mode, which provides a small RESTful service simulating a system that holds Clients, Accounts and Transactions and that allows transfers between Accounts
- test mode, which executes a REST client against a running instance of the service to demonstrate successful execution
 
The project assumes you are running a reasonably recent version of Maven (3.2.5 in my case), and that you are building and running against at least a Java 7 JDK.

Please *do not* use this as the basis for a real financial system! It provides no security or authentication, and the transactional safety around transfers is not proven to be thread safe.

## Building

To build the executable JAR after checking out the project:

```
  mvn clean package
```

After a bit of grinding, the JAR should be available at

```
  target/TransferServer-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The JAR has a command line interface, and is run with the standard ``java -jar`` invocation.

```
java -jar target/TransferServer-1.0-SNAPSHOT-jar-with-dependencies.jar -?
usage: TransferServer
 -?,--help         print this message
 -p,--port <arg>   specify the port to run on (defaults to 8080)
 -v,--version      print version
 -x,--test         executes in test mode against a running instance
 ```

## Running
 
To run in server mode, optionally with port 8084 (note that if not specified the port defaults to 8080):
 
```
java -jar target/TransferServer-1.0-SNAPSHOT-jar-with-dependencies.jar -p 8084
```

To run the tests against this instance

```
java -jar target/TransferServer-1.0-SNAPSHOT-jar-with-dependencies.jar -p 8084 -x
```

If all is working ok, you should wind up with a report of a series of tests and the result:

```
checkStatus() trying http://localhost:8084/status
checkStatus() read Status[status=active,requestCount=1]
checkGetAccount() trying http://localhost:8084/transfer/1.0/account/87a4dd04-385a-11e5-a151-feff819cdc9f
checkGetAccount() read Account[accountId=87a4dd04-385a-11e5-a151-feff819cdc9f,balance=300.000,client=146b6c7f-0b8a-43b9-b35d-6489e6daee92,currency=JPY,open=true]
checkGetAccount() trying http://localhost:8084/transfer/1.0/account/EXPECTTOFAIL
checkGetAccount() FAILED - HTTP response = 204
checkGetClient() trying http://localhost:8084/transfer/1.0/client/146b6c7f-0b8a-43b9-b35d-6489e6daee92
checkGetClient() read Client[clientId=146b6c7f-0b8a-43b9-b35d-6489e6daee92,name=MARY]
checkGetClient() trying http://localhost:8084/transfer/1.0/client/EXPECTTOFAIL
checkGetClient() FAILED - HTTP response = 204
checkDetails() trying http://localhost:8084/transfer/1.0/client/146b6c7f-0b8a-43b9-b35d-6489e6daee92
checkDetails() read Client[clientId=146b6c7f-0b8a-43b9-b35d-6489e6daee92,name=MARY]
------------- Client Dump ----------------
Client ID   = 146b6c7f-0b8a-43b9-b35d-6489e6daee92
Client Name = MARY
  Account ID = 87a4d7aa-385a-11e5-a151-feff819cdc9f
  Balance    = 100.000
  Currency   = USD
    Transaction ID = eab5dde4-385f-11e5-a151-feff819cdc9f
    Reference      = special ointment
    Date           = 2015-06-08T08:14:00.000Z
    Amount         = -100.000
    Transaction ID = eab5df2e-385f-11e5-a151-feff819cdc9f
    Reference      = bread
    Date           = 2015-01-23T21:21:00.000Z
    Amount         = -150.000
    Transaction ID = eab5e064-385f-11e5-a151-feff819cdc9f
    Reference      = shoes
    Date           = 2015-01-27T16:18:23.000Z
    Amount         = -50.000
    Transaction ID = eab5e316-385f-11e5-a151-feff819cdc9f
    Reference      = 
    Date           = 2015-08-08T16:23:00.000Z
    Amount         = 300.000
    Transaction ID = eab5e3f2-385f-11e5-a151-feff819cdc9f
    Reference      = 
    Date           = 2015-06-07T02:14:00.000Z
    Amount         = 100.000
  Account ID = 87a4db6a-385a-11e5-a151-feff819cdc9f
  Balance    = 200.000
  Currency   = GBP
  Account ID = 87a4dd04-385a-11e5-a151-feff819cdc9f
  Balance    = 300.000
  Currency   = JPY
-----------------------------------------
checkDetails() trying http://localhost:8084/transfer/1.0/client/046b6c7f-0b8a-43b9-b35d-6489e6daee91
checkDetails() read Client[clientId=046b6c7f-0b8a-43b9-b35d-6489e6daee91,name=FRED]
------------- Client Dump ----------------
Client ID   = 046b6c7f-0b8a-43b9-b35d-6489e6daee91
Client Name = FRED
  Account ID = 46fd58da-385a-11e5-a151-feff819cdc9f
  Balance    = 0.000
  Currency   = USD
  Account ID = 46fd5b64-385a-11e5-a151-feff819cdc9f
  Balance    = 0.000
  Currency   = GBP
  Account ID = 46fd5dee-385a-11e5-a151-feff819cdc9f
  Balance    = 0.000
  Currency   = JPY
  Account ID = 46fd6528-385a-11e5-a151-feff819cdc9f
  Balance    = 0.000
  Currency   = AUD
    Transaction ID = eab5d57e-385f-11e5-a151-feff819cdc9f
    Reference      = 
    Date           = 2015-06-07T02:14:00.000Z
    Amount         = -100.000
    Transaction ID = eab5d858-385f-11e5-a151-feff819cdc9f
    Reference      = eab5dde4-385f-11e5-a151-feff819cdc9f
    Date           = 2015-06-08T08:14:00.000Z
    Amount         = 100.000
    Transaction ID = eab5d970-385f-11e5-a151-feff819cdc9f
    Reference      = 
    Date           = 2015-08-08T16:23:00.000Z
    Amount         = -200.000
    Transaction ID = eab5da42-385f-11e5-a151-feff819cdc9f
    Reference      = eab5df2e-385f-11e5-a151-feff819cdc9f
    Date           = 2015-01-23T21:21:00.000Z
    Amount         = 150.000
    Transaction ID = eab5dd08-385f-11e5-a151-feff819cdc9f
    Reference      = eab5e064-385f-11e5-a151-feff819cdc9f
    Date           = 2015-01-27T16:18:23.000Z
    Amount         = 50.000
-----------------------------------------
checkGetAccount() trying http://localhost:8084/transfer/1.0/account/87a4dd04-385a-11e5-a151-feff819cdc9f
checkGetAccount() read Account[accountId=87a4dd04-385a-11e5-a151-feff819cdc9f,balance=300.000,client=146b6c7f-0b8a-43b9-b35d-6489e6daee92,currency=JPY,open=true]
checkGetAccount() trying http://localhost:8084/transfer/1.0/account/46fd58da-385a-11e5-a151-feff819cdc9f
checkGetAccount() read Account[accountId=46fd58da-385a-11e5-a151-feff819cdc9f,balance=0.000,client=046b6c7f-0b8a-43b9-b35d-6489e6daee91,currency=USD,open=true]
-- Starting from balance = 300.000
-- Starting to   balance = 0.000
checkTransfer() trying http://localhost:8084/transfer/1.0/transfer
checkTransfer() read TransferResult[resultCode=200,resultMessage=OK,transactionId=77e2bb97-9693-4378-bbe8-dadde7f32cb1]
TransferResult[resultCode=200,resultMessage=OK,transactionId=77e2bb97-9693-4378-bbe8-dadde7f32cb1]
checkGetAccount() trying http://localhost:8084/transfer/1.0/account/87a4dd04-385a-11e5-a151-feff819cdc9f
checkGetAccount() read Account[accountId=87a4dd04-385a-11e5-a151-feff819cdc9f,balance=200.000,client=146b6c7f-0b8a-43b9-b35d-6489e6daee92,currency=JPY,open=true]
checkGetAccount() trying http://localhost:8084/transfer/1.0/account/46fd58da-385a-11e5-a151-feff819cdc9f
checkGetAccount() read Account[accountId=46fd58da-385a-11e5-a151-feff819cdc9f,balance=100.000,client=046b6c7f-0b8a-43b9-b35d-6489e6daee91,currency=USD,open=true]
-- Final from balance = 200.000
-- Final to   balance = 100.000
```
## API

| URL | Purpose | Method |
| --- | ------- | ------ |
| /status | returns a Status object | GET |
| /transfer/1.0/account/{account id} | attempts to retrieve an Account matching the specified ID. Will return 404 if the account is not found | GET |
| /transfer/1.0/client/{client id} | attempts to retrieve a Client matching the specified ID. Will return 404 if the client is not found | GET |
| /transfer/1.0/transfer | sends a TransferRequest and gets a TransferResponse back. Usually returns a 200 with transaction details in the response, but can return 5xx if something goes horribly wrong | POST |

The *TransferRequest* looks like this:

```
{
  "fromAccount" : "87a4dd04-385a-11e5-a151-feff819cdc9f",
  "toAccount" : "46fd58da-385a-11e5-a151-feff819cdc9f",
  "amount" : 10000
}
```

Note that the *amount* does not specify the number of decimal places in the currency, and in this proof-of-concept it is hardwired to 2, so in the case above 10000 represents 100.00
