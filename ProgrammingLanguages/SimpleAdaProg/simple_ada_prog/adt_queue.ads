package adt_queue is
   type queue is private;
   procedure extract(q: in out queue; x:integer);
   procedure insert(q: in out queue; x: out integer);
   procedure initialize(q: in out queue);
private
   type int_array is array(1..20) of integer;
   type queue is record
       the_queue: int_array;
       startIndex: integer;
       endIndex: integer;
	   end record;
end adt_queue;