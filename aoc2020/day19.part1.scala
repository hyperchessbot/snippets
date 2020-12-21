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

  case class ValidationState(val string:String, val rule:String)

  var rules = Map[String, String]()

  def validateString(state:ValidationState):ValidationResult = {    
    if(state.string.length == 0) return ValidationResult(ok = true, rest = "")
    state.rule.replaceAll("\"", "!") match {
      case s"!$terminal!" => {
        val char = state.string.substring(0, 1)
        if(char == terminal) return ValidationResult(ok = true, rest = state.string.substring(1)) else return ValidationResult(ok = false, rest = state.string)
      }
      case _ => {
        def checkBranch(branch:String):ValidationResult = {
          var currentString = state.string
          for(ruleId <- branch.split(" ")){            
            val result = validateString(ValidationState(string = currentString, rule = rules(ruleId)))
            if(!result.ok) return result
            currentString = result.rest
          }
          ValidationResult(ok = true, rest = currentString)
        }
        for(branch <- state.rule.split(" \\| ")){
          val result = checkBranch(branch)
          if(result.ok) return result
        }
        ValidationResult(ok = false, rest = state.string)
      }
    }
    ValidationResult(ok = false, rest = state.string)
  }

  def countMatchingStrings(strings:List[String]):Int = {    
    log(s"counting matching strings ( num strings = ${strings.length} , num rules = ${rules.size} )")    
    strings.count(string => validateString(ValidationState(string = string, rule = rules("0"))).matchOk)
  }

  def main(args: Array[String]): Unit = {
    scala.io.Source.fromFile("day19.input.txt").mkString.split("\n\n") match {
      case Array(rulesStr, stringStr) => {
        rules = rulesStr.split("\n").map(_ match {
          case s"$id: $rule" => id -> rule
        }).toMap
        val strings = stringStr.split("\n").toList
        val matching = countMatchingStrings(strings)
        if(VERBOSE) println(s"number of matching strings = $matching") else println(matching)
      }
      case _ => println("invalid input")
    }
  }
}
