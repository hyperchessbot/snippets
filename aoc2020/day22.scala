object Main {
  case class Player(id:Int, initialCards: Seq[Int]){
    var cards = scala.collection.mutable.ArrayBuffer[Int](initialCards:_*)
    override def clone():Player = Player(id, cards.clone.toSeq)
    def total = cards.zipWithIndex.map(item => (cards.length - item._2) * item._1).sum
    override def toString():String = s"[Player $id , cards = ${cards.mkString(" , ")}]"
    def draw:Int = cards.remove(0)
  }

  case class Game(players:Seq[Player], part:Int = 1){
    val player1 = players(0).clone
    val player2 = players(1).clone
    val configs = scala.collection.mutable.Set[String]()
    def config = player1.toString() + " | " + player2.toString()
    def result = if(configs.contains(config)) 1 else if(player1.cards.length == 0) 2 else if(player2.cards.length == 0) 1 else 0
    def total = if(result == 0) 0 else if(result == 1) player1.total else player2.total    
    def playRound():Boolean = {      
      if(result != 0) return false      
      configs += config
      val card1 = player1.draw
      val card2 = player2.draw
      var winner = if(card1 > card2) 1 else if(card2 > card1) 2 else 0      
      if(part == 2) if((card1 <= player1.cards.length) && (card2 <= player2.cards.length)) winner = Game(Seq(Player(1, player1.cards.slice(0, card1).toSeq), Player(2, player2.cards.slice(0, card2).toSeq))).playGame()
      if(winner == 1) player1.cards ++= List(card1, card2)
      if(winner == 2) player2.cards ++= List(card2, card1)
      return true
    }
    def playGame():Int = {
      while(playRound()){}
      return result
    }
    playGame()
  }

  def main(args: Array[String]): Unit = {    
    val players = scala.io.Source.fromFile("input.txt").mkString.split("\n\n").zipWithIndex.map(cards => (Player(cards._2 + 1, cards._1.split("\n").tail.map(_.toInt).toSeq))).toList

    println(Game(players, 1).total)
    println(Game(players, 2).total)
  }
}
