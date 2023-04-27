
drop table if exists questions;

create table if not exists questions
(
    question_id          integer primary key,
    score                integer,
    view_count           integer,
    answer_count         integer,
    is_answered          boolean,
    title                varchar,
    tags                 varchar,
    accepted_answer_id   integer,
    last_activity_date   date,
    creation_date        date,
    closed_date          date,
    last_edit_date       date,
    body                 varchar,
    user_id              integer

);

drop table if exists answers;
create table if not exists answers
(
    answer_id          integer primary key,
    is_accepted boolean,
    score              integer,
    last_activity_date date,
    creation_date      date,
    question_id integer,
    tags varchar,
    body varchar

);

drop table if exists tags;
create table if not exists tags
(
    name varchar primary key,
    count integer,
    has_synonyms boolean
);

drop table if exists users;
create table if not exists users
(
    user_id integer primary key,
    reputation integer,
    accept_rate integer,
    post_count integer,
    score integer
);

drop table if exists comments;
create table if not exists comments
(
    comment_id integer primary key,
    score integer,
    creation_date date,
    post_id integer,
    user_id integer,
    edited boolean,
    body varchar
);


