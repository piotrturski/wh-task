drop table if exists sometbl;

CREATE TABLE sometbl ( ID INT, NAME VARCHAR(50) );
INSERT INTO sometbl VALUES (1, 'Smith'), (2, 'Julio|Jones|Falcons'), (3,'White|Snow'), (4, 'Paint|It|Red'),
 (5, 'Green|Lantern'), (6, 'Brown|bag|'), (7, ''), (8, null);

drop procedure if exists to_rows;

drop table if exists result_table;
create table result_table ( ID INT, NAME VARCHAR(50) );

delimiter //

create procedure to_rows()
begin
	declare single_string, concatenated longtext;
	declare row_id, separator_pos int;
	declare line_processing bool;
	DECLARE no_more_rows bool DEFAULT false;
	declare cur cursor for select id, name from sometbl;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_rows = true;

	open cur;

	get_row: loop
		fetch cur into row_id, concatenated;
		if no_more_rows then
			leave get_row;
		end if;

		set line_processing = true;
		while line_processing do
			set separator_pos = locate('|', concatenated);
			if ifnull(separator_pos,0) = 0
			then
				set single_string = concatenated;
				set line_processing = false;
			else
				set single_string = left(concatenated, separator_pos-1);
				set concatenated = substring(concatenated, separator_pos+1);
			end if;
			insert into result_table values (row_id, single_string);
		end while;
	end loop;

	close cur;

end //

delimiter ;

call to_rows();
select * from result_table;
