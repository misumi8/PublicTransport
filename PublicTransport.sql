set serveroutput on;

drop trigger users_before_insert;

drop sequence users_seq;
drop sequence depots_seq;
drop sequence vehicles_seq;
drop sequence routes_seq;
drop sequence stations_seq;

drop table stations;
drop table users;
drop table schedule;
drop table vehicles;
drop table depots;
drop table routes;

create or replace type int_tab is table of INT;

/

CREATE SEQUENCE users_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE SEQUENCE depots_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE SEQUENCE vehicles_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE SEQUENCE routes_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE SEQUENCE stations_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

create table users(
    id INT NOT NULL primary key,
    username VARCHAR2(50) NOT NULL,
    password RAW(32) NOT NULL,
    city VARCHAR2(50)
);

create table depots(
    id INT NOT NULL primary key,
    placement VARCHAR2(100)
);

/

create table routes(
    id INT NOT NULL primary key,
    ticket_price NUMBER(6,2),
    customers INT,
    expected_time INT --minutes
);

/

create table vehicles(
    id INT NOT NULL primary key,
    plate VARCHAR2(6) unique,
    date_of_manufacture DATE,
    depot_id INT,
    route_id INT,
    foreign key (route_id) references routes(id),    
    foreign key (depot_id) references depots(id)
);

/

create table stations(
    id INT NOT NULL primary key,
    placement VARCHAR2(100),
    route_id INT,
    order_number NUMBER(4),
    foreign key (route_id) references routes(id)
);

/

create table schedule(
    depot_id INT,
    vehicle_id INT,
    departure_time TIMESTAMP,
    foreign key (depot_id) references depots(id),
    foreign key (vehicle_id) references vehicles(id)
);

/

-- TRIGGERS:
create or replace trigger users_before_insert
before insert on users
for each row
declare
    v_same_username_count INT;
    user_existent exception;
    PRAGMA EXCEPTION_INIT (user_existent, -20001);  
begin
    :NEW.id := users_seq.NEXTVAL;
    select count(*) into v_same_username_count from users u where u.username = :NEW.username;
    if(v_same_username_count > 0) then
        raise user_existent;
    end if;
exception
    when user_existent then
        RAISE_APPLICATION_ERROR(-20001, SQLERRM || 'An user with username ' || :NEW.username || ' already exists');
end;

/

create or replace trigger depots_before_insert
before insert on depots
for each row
declare
    v_same_place_count INT;
    v_same_placement INT;
    same_placement exception;
    PRAGMA EXCEPTION_INIT (same_placement, -20007);

begin
    :NEW.id := depots_seq.NEXTVAL;
    select count(*), max(id) into v_same_place_count, v_same_placement from depots d where d.placement = :NEW.placement;
    if(v_same_place_count > 0) then
        raise same_placement;
    end if;
exception
    when same_placement then
        RAISE_APPLICATION_ERROR(-20007, SQLERRM || 'This placement ' || :NEW.placement || ' is already occupied by depot ' || v_same_placement);
end;

/

create or replace trigger vehicles_before_insert
before insert on vehicles
for each row
declare
    v_same_plate_count INT;
    v_id_vehicle INT;
    existing_plate exception;
    PRAGMA EXCEPTION_INIT (existing_plate, -20006);
begin
    :NEW.id := vehicles_seq.NEXTVAL;
    select count(*), max(v.id) into v_same_plate_count, v_id_vehicle from vehicles v where v.plate = :NEW.plate;
    if(v_same_plate_count > 0) then 
        raise existing_plate;
    end if;
exception
    when existing_plate then
        RAISE_APPLICATION_ERROR(-20006, SQLERRM || 'Plate ' || :NEW.plate || ' already exists (vehicle ' || v_id_vehicle || ')');
end;

/

create or replace trigger routes_before_insert
before insert on routes
for each row
begin
    :NEW.id := routes_seq.NEXTVAL;
end;

/

create or replace trigger stations_before_insert
before insert on stations
for each row
declare
    v_same_ord_no INT;
    v_available_ord INT;
    v_next_avlb_ord INT;
    v_station_name stations.placement%type;
    invalid_ord_number exception;
    PRAGMA EXCEPTION_INIT (invalid_ord_number, -20004);
    existing_station exception;
    PRAGMA EXCEPTION_INIT (existing_station, -20005);
begin
    :NEW.id := stations_seq.NEXTVAL;
    if (:NEW.route_id is not NULL) then
        select count(*) into v_station_name from stations s where s.route_id = :NEW.route_id and :NEW.placement = s.placement;
        if(v_station_name > 0) then
            raise existing_station;
        end if;
        select count(*) into v_same_ord_no from stations s where s.route_id = :NEW.route_id and s.order_number = :NEW.order_number;
        if (v_same_ord_no > 0) then
            select min(s.order_number) into v_available_ord from stations s where s.route_id = :NEW.route_id;
            loop
                select min(s.order_number) into v_next_avlb_ord from stations s where s.route_id = :NEW.route_id and s.order_number > v_available_ord;
                if(v_available_ord != v_next_avlb_ord - 1) then
                    v_available_ord := v_available_ord + 1;
                    exit;
                elsif (v_next_avlb_ord is NULL) then
                    v_available_ord := v_available_ord + 1;
                    exit;
                else 
                    v_available_ord := v_available_ord + 1;
                end if;
            end loop;
            raise invalid_ord_number;
        end if;
    end if;
exception
    when invalid_ord_number then
        RAISE_APPLICATION_ERROR(-20004, SQLERRM || 'Invalid station order number. First available station order is ' || v_available_ord);
    when existing_station then
        RAISE_APPLICATION_ERROR(-20005, SQLERRM || 'This route already has a station at ' || :NEW.placement);
end;

/

-- FUNCTIONS:

-- might be necessary GRANT EXECUTE ON DBMS_CRYPTO TO student; !sysdba
create or replace function hashPassword(
    p_password VARCHAR2
) return RAW as
    v_hashed_password RAW(32);
begin
    v_hashed_password := DBMS_CRYPTO.HASH(UTL_I18N.STRING_TO_RAW(p_password, 'AL32UTF8'), 3); -- 3 must be equiv to SHA-256
    return v_hashed_password;
end hashPassword;

/

create or replace function login(
    p_username users.username%type,
    p_password VARCHAR2
) return boolean as
    v_user_exist INT;
    v_hashed_password RAW(32);
    v_hashed_p_password RAW(32);
    user_inexistent exception;
    PRAGMA EXCEPTION_INIT (user_inexistent, -20001);  
    incorrect_password exception;
    PRAGMA EXCEPTION_INIT (incorrect_password, -20003); 
begin
    select count(*) into v_user_exist from users u where u.username = p_username;
    if (v_user_exist > 0) then
        select u.password into v_hashed_password from users u where u.username = p_username;
        v_hashed_p_password := hashPassword(p_password);
        if(v_hashed_p_password = v_hashed_password) then
            return true;
        else
            raise incorrect_password; 
        end if;
    else raise user_inexistent;
    end if;
exception
    when user_inexistent then
        DBMS_OUTPUT.PUT_LINE(SQLERRM || 'Non-existent username');
        return false;
    when incorrect_password then
        DBMS_OUTPUT.PUT_LINE(SQLERRM || 'Incorrect password');
        return false;
end login;

/

create or replace function registerUser(
    p_username users.username%type,
    p_password VARCHAR2,
    p_city users.city%type
) return boolean as
    v_user_exist INT;
    v_hashed_password RAW(32);
    too_short_username exception;
    PRAGMA EXCEPTION_INIT (too_short_username, -20009);
    no_number exception;
    PRAGMA EXCEPTION_INIT (no_number, -20010);
    no_upper_letter exception;
    PRAGMA EXCEPTION_INIT (no_upper_letter, -20011);
begin
    if(LENGTH(p_username) < 8) then
        raise too_short_username;
    elsif (not REGEXP_LIKE(p_password, '[a-zA-Z0-9]*[A-Z][a-zA-Z0-9]*')) then
        raise no_upper_letter;
    elsif (not REGEXP_LIKE(p_password, '[a-zA-Z0-9]*[0-9][a-zA-Z0-9]*')) then
        raise no_number;
    end if;
    select count(*) into v_user_exist from users u where u.username = p_username;
    if(v_user_exist = 0) then
        v_hashed_password := hashPassword(p_password);
        insert into users(username, password, city) values (p_username, v_hashed_password, p_city);
        return true;
    end if;
exception 
    when too_short_username then
        DBMS_OUTPUT.PUT_LINE(SQLERRM || 'Your username must be at least 8 characters');
        return false;
    when no_number then
        DBMS_OUTPUT.PUT_LINE(SQLERRM || 'Your password must contain at least one number');
        return false;
    when no_upper_letter then
        DBMS_OUTPUT.PUT_LINE(SQLERRM || 'Your password must contain at least one upper case letter');
        return false;
end registerUser;

/

create or replace function getAvgCustomers
return FLOAT as
    v_total_customers INT;
    v_no_routes INT;
begin
    v_total_customers := 0;
    v_no_routes := 0;
    for v_row in (select * from routes) loop
        v_total_customers := v_total_customers + v_row.customers;
        v_no_routes := v_no_routes + 1;
    end loop;
    if(v_total_customers = 0 or v_no_routes = 0) then
        return 0;
    end if;
    return v_total_customers/v_no_routes;
end getAvgCustomers;

/

-- return the id s of vehicles that are highly solicited and need more entities
create or replace function getRoutesNeedMoreVehicles(
    p_upper_limit INT
) return int_tab as
    route_ids int_tab := int_tab();
    v_avg_customers FLOAT;
begin
    if(p_upper_limit is not NULL) then
        v_avg_customers := p_upper_limit;
    else
        v_avg_customers := getAvgCustomers;
    end if;
    for v_row in (select * from routes) loop
        if(v_row.customers > v_avg_customers) then
            route_ids.EXTEND;
            route_ids(route_ids.LAST) := v_row.id;
        end if;
    end loop;
    return route_ids;
end getRoutesNeedMoreVehicles;

/

create or replace function getVehiclesToReplace(
    p_min_years INT
) return int_tab as
    vehicle_ids int_tab := int_tab();
begin
    for v_row in (select * from vehicles v where v.date_of_manufacture < add_months(TRUNC(SYSDATE), -(p_min_years * 12))) loop
        vehicle_ids.EXTEND;
        vehicle_ids(vehicle_ids.LAST) := v_row.id;
    end loop;
    return vehicle_ids;
end getVehiclesToReplace;

/

create or replace function getArrivalTime(
    p_vehicle_id INT
) return TIMESTAMP as
    v_vehicle_count INT;
    v_dep_time TIMESTAMP;
    v_arr_time TIMESTAMP;
    v_exp_time INT;
    v_route_id INT;
    inexistent_vehicle exception;
    v_hours INT;
    v_minutes INT;
    PRAGMA EXCEPTION_INIT (inexistent_vehicle, -20008);
begin
    select count(*) into v_vehicle_count from vehicles v where v.id = p_vehicle_id;
    if(v_vehicle_count > 0) then
        select min(s.departure_time) into v_dep_time from schedule s where s.vehicle_id = p_vehicle_id;
        select min(v.route_id) into v_route_id from vehicles v where v.id = p_vehicle_id;
        select min(r.expected_time) into v_exp_time from routes r where r.id = v_route_id;
        v_hours := TRUNC(v_exp_time / 60);
        while (v_hours >= 24) loop
            v_hours := v_hours - 24;
        end loop;
        v_minutes := v_exp_time - (v_hours * 60);
        v_arr_time := v_dep_time + TO_DSINTERVAL('0 '|| TO_CHAR(v_hours, 'FM00') || ':' || TO_CHAR(v_minutes, 'FM00') || ':00'); --INTERVAL DAY TO SECOND --FMOO removes useless symbols?
        return v_arr_time;
    else 
        raise inexistent_vehicle;
    end if;
exception
    when inexistent_vehicle then
        DBMS_OUTPUT.PUT_LINE(SQLERRM || 'Vehicle with id ' || p_vehicle_id || ' does not exist');
end getArrivalTime;

/

declare
    answer boolean;
    --hashed_password RAW(32);
    --avg_customers FLOAT;
    --route_ids int_tab := int_tab();
    vehicle_ids int_tab := int_tab();
    min_years INT := 5;
    arr_time TIMESTAMP;
    p_password VARCHAR2(50) := 'ab9A';
begin
    if (REGEXP_LIKE(p_password, '[a-zA-Z0-9]*[A-Z][a-zA-Z0-9]*')) then
        DBMS_OUTPUT.PUT_LINE('One Upper Case Letter');
    end if;
    if (REGEXP_LIKE(p_password, '[a-zA-Z0-9]*[0-9][a-zA-Z0-9]*')) then
        DBMS_OUTPUT.PUT_LINE('One Number');
    end if;

    /*insert into users(username, password, city) values ('misumi7', hashPassword('someText123'), 'Iasi');
    insert into users(username, password, city) values ('misumi8', hashPassword('someText124'), 'Iasi');
    insert into users(username, password, city) values ('misumi7', hashPassword('someText125'), 'Iasi');*/

    /*insert into depots(placement) values ('str. Igor Vieru 15');
    insert into depots(placement) values ('str. Igor Vieru 16');
    insert into depots(placement) values ('str. Igor Vieru 18');
    insert into depots(placement) values ('str. Igor Vieru 19');*/

    /*insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI01', TO_DATE('21-01-2005', 'DD-MM-YYYY'), NULL, NULL);
    insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI02', TO_DATE('21-01-2006', 'DD-MM-YYYY'), NULL, NULL);
    insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI03', TO_DATE('21-01-2007', 'DD-MM-YYYY'), NULL, NULL);
    insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI04', TO_DATE('21-01-2011', 'DD-MM-YYYY'), NULL, NULL);
    insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI05', TO_DATE('21-01-2015', 'DD-MM-YYYY'), NULL, NULL);
    insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI06', TO_DATE('21-01-2020', 'DD-MM-YYYY'), NULL, NULL);
    insert into vehicles(plate, date_of_manufacture, depot_id, route_id) values ('IASI07', TO_DATE('21-01-2024', 'DD-MM-YYYY'), NULL, NULL);*/
    
    /*insert into routes(ticket_price, customers) values (3.5, 20);
    insert into routes(ticket_price, customers) values (3.5, 25);
    insert into routes(ticket_price, customers) values (3.5, 40);
    insert into routes(ticket_price, customers) values (3.5, 88);
    insert into routes(ticket_price, customers) values (3.5, 45);
    insert into routes(ticket_price, customers) values (3.5, 98);
    insert into routes(ticket_price, customers) values (3.5, 77);
    insert into routes(ticket_price, customers) values (3.5, 66);
    insert into routes(ticket_price, customers) values (3.5, 74);
    insert into routes(ticket_price, customers) values (3.5, 83);
    insert into routes(ticket_price, customers, expected_time) values (3.5, 11, 50);*/

    /*insert into schedule(depot_id, vehicle_id, departure_time) values (1, 1, TO_TIMESTAMP('2024-05-28 10:00:00', 'YYYY-MM-DD HH24:MI:SS'));*/
    /*arr_time := getArrivalTime(1);
    DBMS_OUTPUT.PUT_LINE(arr_time);*/

    --answer := registerUser('aUserName', 'a8ser9assword', 'aCityName');    
    --answer := login('aUserName', 'aUserPassword');
    
    /*hashed_password := hashPassword('aPasasword');
    DBMS_OUTPUT.PUT_LINE(hashed_password);*/
    
    /*insert into routes(ticket_price, avg_customers) values (3.5, 500);
    insert into stations(placement, route_id, order_number) values ('str. Vasile Alecsandri 3/7', 1, 8);*/
    --insert into routes(ticket_price, customers) values (3.5, 20);
    
    /*avg_customers := getAvgCustomers;
    DBMS_OUTPUT.PUT_LINE('AVG COSTUMRS: ' || avg_customers);*/

    /*route_ids := getRoutesNeedMoreVehicles(NULL);
    --route_ids := getRoutesNeedMoreVehicles(25);
    for i in route_ids.first..route_ids.last loop
       DBMS_OUTPUT.PUT_LINE(i || ') ' || route_ids(i));    
    end loop;*/
    
    /*vehicle_ids := getVehiclesToReplace(min_years);
    for i in vehicle_ids.first..vehicle_ids.last loop
       DBMS_OUTPUT.PUT_LINE(i || ') ' || vehicle_ids(i));    
    end loop;*/
end;
