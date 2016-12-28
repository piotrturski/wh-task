select v.*, cast(@curRank := @curRank + 1  as signed) AS rank -- cast fixes type for empty result
from votes v, (SELECT @curRank := 0) r
order by votes desc;