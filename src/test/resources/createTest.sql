CREATE TABLE client (
  client_id VARCHAR(36) NOT NULL,
  name VARCHAR(64) NOT NULL,
  PRIMARY KEY (client_id)
);

INSERT INTO client VALUES (
 ('046b6c7f-0b8a-43b9-b35d-6489e6daee91', 'FRED'),
 ('146b6c7f-0b8a-43b9-b35d-6489e6daee92', 'MARY'),
 ('146b6c7f-0b8a-43b9-e35e-6489e6daee92', 'BOB'),
 ('41f4ca3c-389e-11e5-a151-feff819cdc9f', 'JANE'),
 ('41f4ce2e-389e-11e5-a151-feff819cdc9f', 'ALEX')
);

CREATE TABLE IF NOT EXISTS account (
  account_id VARCHAR(36) NOT NULL,
  client_id VARCHAR(36) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  balance DECIMAL(10,3) NOT NULL,
  open TINYINT NOT NULL,
  PRIMARY KEY (account_id),
  FOREIGN KEY (client_id) REFERENCES client (client_id)
);

INSERT INTO account VALUES (
  ('46fd58da-385a-11e5-a151-feff819cdc9f', '046b6c7f-0b8a-43b9-b35d-6489e6daee91', 'USD', 0.0, 1),
  ('46fd5b64-385a-11e5-a151-feff819cdc9f', '046b6c7f-0b8a-43b9-b35d-6489e6daee91', 'GBP', 0.0, 1),
  ('46fd5dee-385a-11e5-a151-feff819cdc9f', '046b6c7f-0b8a-43b9-b35d-6489e6daee91', 'JPY', 0.0, 1),
  ('46fd6528-385a-11e5-a151-feff819cdc9f', '046b6c7f-0b8a-43b9-b35d-6489e6daee91', 'AUD', 0.0, 0),

  ('87a4d7aa-385a-11e5-a151-feff819cdc9f', '146b6c7f-0b8a-43b9-b35d-6489e6daee92', 'USD', 100.0, 0),
  ('87a4db6a-385a-11e5-a151-feff819cdc9f', '146b6c7f-0b8a-43b9-b35d-6489e6daee92', 'GBP', 200.0, 0),
  ('87a4dd04-385a-11e5-a151-feff819cdc9f', '146b6c7f-0b8a-43b9-b35d-6489e6daee92', 'JPY', 300.0, 1),
  
  ('87a4dd04-11e5-a151-385a-feff819cdc9f', '146b6c7f-0b8a-43b9-e35e-6489e6daee92', 'JPY', 300.0, 1),
  
  ('adfd52b2-389e-11e5-a151-feff819cdc9f', '41f4ca3c-389e-11e5-a151-feff819cdc9f', 'USD', 10000.00, 1),
  ('adfd560e-389e-11e5-a151-feff819cdc9f', '41f4ce2e-389e-11e5-a151-feff819cdc9f', 'USD', 10000.00, 1)

);

CREATE TABLE IF NOT EXISTS transaction (
  tx_id VARCHAR(36) NOT NULL,
  account_id VARCHAR(36) NOT NULL,
  amount DECIMAL(10,3) NOT NULL,
  date TIMESTAMP NOT NULL,
  reference VARCHAR(36) NULL,
  PRIMARY KEY (tx_id, account_id),
  FOREIGN KEY (account_id) REFERENCES account (account_id)
);

INSERT INTO transaction VALUES (
  ('87a4dd04-11e5-a151-385a-feff819cdc9f', '87a4dd04-11e5-a151-385a-feff819cdc9f', 123.45, '2015-08-08 21:13:17', ''),

  ('eab5d57e-385f-11e5-a151-feff819cdc9f','46fd6528-385a-11e5-a151-feff819cdc9f', -100.0, '2015-06-07 03:14:00', ''),
  ('eab5d858-385f-11e5-a151-feff819cdc9f','46fd6528-385a-11e5-a151-feff819cdc9f', 100.0, '2015-06-08 9:14:00', 'eab5dde4-385f-11e5-a151-feff819cdc9f'),
  ('eab5d970-385f-11e5-a151-feff819cdc9f','46fd6528-385a-11e5-a151-feff819cdc9f', -200.0, '2015-08-08 17:23:00', ''),
  ('eab5da42-385f-11e5-a151-feff819cdc9f','46fd6528-385a-11e5-a151-feff819cdc9f', 150.0, '2015-01-23 21:21:00', 'eab5df2e-385f-11e5-a151-feff819cdc9f'),
  ('eab5dd08-385f-11e5-a151-feff819cdc9f','46fd6528-385a-11e5-a151-feff819cdc9f', 50.0, '2015-01-27 16:18:23', 'eab5e064-385f-11e5-a151-feff819cdc9f'),
  
  ('eab5dde4-385f-11e5-a151-feff819cdc9f', '87a4d7aa-385a-11e5-a151-feff819cdc9f', -100.0, '2015-06-08 9:14:00', 'special ointment'),
  ('eab5df2e-385f-11e5-a151-feff819cdc9f', '87a4d7aa-385a-11e5-a151-feff819cdc9f', -150.0, '2015-01-23 21:21:00', 'bread'),
  ('eab5e064-385f-11e5-a151-feff819cdc9f', '87a4d7aa-385a-11e5-a151-feff819cdc9f', -50.0, '2015-01-27 16:18:23', 'shoes'),
  ('eab5e316-385f-11e5-a151-feff819cdc9f', '87a4d7aa-385a-11e5-a151-feff819cdc9f', 300.0, '2015-08-08 17:23:00', ''),
  ('eab5e3f2-385f-11e5-a151-feff819cdc9f', '87a4d7aa-385a-11e5-a151-feff819cdc9f', 100.0, '2015-06-07 03:14:00', '')
);