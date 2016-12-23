drop table if exists bugs;
create table bugs (

id int,
open_date date,
close_date date,
severity int
);

-- in mysql it sucks. you need some table; 1 => 356; 10 => 3560, 100 => 35'600

insert into bugs values (1, '2016-01-05', null, 3), (2, '2016-01-03', '2016-01-04', 1);

set @start_date = '2016-01-03';
set @end_date = '2016-01-04';

-- assmptions: open_date, @start_date, @end_date are never null

select count(1) from bugs
where
	open_date <= @start_date and 
	ifnull(close_date > @end_date, true)
	