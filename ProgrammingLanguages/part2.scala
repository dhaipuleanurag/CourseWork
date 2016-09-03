abstract class Tree[+T]

case class Node[T](label:T, left:Tree[T], right:Tree[T]) extends Tree[T]
case class Leaf[T](label:T) extends Tree[T]

trait addable[T]{
def +(other:T):T
}

class A(x:Int) extends addable[A]{
val value = x
def +(other:A) = new A(x+other.value)
override def toString():String = "A(" + x +")"
}

class B(x:Int) extends A(x){
override def toString():String = "B(" + x +")"
}

class C(x:Int) extends B(x){
override def toString():String = "C(" + x +")"
}

object part2{
def inOrder[T](myTree: Tree[T]):List[T]= myTree match{
case Leaf(x) => List(x)
case Node(x,left,right) => inOrder(left)++(x::inOrder(right))
}

def treeSum[T <: addable[T]](myTree:Tree[T]):T = myTree match{
case Leaf(x) => x
case Node(x,left,right)=>x + treeSum(left) + treeSum(right) 
}

def treeMap[T,V](func:T=>V, myTree: Tree[T]):Tree[V] = myTree match{
case Leaf(x) => new Leaf(func(x))
case Node(x,left,right) => new Node(func(x), treeMap(func,left), treeMap(func,right))
}

def BTreeMap(func:B=>B, myTree: Tree[B]):Tree[B] = treeMap(func, myTree)

def test() {
def faa(a:A):A = new A(a.value+10)
def fab(a:A):B = new B(a.value+20)
def fba(b:B):A = new A(b.value+30)
def fbb(b:B):B = new B(b.value+40)
def fbc(b:B):C = new C(b.value+50)
def fcb(c:C):B = new B(c.value+60)
def fcc(c:C):C = new C(c.value+70)
def fac(a:A):C = new C(a.value+80)
def fca(c:C):A = new A(c.value+90)
val myBTree: Tree[B] = Node(new B(4),Node(new B(2),Leaf(new B(1)),Leaf(new B(3))), Node(new B(6), Leaf(new B(5)), Leaf(new B(7))))
val myATree: Tree[A] = myBTree
println("inOrder = " + inOrder(myATree))
println("Sum = " + treeSum(myATree))

//The below line produces compile error because BTreeMap expects the function to produce output of type B(or its derived) but faa produces type A.
//println(BTreeMap(faa,myBTree))

println(BTreeMap(fab,myBTree))

//The below line produces compile error because BTreeMap expects the function to produce output of type B(or its derived) but fba produces type A.
//println(BTreeMap(fba,myBTree))

println(BTreeMap(fbb,myBTree))
println(BTreeMap(fbc,myBTree))

//The below line produces compile error because BTreeMap expects the input type of function to be B but the input type of fcb is type C and we cannot pass type B(from myBTree) to fcb.
//println(BTreeMap(fcb,myBTree))

//The below line produces compile error because BTreeMap expects the input type of function to be B but the input type of fcc is type C and we cannot pass type B(from myBTree) to fcb.
//println(BTreeMap(fcc,myBTree))

println(BTreeMap(fac,myBTree))

//The below line produces compile error because BTreeMap expects the input type of function to be B but the input type of fca is type C and we cannot pass type B(from myBTree) to fcb.
//println(BTreeMap(fca,myBTree))

println(treeMap(faa,myATree))
println(treeMap(fab,myATree))

// Here the output of function passed doesn't matter as treeMap produces Tree[of that type]. The input to fba is of type A but we can only pass type B or its derived types.
//println(treeMap(fba,myATree))

// Here the output of function passed doesn't matter as treeMap produces Tree[of that type]. The input to fbb is of type A but we can only pass type B or its derived types.
//println(treeMap(fbb,myATree))

// Here the output of function passed doesn't matter as treeMap produces Tree[of that type]. The input to fbb is of type A but we can only pass type B or its derived types.
//println(treeMap(fbc,myATree))

// Here the output of function passed doesn't matter as treeMap produces Tree[of that type]. The input to fcb is of type A but we can only pass type C or its derived types.
//println(treeMap(fcb,myATree))

// Here the output of function passed doesn't matter as treeMap produces Tree[of that type]. The input to fcb is of type A but we can only pass type C or its derived types.
//println(treeMap(fcc,myATree))

println(treeMap(fac,myATree))

// Here the output of function passed doesn't matter as treeMap produces Tree[of that type]. The input to fcb is of type A but we can only pass type C or its derived types.
//println(treeMap(fca,myATree))
}

def main(){
test()
}
}