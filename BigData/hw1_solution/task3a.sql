select medallion, pickup_datetime from trips group by medallion,pickup_datetime having count(*) > 1 order by medallion;