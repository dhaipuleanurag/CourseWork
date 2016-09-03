with Sort;
use Sort;

with Text_Io;
with Ada.Command_Line;
use Text_Io;
use Ada.Command_Line;

procedure ProgMain is
	package Int_Io is new Integer_Io(Integer);
	use Int_Io;
	
	arrayElements : List;
	i: Integer := 1;
	Sum: Integer;
	
	task Reader is
		entry Start;
	end Reader;
	task body Reader is
	begin
	i:=1;
	accept Start do
	while i <= SIZE 
	loop
		Get(arrayElements(i));
		i:=i+1;
	end loop;
	end Start;
	end Reader;

	task SumComputer is
		entry Start;
	end SumComputer;
	
	
	task PrintArray is
		entry Start;
		entry PrintSum;
	end PrintArray;
	
	task body SumComputer is
	begin
	accept Start;
	Sum:=0;
	i:=1;
	while i <= SIZE
		loop
		Sum := Sum + arrayElements(i);
		i:= i+1;
	end loop;
	PrintArray.PrintSum;
	end SumComputer;
	
	task body PrintArray is
	begin
	accept Start;
	i :=1;
	New_Line;
	Put("Array is:");
	New_Line;
	while i <= SIZE
	loop
		Put(arrayElements(i));
		New_Line;
		i:= i+1;
	end loop;
	accept PrintSum;
	Put("Sum is: ");
	Put(Sum);
	New_Line;
	end PrintArray;

	begin
	Reader.Start;
	MergeSort(arrayElements, 1, SIZE);
	SumComputer.Start;
	PrintArray.Start;
end ProgMain;
	
