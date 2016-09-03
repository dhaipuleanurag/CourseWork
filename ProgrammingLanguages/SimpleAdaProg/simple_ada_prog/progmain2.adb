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
	My_File  : FILE_TYPE;
begin
	
	Open(My_File, In_File, "arrayElements.txt");
	Put(Argument(1));
	
	i:=1;
	while Not End_Of_File(My_File) 
	loop
		Get(My_File, arrayElements(i));
		Put(arrayElements(i));         
		i:=i+1;
	end loop;
	
	Put("Before Merge:");
	i:=1;
	while i <= SIZE
	loop
		Put(arrayElements(i));
		i:= i+1;
	end loop;
	
	MergeSort(arrayElements, 1, SIZE);
	Put("After Merge:");
	i:=1;
	while i <= SIZE
	loop
		Put(arrayElements(i));
		i:= i+1;
	end loop;
	
end ProgMain;
	
