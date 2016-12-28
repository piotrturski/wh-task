/*
assmptions:
  - open_date, @start_date, @end_date are never null
  - count bugs open during the whole perion: since @start_date inclusive to @end_date inclusive
*/


-- set @start_date = '2016-01-03';
-- set @end_date = '2016-01-04';

select count(1) from bugs
where
	open_date <= @start_date and 
	ifnull(close_date > @end_date, true)
	