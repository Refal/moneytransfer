create table account
(
    accountNumber VARCHAR(16),
    amount        DECIMAL

);

create index idx_account_number_account ON account (accountNumber);
commit;