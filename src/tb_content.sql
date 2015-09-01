----------------------------------------------oracle


--建表
create table tb_content
(
id number(11) primary key,
url varchar2(200),
keyword varchar2(50)
);
--序列
create sequence seq_tb_content
minvalue 1
nomaxvalue
start with 1
increment by 1
cache 100
nocycle;
--触发器，序列自增
CREATE OR REPLACE TRIGGER "CRAWLER"."TRG_TB_CONTENT"
before insert on tb_content
for each row
begin
 select seq_tb_content.nextval into:NEW.id from dual;
end;

insert into tb_content(url) values('www.taobao.com');
