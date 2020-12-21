object Main {
  val VERBOSE = true

  def log(item:String):Unit = {
    if(VERBOSE){
      println(item)
    }
  }

  def countMatchingStrings(rules:Map[String, String], strings:List[String]):Int = {
    log(s"counting matching strings ( num strings = ${strings.length} , num rules = ${rules.size}")
    0
  }

  def main(args: Array[String]): Unit = {
    scala.io.Source.fromFile("input.txt").mkString.split("\n\n") match {
      case Array(rulesStr, stringStr) => {
        val rules = rulesStr.split("\n").map(_ match {
          case s"$id: $rule" => id -> rule
        }).toMap
        val strings = stringStr.split("\n").toList
        val matching = countMatchingStrings(rules, strings)
        if(VERBOSE) println(s"number of matching strings = $matching") else println(matching)
      }
      case _ => println("invalid input")
    }
  }
}
