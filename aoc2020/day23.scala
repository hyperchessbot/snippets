object Main {
  case class Cup(value:Int){
    var next:Cup = null
    override def toString:String = s"$value"
  }

  case class Ring(initStr:String){
    val cups = initStr.split("").map(num => Cup(num.toInt)).toList
    var mapCups = scala.collection.mutable.Map[Int, Cup](cups.map(cup => cup.value -> cup).toSeq:_*)    
    for(i <- 0 until cups.length - 1) cups(i).next = cups(i + 1) ; cups.last.next = cups(0)
    var current = cups(0)
    override def toString:String = {
      var ( buff , temp ) = ( "" , mapCups(1) )      
      do{
        buff += (if(temp == mapCups(1)) s"(${temp.toString})" else temp.toString)
        temp = temp.next
      }while(temp != mapCups(1))
      buff
    }
    def removeNextAt(cup: Cup):Cup = {
      val nextCup = cup.next
      cup.next = cup.next.next      
      nextCup
    }
    def removeAt(cup: Cup, size:Int):List[Cup] = (for(_ <- 0 until size) yield removeNextAt(cup)).toList
    def findCup(value:Int, removed:List[Cup]):Option[Cup] = {
      if(removed.map(_.value).contains(value)) return None
      Some(mapCups(value))
    }
    var maxValue = 0
    def move():Unit = {
      val removed = removeAt(current, 3)            
      var search = current.value
      do{
        search -= 1
        if(search < 1) search = maxValue
      }while(findCup(search, removed).isEmpty)
      val dest = mapCups(search)
      val destNext = dest.next
      dest.next = removed(0)
      removed(2).next = destNext
      current = current.next
    }
    def product:Long = {
        val cup1 = mapCups(1)
        val cupNext1 = cup1.next.value.toLong
        val cupNext2 = cup1.next.next.value.toLong
        println(cupNext1, cupNext2)
        cupNext1 * cupNext2
    }
    def makeMoves(n:Int, part:Int, verbose:Boolean = false):Unit = {
      maxValue = mapCups.keySet.max
      for(i <- 0 until n){
        move()        
        if(verbose) if(i % 1000000 == 0) println(i)
      }
      if(part == 1) println(this) else println(product)
    }
    def augmentTo(size:Int):Ring = {
      var last = cups.last
      for(i <- 10 until size + 1){
        val newCup = Cup(i)
        mapCups.update(i, newCup)
        last.next = newCup
        last = newCup        
      }
      last.next = current      
      this
    }
  }
  
  def main(args: Array[String]): Unit = {    
    val t0 = System.nanoTime()

    Ring("389125467").makeMoves(100, 1)
	  //Ring("467528193").makeMoves(100, 1)
    Ring("389125467").augmentTo(1000000).makeMoves(10000000, 2)
	  //Ring("467528193").augmentTo(1000000).makeMoves(10000000, 2)

    val elapsed = (System.nanoTime() - t0) / 1000000L

    println(elapsed)
  }
}
