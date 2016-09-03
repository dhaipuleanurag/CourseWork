package body adt_queue is
	procedure extract(q:in out queue; x:integer) is
	begin
		x :=  q.the_queue(q.startIndex);
		q.startIndex = q.startIndex +1;
		if (q.startIndex > 20) then
				q.startIndex = 1;
	end;

	procedure insert(q: in out queue; x: out integer) is
	begin
		q.the_queue(q.endIndex) := x;
		q.endIndex := q.endIndex + 1;
		if (q.endIndex > 20) then
				q.endIndex = 1;
	end;

	procedure initialize(q: out queue) is
	begin
		q.startIndex := 0;
		q.endIndex := 1;
	end;
end adt_queue;