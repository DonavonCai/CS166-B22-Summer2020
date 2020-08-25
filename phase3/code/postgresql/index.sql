CREATE INDEX cust_idx
ON Customer
USING BTREE
(id);

CREATE INDEX mech_idx
ON Mechanic
USING BTREE
(id);

CREATE INDEX car_idx
ON Car
USING BTREE
(vin);

CREATE INDEX own_idx
ON Owns
USING BTREE
(ownership_id);

CREATE INDEX sr_idx
ON Service_Request
USING BTREE
(rid);

CREATE INDEX cr_idx
ON Closed_Request
USING BTREE
(wid);
