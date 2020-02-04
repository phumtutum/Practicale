import io.threadcso._
import scala.language.postfixOps
import scala.util.Random

object Sorting
{
    //Question 1 :
    // A single comparator, inputting on in0 and in1, and outputting on out0
    // (smaller value) and out1 (larger value). ∗/
    def comparator(in0 : ?[Int], in1 : ?[Int], out0 : ![Int], out1 : ![Int]) = proc
    {
        repeat
        {
            var x,y = 0;
            def read0 = proc
            {
                x = in0?
            }
            
            def read1 = proc
            {
                y = in1?
            }
            
            run(read0 || read1)

            if(x > y)
            {
                var aux = x
                x = y
                y = aux
            }

            def write0 = proc
            {
                out0!x
            }

            def write1 = proc
            {
                out1!y
            }

            run(write0 || write1)
        }

        out0.closeOut
        out1.closeOut
    }

    // Question 2
    // A sorting network for four values. 
    def sort4(ins : List[?[Int]], outs : List[![Int]]) : PROC = proc
    {
        var xs = List.fill(6)(OneOne[Int])

        run( comparator(ins(0), ins(2), xs(0), xs(2)) || comparator(ins(1), ins(3), xs(1), xs(3)) 
        || comparator(xs(0), xs(1), outs(0), xs(4)) || comparator(xs(2), xs(3), xs(5), outs(3))
        || comparator(xs(4), xs(5), outs(1), outs(2)) )
    }

    def sort4Test = 
    {
        var ins = List.fill(4)(OneOne[Int])
        var outs = List.fill(4)(OneOne[Int])
        var xs = Array.fill(4)(Random.nextInt(100))
        var ys = new Array[Int](4)

        def sender(i : Int) = proc
        {
            ins(i)!xs(i)
            ins(i).close
        }

        def receiver(i : Int) = proc
        {
            ys(i) = outs(i)?
        }

        run( sender(0) || sender(1) || sender(2) || sender(3) ||
        receiver(0) || receiver(1) || receiver(2) || receiver(3) ||
        sort4(ins, outs) )
        

        assert(xs.sorted.sameElements(ys))
    }
    
    // Question 3
    // Insert a value input on in into a sorted sequence input on ins.
    // Pre: ins.length = n && outs.length = n+1, for some n >= 1.
    // If the values xs input on ins are sorted, and x is input on in, then a
    // sorted permutation of x::xs is output on ys. ∗/
    def insert(ins: List[?[Int ]], in: ?[Int], outs: List [![ Int ]]): PROC = proc
    {
        val n = ins.length
        require(n >= 1 && outs.length == n+1);
        if(n == 1)
        {
            var x = in?
            var y = ins(0)?

            if(x > y)
            {
                outs(0)!y
                outs(1)!x
            }
            else
            {
                outs(0)!x
                outs(1)!y
            }
        }
        else
        {
            val nextIn = OneOne[Int]
            def adjuster = proc
            {
                var x = 0
                var y = 0
                run(proc{x = in?} || proc{y = ins(0)?})
                if(x > y)
                {
                    var aux = x
                    x = y
                    y = aux
                }

                run(proc{ nextIn!y } || proc{ outs(0)!x })
            }

            run( comparator(in, ins(0), outs(0), nextIn) || insert(ins.tail, nextIn, outs.tail) )
        }

    }
    //Question 4
    //Binary search approach
    def insertOptimized(ins: List[?[Int ]], in: ?[Int], outs: List [![ Int ]]): PROC = proc
    {

        val n = ins.length
        println(n + " " + outs.length)
        require(outs.length == n+1);
        println("size e " + n)
        if(n == 0)
        {
            outs(0)!(in?)
        }
        else
        {
            val nextIn = OneOne[Int]
            var x = in?
            var mid = (n-1)/2
            var elem = ins(mid)?


            def transfer(otherIns : List[?[Int]], otherOuts : List[![Int]]) : PROC = proc
            {   
                val sz = otherIns.length
                println("transfer: " + sz + " " + otherOuts.length)
                require(sz >= 0 && sz == otherOuts.length)
                if(sz > 0)
                {
                    run( proc{ otherOuts(0)!(otherIns(0)?) } || transfer(otherIns.tail, otherOuts.tail) )
                }
            }
            
            if(elem >= x)
            {
                println("prefix of " + (mid+1))
                //x should be inserted in the first half
                run(proc{ nextIn!x; nextIn.close } || proc{ outs(mid+1)!elem } ||
                 insertOptimized(ins.take(mid), nextIn, outs.take(mid+1)) ||
                transfer(ins.drop(mid+1), outs.drop(mid+2)) )
            }
            else
            {
                println("suffix of " + (mid+1))
                val auxChan = OneOne[Int]
                //x should be inserted in the second half
                run(proc{auxChan!elem; auxChan.close } || proc{outs(mid)!(auxChan?)} 
                || proc{nextIn!x; nextIn.close} || insertOptimized(ins.drop(mid+1), nextIn, outs.drop(mid+1))
                || transfer(ins.take(mid) , outs.take(mid)) )
            }
        }
    }

    def insertTest = 
    {
        val N = 5
        val xs = Array(701, 1, 2, 5, 700)
        val ys = new Array[Int](N)
        val ins = List.fill(N)(OneOne[Int])
        val outs = List.fill(N)(OneOne[Int])

        def sender(i : Int) = proc
        {
            ins(i)!(xs(i))
            ins(i).close
        }

        def receiver(i : Int) = proc
        {
            ys(i) = outs(i)?
        }
        run(sender(0) || sender(1) || sender(2) || sender(3)  || sender(4) ||
        receiver(0) || receiver(1) || receiver(2) || receiver(3) || receiver(4) ||
        insert(ins.tail, ins(0), outs) )
        ys.foreach(println)
        assert(xs.sorted.sameElements(ys))
    }

    //def insertionSort(ins: List[?[Int ]], outs: List [![ Int ]]): PROC = {
        
    //}

    def main(args : Array[String]) =
    {
        insertTest
    }
}
