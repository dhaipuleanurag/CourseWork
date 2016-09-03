with Text_Io;
use Text_Io;
package body Sort is 
	
	package Int_Io is new Integer_Io(Integer);
	use Int_Io;
	
	procedure MergeSort(arrayElements:in out List; p: Integer; r: Integer ) is
	
		procedure RunTasks(p: Integer; q: Integer; r: Integer) is
			task first;
			task body first is
			begin
				MergeSort(arrayElements, p, q);
			end first;
	
			task second;
			task body second is
			begin
				MergeSort(arrayElements, q+1, r);
			end second;
		begin
		null;
		end;
		
		procedure Merge(p: Integer; q: Integer; r: Integer) is
		A: List;
		B: List;
		LengthofA: Integer;
		LengthofB: Integer;
		i: Integer;
		j: Integer;
		N: Integer;
		begin
		LengthofA := q - p + 1;
		LengthofB := r - q;
		A(LengthofA + 1):=501;
		B(LengthofB + 1):=501;
		i:=0;
		while i < LengthofA 
		loop
			A(i+1) := arrayElements(p+i);
			i:=i+1;
		end loop;

		i:=0;
		while i < LengthofB 
		loop
			B(i+1) := arrayElements(q+1+i);
			i:=i+1;
		end loop;
		
		i:=1;
		j:=1;
		N:=0;
		while N<=(r-p)
		loop
			if A(i) <= B(j) then
				arrayElements(N+p) := A(i);
				i:=i+1;
			else
				arrayElements(N+p) := B(j);
				j := j+1;
			end if;
			N:= N+1;
		end loop;
		end;
		
	q: Integer;
	begin
		if p < r then
			q:=	(p+r)/2; 
			RunTasks(p, q, r);
			Merge(p, q, r);
		end if;
	end;
end Sort;
