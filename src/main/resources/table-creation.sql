create table ers_user_roles(
	id serial,
	role_name varchar(25) not null,
	
	constraint ers_user_roles_pk
	primary key (id)
);
INSERT INTO ers_user_roles
(role_name)
VALUES('ADMIN'),('FINANCE_MANAGER'),('EMPLOYEE');
INSERT INTO ers_user_roles
(role_name)
VALUES('DELETED');

-- +++--------------------------------------------+++ --
create table ers_reimbursement_types(
	id serial,
	role_name varchar(10) not null,

	constraint ers_reimbursement_types_pk
	primary key (id)
);
INSERT INTO ers_reimbursement_types
(role_name)
VALUES('LODGING'),('TRAVEL'),('FOOD'),('OTHER');

-- +++--------------------------------------------+++ --
create table ers_reimbursement_statuses(
	id serial,
	role_name varchar(10) not null,

	constraint ers_reimbursement_statuses_pk
	primary key (id)
);
INSERT INTO ers_reimbursement_statuses
(role_name)
VALUES('PENDING'),('APPROVED'),('DENIED'),('CLOSED');

-- +++--------------------------------------------+++ --
create table ers_users(
	id serial,
	username varchar(25) unique not null,
	password varchar(256) not null,
	first_name varchar(25) not null,
	last_name varchar(25) not null,
	email varchar(256) unique not null,
	user_role_id int not null,
	is_active boolean default true,


	constraint ers_user_id
	primary key (id),

	constraint ers_user_roles_fk
	foreign key (user_role_id)
	references ers_user_roles
);


-- +++--------------------------------------------+++ --
-- project_1.ers_reimbursements definition

-- Drop table

-- DROP TABLE project_1.ers_reimbursements;

create TABLE ers_reimbursements (
	id serial NOT NULL,
	amount numeric(6,2) NOT NULL,
	submitted timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	resolved timestamp NULL,
	description varchar(1000) NULL,
	reciept bytea,
	author_id int4 NOT NULL,
	resolver_id int4 NULL,
	reimbursement_status_id int4 NOT NULL,
	reimbursement_type_id int4 NOT NULL,
	CONSTRAINT ers_reimbursements_pk PRIMARY KEY (id)
);


-- project_1.ers_reimbursements foreign keys

ALTER TABLE ers_reimbursements
ADD CONSTRAINT author_id_fk
FOREIGN KEY (author_id)
REFERENCES ers_users(id);

ALTER TABLE ers_reimbursements
ADD CONSTRAINT reimbursement_status_id_fk
FOREIGN KEY (reimbursement_status_id)
REFERENCES ers_reimbursement_statuses(id);

ALTER TABLE ers_reimbursements
ADD CONSTRAINT reimbursement_type_id_fk
FOREIGN KEY (reimbursement_type_id)
REFERENCES ers_reimbursement_types(id);

ALTER TABLE ers_reimbursements
ADD CONSTRAINT resolver_id_fk
FOREIGN KEY (resolver_id)
REFERENCES ers_users(id);




INSERT INTO ers_users
(username, password, first_name, last_name, email, user_role_id)
VALUES('u6', crypt('password', gen_salt('bf', 10)), 'troy', 'davis', 'u6', 1);

select * from ers_users eu
where password = crypt('password', password);

--https://www.meetspaceapp.com/2016/04/12/passwords-postgresql-pgcrypto.html

truncate ers_users ;

select * from ers_users eu ;

select * from ers_reimbursements er ;
--needed to be able to hash and unhash the passwords
CREATE EXTENSION pgcrypto;

SELECT er.id, er.amount, er.description, er.reimbursement_status_id,
er.reimbursement_type_id, er.resolved, er.submitted,  er.author_id , er.resolver_id,
author.first_name as author_first_name , author.last_name as author_last_name ,
resolver.first_name as resolver_first_name, resolver.last_name as resolver_last_name
FROM ers_reimbursements er
left join ers_users author
on er.author_id = author.id
left join ers_users resolver
on er.resolver_id = resolver.id;

SELECT * FROM ers_reimbursements er
join ers_users author
on er.author_id = author.id
where author.username = 'u4';
