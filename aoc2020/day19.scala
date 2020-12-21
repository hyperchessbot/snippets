object Main {
  val VERBOSE = true

  def log(item:String):Unit = {
    if(VERBOSE){
      println(item)
    }
  }

  case class ValidationResult(val ok:Boolean, val rest:String){
    def matchOk = ( ok && ( rest.length == 0 ) )
  }

  case class ValidationState(val string:String, val rest:String)

  def validateString(state:ValidationState):ValidationResult = {
    ValidationResult(false, state.rest)
  }

  def countMatchingStrings(rules:Map[String, String], strings:List[String]):Int = {    
    log(s"counting matching strings ( num strings = ${strings.length} , num rules = ${rules.size} )")
    strings.count(string => validateString(ValidationState(string, "")).matchOk)
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
