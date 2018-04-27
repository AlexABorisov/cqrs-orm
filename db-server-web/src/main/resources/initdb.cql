CREATE KEYSPACE IF NOT EXISTS clouddb WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 3 };
USE clouddb;

CREATE TABLE IF NOT EXISTS ChangeLog (id int, type text, time_stamp timestamp, command text ,properties map<text,text>,  primary key (id,type,time_stamp));

CREATE TABLE IF NOT EXISTS Generator (id int, primary key (id));
insert into generator (id) values (1);

CREATE TABLE IF NOT EXISTS ClassInfo (className text, fields map<text,text> ,parentClass text, primary key (className));