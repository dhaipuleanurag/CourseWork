with Text_Io;
with Ada.Command_Line;
use Text_Io;
use Ada.Command_Line;

procedure printsingleloop is
	package Int_Io is new Integer_Io(Integer);
	use Int_Io;
	
	task Print1 is
		entry Start1;
	end Print1;

	task Print2 is
		entry Start2;
	end Print2;
	
	task body Print1 is
	i:Integer;
	begin
	i := 1;
	accept Start1;
	while i <= 100
	loop
		if i mod 10 = 0 then 
			accept Start1;
			Print2.Start2;
		end if;
		Put(i);
		i:= i+1;
	end loop;
	end Print1;

	task body Print2 is
	i:Integer;
	begin
	i :=101;
	accept Start2;
	while i <= 200
	loop
		if i mod 10 = 0 then 
			Print1.Start1;
			accept Start2;
		end if;
		Put(i);
		i:= i+1;
	end loop;
	end Print2;

	begin
	Print1.Start1;
	end printsingleloop;