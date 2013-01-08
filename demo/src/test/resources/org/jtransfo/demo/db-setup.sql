create table address (id bigint not null, address varchar(255), country varchar(2), location varchar(255), postalCode varchar(255), primary key (id));
create table person (id bigint not null, name varchar(255), comment varchar(255), address_id bigint, primary key (id));
create table voiceContact (id bigint not null, type varchar(255), voice varchar(255), person_id bigint not null, primary key (id));
alter table person add constraint person_address foreign key (address_id) references address;
alter table voiceContact add constraint person_voiceContact foreign key (person_id) references person;
create sequence address_seq start with 10000;
create sequence person_seq start with 10000;
create sequence voiceContact_seq start with 10000;

insert into address (id, address, postalCode, location, country) values
        (2000, 'Churchstreet 11', '1234', 'Mytown', 'BE');
insert into person (id, name, address_id, comment) values
        (1000, 'John Doe', 2000, 'db-only info');
insert into voiceContact (id, person_id, type, voice) values
        (3000, 1000, 'work', '+123 456 789');
insert into voiceContact (id, person_id, type, voice) values
        (3001, 1000, 'private, skype', 'myskypeid');
