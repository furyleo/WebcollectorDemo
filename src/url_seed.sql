--------------------------------------------oracle

--select * from user_sys_privs;

--grant resource to crawler;


--create user crawler identified by crawler;

--建表
create table url_seed
(
id number(11) primary key,
url varchar2(200)
);

--序列
create sequence seq_url_seed
minvalue 1
nomaxvalue
start with 1
increment by 1
cache 100
nocycle;

--触发器
CREATE OR REPLACE TRIGGER "CRAWLER"."TRG_TB_CONTENT"
before insert on url_seed
for each row
begin
 select seq_url_seed.nextval into:NEW.id from dual;
end;

--select seq_url_seed.nextval from dual;
insert into url_seed(url) values('www.taobao.com');
--insert into url_seed(id,url) values(seq_tb_content.nextval,'www.jingdong.com');
