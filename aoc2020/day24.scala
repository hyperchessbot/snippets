import scala.collection.mutable.{ArrayBuffer, Map => MutMap}

for(inp <- List("example", "input")){
println(inp)
val aoc2020SnippetsBaseUrl = "https://raw.githubusercontent.com/hyperchessbot/snippets/main/aoc2020/"
val input = scala.io.Source.fromURL(s"$aoc2020SnippetsBaseUrl/day24.$inp.txt").mkString.split("\n")
  
case class Vect(x:Int, y:Int){def add(v:Vect):Vect = Vect(x + v.x, y + v.y)}
  
val directions = Map[String, Vect]("ne" -> Vect(1, 1), "se" -> Vect(1, -1), "sw" -> Vect(-1, -1), "nw" -> Vect(-1, 1), "e" -> Vect(2, 0), "w" -> Vect(-2, 0))
  
var grid = MutMap[Vect, Boolean](Vect(0,0) -> false)
  
// initialize grid
for(i <- 0 until 25 ; (vect, _) <- grid ; (_, dir) <- directions) grid.update(vect.add(dir), false)

case class Instruction(initStr:String){
  var buff = initStr
  val steps = ArrayBuffer[Vect]()  
  while(buff.length > 0){
    val char = buff.substring(0, 1)
    if((char == "n")||(char == "s")){
      steps += directions(buff.substring(0, 2))
      buff = buff.substring(2)
    }else{      
      steps += directions(char)
      buff = buff.substring(1)      
    }
  }
  def aggr:Vect = steps.foldLeft(Vect(0,0))((a, b) => a.add(b))  
}

for(instruction <- input.map(Instruction(_))) grid.updateWith(instruction.aggr)(_ match {
  case None => Some(true)
  case Some(flipped) => Some(!flipped)
})
  
println(grid.values.count(identity))
  
def move(grid:MutMap[Vect, Boolean]):MutMap[Vect, Boolean] = {
  val newGrid = MutMap[Vect, Boolean]()
  for((vect, flipped) <- grid){    
    val cnt = directions.values.map(ndir => grid.getOrElse(vect.add(ndir), false)).count(identity)
    newGrid.update(vect, if(flipped) if((cnt == 0)||(cnt > 2)) false else flipped else if(cnt == 2) true else flipped)
  }
  newGrid
}

for(i <- 0 until 100) grid = move(grid)
  
println(grid.values.count(identity))
}
