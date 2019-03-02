-- begin TP_PERSON
create table TP_PERSON (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID uuid not null,
    TEAM_ID uuid not null,
    --
    primary key (ID)
)^
-- end TP_PERSON
-- begin TP_WEEK_REPORT
create table TP_WEEK_REPORT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_ID uuid not null,
    WEEK_ID uuid not null,
    PLANNED varchar(1000),
    ACTUAL varchar(1000),
    PLAN_EDITABLE boolean,
    ACT_EDITABLE boolean,
    --
    primary key (ID)
)^
-- end TP_WEEK_REPORT
-- begin TP_PROJECT
create table TP_PROJECT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end TP_PROJECT
-- begin TP_TEAM
create table TP_TEAM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROJECT_ID uuid not null,
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end TP_TEAM
-- begin TP_WEEK
create table TP_WEEK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    START_DATE date not null,
    END_DATE date not null,
    WEEK_NUMBER integer,
    YEAR_ integer not null,
    --
    primary key (ID)
)^
-- end TP_WEEK
