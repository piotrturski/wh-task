drop function if exists initcap;

delimiter //

create function initcap( str longtext ) returns longtext
begin
	declare word_separators varchar(3) default ' \t\n'; -- those chars delimit words and make next letter uppercase

	declare i int default 1;
	declare beginning_of_word bool;
	declare current_char varchar(1);
	declare new_char varchar(1);
	declare result longtext default '';

	while i <= length(str) do
		set beginning_of_word = locate(substring(str, i-1, 1), word_separators) > 0; -- returns true also for 1st letter
		set current_char = substring(str, i, 1);
		set new_char = if(beginning_of_word, upper(current_char), lower(current_char));
		set result = concat(result, new_char);
		set i = i + 1;
	end while;

	return result;
end //

delimiter ;

-- select initcap('a bC	cc
-- dD');