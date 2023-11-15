DEFINE tab_space_table = act0_tab
DEFINE tab_space_index = act0_idx
PROMPT Creating TABLE favorite_instrument

CREATE TABLE favorite_study (
    id              NUMBER(8, 0) NOT NULL
        CONSTRAINT pk_favorite_study PRIMARY KEY
            USING INDEX TABLESPACE &tab_space_index
                STORAGE ( INITIAL 10k ),
    user_id       NUMBER(8, 0),
    study_id   NUMBER(8, 0) NOT NULL
        CONSTRAINT fk_study_id
            REFERENCES study ( id )
)
TABLESPACE &tab_space_table
    STORAGE ( INITIAL 200K );
    

grant select, insert, update, delete on favorite_study to SPIRIT_USER;
grant select, insert, update, delete on favorite_study to SPIRIT_NG;
grant select, insert, update, delete on favorite_study to SPIRIT_FATCLIENT;

--drop SEQUENCE FAV_SEQ;
CREATE SEQUENCE FAVSTUDY_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
grant select on FAVSTUDY_SEQ to SPIRIT_USER;
grant select on FAVSTUDY_SEQ to SPIRIT_NG;
grant select on FAVSTUDY_SEQ to SPIRIT_FATCLIENT;

insert into favorite_study (id, study_id, user_id) values (FAVSTUDY_SEQ.nextval, 22162, 11412);
insert into favorite_study (id, study_id, user_id) values (FAVSTUDY_SEQ.nextval, 22074, 11412);
insert into favorite_study (id, study_id, user_id) values (FAVSTUDY_SEQ.nextval, 20201, 11412);
insert into favorite_study (id, study_id, user_id) values (FAVSTUDY_SEQ.nextval, 20462, 11412);
commit;