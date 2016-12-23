-- docker run --rm -e MYSQL_ALLOW_EMPTY_PASSWORD=true  mysql:5.7.17

select v.*, @curRank := @curRank + 1 AS rank
from votes v, (SELECT @curRank := 0) r
order by votes desc;