val aoc2020SnippetsBaseUrl = "https://raw.githubusercontent.com/hyperchessbot/snippets/main/aoc2020/"
val input = scala.io.Source.fromURL(s"$aoc2020SnippetsBaseUrl/day24.input.txt").mkString.split("\n")
case class Vect(x:Int, y:Int){
  def add(v:Vect):Vect = Vect(x + v.x, y + v.y)
}
val grid = scala.collection.mutable.Map[Vect, Boolean]()
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
val directions = Map[String, Vect]("ne" -> Vect(1, 1), "se" -> Vect(1, -1), "sw" -> Vect(-1, -1), "nw" -> Vect(-1, 1), "n" -> Vect(0, 2), "e" -> Vect(2, 0), "s" -> Vect(0, -2), "w" -> Vect(-2, 0))
val instructions = input.map(Instruction(_)).toList
for(instruction <- instructions){  
  grid.updateWith(instruction.aggr)(_ match {
    case None => Some(true)
    case Some(flipped) => Some(!flipped)
  })
}
//println(instructions(0).initStr, instructions(0).steps)
println(grid.keySet.count(grid(_)))
