for(inp <- List("example", "input")){
println(inp)
val aoc2020SnippetsBaseUrl = "https://raw.githubusercontent.com/hyperchessbot/snippets/main/aoc2020/"
val input = scala.io.Source.fromURL(s"$aoc2020SnippetsBaseUrl/day24.$inp.txt").mkString.split("\n")
case class Vect(x:Int, y:Int){
  def add(v:Vect):Vect = Vect(x + v.x, y + v.y)
  def mult(s:Int):Vect = Vect(x * s, y * s)
}
val directions = Map[String, Vect]("ne" -> Vect(1, 1), "se" -> Vect(1, -1), "sw" -> Vect(-1, -1), "nw" -> Vect(-1, 1), "e" -> Vect(2, 0), "w" -> Vect(-2, 0))
val grid = scala.collection.mutable.Map[Vect, Boolean](Vect(0,0) -> false)
for(i <- 0 until 10){
  for((vect, _) <- grid){
    for((_, dir) <- directions){
      val v = vect.add(dir)
      grid.update(v, false)
    }  
  }
}

case class Instruction(initStr:String){
  var buff = initStr
  val steps = scala.collection.mutable.ArrayBuffer[Vect]()
  def aggr:Vect = {
    var temp = Vect(0, 0)
    for(step <- steps) temp = temp.add(step)    
    temp
  }
  override def toString:String = s"$steps"
  while(buff.length > 0){
    val char = buff.substring(0, 1)
    if((char == "n")||(char == "s")){
      val dir = buff.substring(0, 2)
      buff = buff.substring(2)
      steps += directions(dir)
    }else{      
      buff = buff.substring(1)
      steps += directions(char)
    }
  }
}

val instructions = input.map(Instruction(_)).toList
for(instruction <- instructions){  
  grid.updateWith(instruction.aggr)(_ match {
    case None => Some(true)
    case Some(flipped) => Some(!flipped)
  })
}
println(grid.keySet.count(grid(_)))
def move(grid:scala.collection.mutable.Map[Vect, Boolean]):scala.collection.mutable.Map[Vect, Boolean] = {
  val newGrid = scala.collection.mutable.Map[Vect, Boolean]()
  for((vect, flipped) <- grid){
    var cnt = 0    
    for((_, ndir) <- directions){
      val v = vect.add(ndir)      
      if(grid.contains(v)){
        if(grid(vect.add(ndir))) cnt += 1
      }else{
        newGrid.update(v, false)
      }
    }        
    val newFlipped = if(flipped){
      if((cnt == 0)||(cnt > 2)) false else flipped
    }else{
      if(cnt == 2) true else flipped
    }
    newGrid.update(vect, newFlipped)
  }
  newGrid
}

var temp = grid
for(i <- 0 until 100){    
  temp = move(temp)
}
println(temp.keySet.count(temp(_)))
}
