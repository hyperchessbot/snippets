def solve(input:String):Unit = {
  
}

for(inp <- List("example")){  
  val input = scala.io.Source.fromURL(s"https://raw.githubusercontent.com/hyperchessbot/snippets/main/aoc2020/day25.$inp.txt").mkString
  println(s"$inp ( size ${input.length} )")
  solve(input)
}
