CREATE SEQUENCE id START WITH 500000;
CREATE SEQUENCE rid START WITH 500000;
CREATE SEQUENCE wid START WITH 500000;
CREATE SEQUENCE ownership_id START WITH 500000;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION inc_id()
  RETURNS "trigger" AS
  $BODY$
  BEGIN
    New.id := nextval('id');
    Return NEW;
  END;
  $BODY$
LANGUAGE plpgsql VOLATILE;
CREATE OR REPLACE FUNCTION inc_rid()
  RETURNS "trigger" AS
  $BODY$
  BEGIN
    New.rid := nextval('rid');
    Return NEW;
  END;
  $BODY$
LANGUAGE plpgsql VOLATILE;
CREATE OR REPLACE FUNCTION inc_wid()
  RETURNS "trigger" AS
  $BODY$
  BEGIN
    New.wid := nextval('wid');
    Return NEW;
  END;
  $BODY$
LANGUAGE plpgsql VOLATILE;
CREATE OR REPLACE FUNCTION inc_oid()
  RETURNS "trigger" AS
  $BODY$
  BEGIN
    New.ownership_id := nextval('ownership_id');
    Return NEW;
  END;
  $BODY$
LANGUAGE plpgsql VOLATILE;
CREATE TRIGGER cust_trigger BEFORE INSERT
ON Customer FOR EACH ROW
EXECUTE PROCEDURE inc_id();

CREATE TRIGGER mech_trigger BEFORE INSERT
ON Mechanic FOR EACH ROW
EXECUTE PROCEDURE inc_id();

CREATE TRIGGER sr_trigger BEFORE INSERT
ON Service_Request FOR EACH ROW
EXECUTE PROCEDURE inc_rid();

CREATE TRIGGER cr_trigger BEFORE INSERT
ON Closed_Request FOR EACH ROW
EXECUTE PROCEDURE inc_wid();

CREATE TRIGGER owns_trigger BEFORE INSERT
ON Owns FOR EACH ROW
EXECUTE PROCEDURE inc_oid();
