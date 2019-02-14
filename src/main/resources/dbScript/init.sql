-- create database if not exists rock DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

create table if not exists user_info
(
  id                              varchar(32)  not null primary key,
  username                        varchar(255) not null unique,
  password                        varchar(255) not null,
  full_name                       varchar(255) not null,
  email                           varchar(255) null,
  phone                           varchar(255) null,
  enable                          bit               default true,
  locked                          bit               default false,
  need_change_password_when_login bit               default false,
  last_time_change_password       timestamp    null,
  expired_time                    timestamp    null,
  created                         timestamp    null default current_timestamp
);

create table if not exists group_info
(
  id       varchar(32) primary key,
  code     varchar(255) not null unique,
  name     varchar(255) not null unique,
  editable bit default true
);

create table if not exists authority
(
  group_id  varchar(32)  not null,
  authority varchar(255) not null,
  constraint fk_authorities_users foreign key (group_id) references group_info (id) on delete cascade,
  unique index uix_auth_group_id (group_id, authority)
);

create table if not exists group_member
(
  group_id varchar(32),
  user_id  varchar(32),
  constraint pk_group_member primary key (group_id, user_id),
  constraint fk_group_member_group_id foreign key (group_id) references group_info (id) on delete cascade,
  constraint fk_group_member_user_id foreign key (user_id) references user_info (id) on delete cascade
);

create table audit_log
(
  id           varchar(32) primary key not null,
  username     varchar(255)            null,
  full_name    varchar(255)            null,
  ip           varchar(255)            null,
  url          varchar(255)            null,
  method       varchar(50)             null,
  request_type varchar(255)            null,
  params       longtext                null,
  success      bit default true,
  details      longtext                null,
  module_name  varchar(255)            null,
  method_name  varchar(255)            null,
  operation    varchar(100),
  created      timestamp,
  index ix_audit_log_created (created)
);

create table dictionary_code
(
  id        varchar(32)  not null primary key,
  code      varchar(255) not null unique,
  name      varchar(255) not null,
  editable  bit default true,
  parent_id varchar(32)  null,
  `index`   int          null,
  remark    varchar(255) null,
  f1        varchar(255) null,
  f2        varchar(255) null,
  f3        varchar(255) null,
  f4        varchar(255) null,
  f5        varchar(255) null,
  unique index ux_dictionary_code_parent_code_code (code, parent_id)
);

create table if not exists customer
(
  id      varchar(32)  not null primary key,
  name    varchar(255) not null,
  address varchar(255),
  tel     varchar(100),
  mobile  varchar(100)
);

create table if not exists attachment
(
  id            varchar(32)  not null primary key,
  name          varchar(255) not null,
  type          varchar(20),
  content_type  varchar(255),
  recorder_type varchar(255) not null,
  recorder_id   varchar(32)  not null,
  content       longblob     not null
);


create table if not exists equipment_model
(
  id       varchar(32)  not null primary key,
  brand_id varchar(32)  not null,
  name     varchar(255) not null,
  constraint foreign key fk_equipment_model_brand_id (brand_id) references dictionary_code (id) on delete restrict
);

create table if not exists equipment
(
  id               varchar(32)  not null primary key,
  name             varchar(255) not null,
  type_id          varchar(32)  not null,
  model_id         varchar(32)  not null,
  owner            varchar(32)  null,
  serial_number    varchar(255),
  manufacture_date timestamp    null,
  constraint foreign key fk_equipment_model_id (model_id) references equipment_model (id) on delete restrict,
  constraint foreign key fk_equipment_type_id (type_id) references dictionary_code (id) on delete restrict,
  constraint foreign key fk_equipment_owner (owner) references customer (id) on delete restrict
);



