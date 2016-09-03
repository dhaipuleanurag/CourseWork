import java.util.*;
public class part1 {
    public static void main(String[] args) {
        test();
    }

    public static <T extends Comparable<T>> void addToSortedList(SortedList<T> L, T z)
    {
        L.add(z);
    }
    
    static void test() {
	SortedList<A> c1 = new SortedList<A>();
	SortedList<A> c2 = new SortedList<A>();
	for(int i = 35; i >= 0; i-=5) {
	    addToSortedList(c1, new A(i,i+1));
	    addToSortedList(c2, new B(i+2,i+3,i+4));
	}
	
	System.out.print("c1: ");
	System.out.println(c1);
	
	System.out.print("c2: ");
	System.out.println(c2);

	switch (c1.compareTo(c2)) {
	case -1: 
	    System.out.println("c1 < c2");
	    break;
	case 0:
	    System.out.println("c1 = c2");
	    break;
	case 1:
	    System.out.println("c1 > c2");
	    break;
	default:
	    System.out.println("Uh Oh");
	    break;
	}

	Sorted<A> res = c1.merge(c2);
	System.out.print("Result: ");
	System.out.println(res);
    }
}


interface Sorted<E extends Comparable<E>> extends List<E> 
{
    Sorted<E> merge(Sorted<E> param);
}

class SortedList<E  extends Comparable<E>> extends ArrayList<E> implements Sorted<E>, Comparable<SortedList<E>>
{
    @Override
    public Sorted<E> merge(Sorted<E> o) {
        SortedList<E> mergedList = new SortedList<E>();
        int thisListSize = this.size();
        int otherListSize = o.size();
        int i = 0;
        while(i < thisListSize)
        {
            mergedList.add(this.get(i));
            i++;
        }
        int j = 0;
        while(j < otherListSize)
        {
            mergedList.add(o.get(j));
            j++;
        }
        return mergedList;
    }

    @Override
    public int compareTo(SortedList<E> o) {
        int thisListSize = this.size();
        int otherListSize = o.size();
        int i = 0;
        int j = 0;
        while(true)
        {
            int value = this.get(i).compareTo(o.get(j));
            if(value == 0)
            {
                i++;
                j++;
                if(i == thisListSize && j == otherListSize)
                {
                    return 0;
                }
                if(i == thisListSize)
                {
                    return -1;
                }
                if(j == otherListSize)
                {
                    return 1;
                }
            }
            else if(value > 0)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }
    
    @Override
    public boolean add(E e) 
    {
        if(this.size()==0)
        {
            super.add(e);
        }
        else
        {
            super.add(e);
            int i;
            for(i=size()-2;i>=0;i--)
            {
                E temp=this.get(i);
                if(temp.compareTo(e)>0)
                {
                    this.set(i+1, temp);
                }
                else
                {
                    break;
                }
            }
            this.set(i+1, e);
        }
    return true;
    }
    
    @Override
    public String toString()
    {
        String output = "[[";
        int i = 0;
        int size = this.size();
        while(i<size)
        {
            output = output + this.get(i)+" ";
            i++;
        }
        output = output + "]]";
        return output;
    }
}

class A implements Comparable<A>
{
    int x;
    int y;
    int sum;
    A(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.sum = x+y;
    }
    
    @Override
    public String toString()
    {
        return "A<" + x +","+ y+ ">";
    }
    
    @Override
    public int compareTo(A o) {
        if(this.sum > o.sum)
        {
            return 1;
        }
        if(this.sum < o.sum)
        {
            return -1;
        }
        return 0;
    }
}

class B extends A
{
    int z;
    B(int x, int y, int z)
    {
        super(x,y);
        this.z = z;
        this.sum = this.sum + z;
    }
    
    @Override
    public String toString()
    {
        return "B<" + x +","+ y+ "," + z +">";
    }
}