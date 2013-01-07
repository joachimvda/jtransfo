create table address (id bigint not null, address varchar(255), country varchar(2), location varchar(255), postalCode varchar(255), primary key (id));
create table person (id bigint not null, name varchar(255), comment varchar(255), address_id bigint not null, primary key (id));
alter table person add constraint person_address foreign key (address_id) references address;
create sequence address_seq;
create sequence person_seq;

insert into address (id, address, postalCode, location, country) values
        (2000, 'Churchstreet 11', '1234', 'Mytown', 'BE');
insert into person (id, name, address_id, comment) values
        (1000, 'John Doe', 2000, 'db-only info');
