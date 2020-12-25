val divisor = 20201227L
val subjectNumber = 7L

def getLoopSize(pubKey:Long, subjectNumber:Long = 7):Long = {
  var current = 1L
  for(loop <- 1L until 100000000L){
    current = current * subjectNumber
    current = current % divisor
    if(current == pubKey) return loop
  }
  0L
}

def transform(subjectNumber:Long, loopSize:Long):Long = {
  println("transforming subject number", subjectNumber, "with loop size", loopSize)
  var current = 1L
  for(loop <- 0L until loopSize){
    current = current * subjectNumber
    current = current % divisor    
  }
  current
}

def solve(input:String):Unit = {
  val pubKeys = input.split("\n").map(_.toLong).toList
  println(pubKeys, subjectNumber, divisor)
  val loops = pubKeys.map(getLoopSize(_))
  println(loops)
  for(i <- 0 until pubKeys.length){
    println(transform(pubKeys(i), loops(1 - i)))
  }
}

for(inp <- List("example", "input")){  
  val input = scala.io.Source.fromURL(s"https://raw.githubusercontent.com/hyperchessbot/snippets/main/aoc2020/day25.$inp.txt").mkString
  println(s"$inp ( size ${input.length} )")
  solve(input)
}
