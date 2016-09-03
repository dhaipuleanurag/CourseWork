Control.Print.printDepth := 100;
Control.Print.printLength := 100;

fun partition x [] = ([],[])
   |partition x (y::ys) = 
		let val (a,b) = partition x ys 
	in 
	if y<x 
		then (y::a,b) 
	else 
		(a,y::b) end

fun partitionSort [] = []
   |partitionSort [x] = [x]
   |partitionSort (x::xs) = 
		let val (a,b) = partition x xs 
   in 
	(partitionSort a)@(x::partitionSort b) 
   end

fun Sort (op <) [] = []
   |Sort (op <) [x] = [x]
   |Sort (op <) (x::xs) = 
		let fun partition (op <) x [] = ([],[])
			   |partition (op <) x (y::ys) = 
					let val (a,b) = partition (op <) x ys 
				in 
				if y<x 
					then (y::a,b) 
				else 
					(a,y::b) end 
			val (a,b) = partition (op <) x xs 
	in 
	(Sort (op <) a)@(x::Sort (op <) b) 
	end
	
fun someFunction f g a =
	hd (g [(f a)])

datatype 'a tree = empty | leaf of 'a | node of 'a * 'a tree * 'a tree

exception emptyException

fun maxTree (op <) empty = raise emptyException 
   |maxTree (op <) (leaf a) = a
   |maxTree (op <) (node (a,empty,empty)) = a
   |maxTree (op <) (node (a,empty,b)) =
					if a<(maxTree (op <) b) 
						then (maxTree (op <) b)
					else
						a
   |maxTree (op <) (node (a,b,empty)) =
					if a<(maxTree (op <) b)
						then (maxTree (op <) b)
					else
						a
   |maxTree (op <) (node (a,b,c)) = 
			let fun comp (op <) (a,b,c) = 
				if a<(maxTree (op <) b) 
					then if (maxTree (op <) c)<(maxTree (op <) b) 		
						then (maxTree (op <) b) 
					else 
						(maxTree (op <) c) 
				else 
					if (maxTree (op <) c)<a 
						then a 
					else (maxTree (op <) c) 
					in 
					comp (op <) (a,b,c) 
					end

fun preorder a b c = a::(b@c)
fun inorder a b c = b@(a::c)
fun postorder a b c = (b@c)@[a]
fun Labels order empty = []
   |Labels order (leaf x) = [x]
   |Labels order (node(a,b,c)) = order a (Labels order b) (Labels order c)

fun lexLess (op <) [] [] = false 
   |lexLess (op <) [] [a] = true 
   |lexLess (op <) [a] [] = false 
   |lexLess (op <) [] a = true 
   |lexLess (op <) a [] = false 
   |lexLess (op <) (a::ax) (b::bx) = 
		if a<b 
			then true 
		else 
			if b<a 
				then false 
			else lexLess (op <) ax bx

fun sortTreeList (op <) x = Sort (fn (a,b) => (lexLess (op <) (Labels inorder a) (Labels inorder b))) x