package Sort is

SIZE : Integer := 40;
subtype Element is Integer range -501 .. 501;
type List is array(1 .. SIZE) of Element;
procedure MergeSort(arrayElements: in out List; p: Integer; r: Integer);

end Sort;
