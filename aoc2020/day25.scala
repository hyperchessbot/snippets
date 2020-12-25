val divisor = 20201227L
val subjectNumber = 7L

def transform(pubKey:Long):Long = {
  var current = 1L
  for(loop <- 1L until 20L){
    current = current * subjectNumber
    current = current % divisor
    if(current == pubKey) return loop
  }
  0L
}

def solve(input:String):Unit = {
  val pubKeys = input.split("\n").map(_.toLong).toList
  println(pubKeys, subjectNumber, divisor)
  println(transform(5764801))
}

for(inp <- List("example")){  
  val input = scala.io.Source.fromURL(s"https://raw.githubusercontent.com/hyperchessbot/snippets/main/aoc2020/day25.$inp.txt").mkString
  println(s"$inp ( size ${input.length} )")
  solve(input)
}
