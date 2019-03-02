-- begin TP_PERSON
alter table TP_PERSON add constraint FK_TP_PERSON_ON_USER foreign key (USER_ID) references SEC_USER(ID)^
alter table TP_PERSON add constraint FK_TP_PERSON_ON_TEAM foreign key (TEAM_ID) references TP_TEAM(ID)^
create unique index IDX_TP_PERSON_UK_USER_ID on TP_PERSON (USER_ID) where DELETE_TS is null ^
create index IDX_TP_PERSON_ON_USER on TP_PERSON (USER_ID)^
create index IDX_TP_PERSON_ON_TEAM on TP_PERSON (TEAM_ID)^
-- end TP_PERSON
-- begin TP_WEEK_REPORT
alter table TP_WEEK_REPORT add constraint FK_TP_WEEK_REPORT_ON_PERSON foreign key (PERSON_ID) references TP_PERSON(ID)^
alter table TP_WEEK_REPORT add constraint FK_TP_WEEK_REPORT_ON_WEEK foreign key (WEEK_ID) references TP_WEEK(ID)^
create unique index TP_WEEK_REPORT_UNIQUE_WEEK_PERSON on TP_WEEK_REPORT (WEEK_ID, PERSON_ID) where DELETE_TS is null ^
create index IDX_TP_WEEK_REPORT_ON_PERSON on TP_WEEK_REPORT (PERSON_ID)^
create index IDX_TP_WEEK_REPORT_ON_WEEK on TP_WEEK_REPORT (WEEK_ID)^
-- end TP_WEEK_REPORT
-- begin TP_TEAM
alter table TP_TEAM add constraint FK_TP_TEAM_ON_PROJECT foreign key (PROJECT_ID) references TP_PROJECT(ID)^
create index IDX_TP_TEAM_ON_PROJECT on TP_TEAM (PROJECT_ID)^
-- end TP_TEAM
