create function default_balance() returns trigger
    language plpgsql
as
'
    begin
        NEW.actualBalance = 1000;
        return NEW;
    end
';

CREATE TRIGGER balance
    BEFORE INSERT
    ON card
    FOR EACH ROW
EXECUTE PROCEDURE default_balance();

create function set_process_date() returns trigger
    language plpgsql
as
'
    begin
        NEW.processDate = now();
        return NEW;
    end
';

CREATE TRIGGER process_date
    BEFORE INSERT
    ON transactionhistory
    FOR EACH ROW
EXECUTE PROCEDURE set_process_date();