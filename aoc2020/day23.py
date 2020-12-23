import time

class Cup():
  def __init__(self, num):
    self.num = num
    self.nextCup = None
  def __repr__(self):
    return f"{self.num}"

class Ring():
  def __init__(self, cupsStr, augmentTo = None):
    self.cups = [Cup(int(num)) for num in list(cupsStr)]
    if not ( augmentTo is None ):
      for i in range(10, augmentTo + 1):
        self.cups.append(Cup(i))
    self.current = self.cups[0]
    for i in range(1, len(self.cups)):
      self.cups[i - 1].nextCup = self.cups[i]
    self.cups[-1].nextCup = self.cups[0]    
    self.cupsDict = {cup.num:cup for cup in self.cups}
    
  def remove_one_at_cup(self, cup):
    removed_cup = cup.nextCup
    cup.nextCup = removed_cup.nextCup
    return removed_cup

  def remove_n_at_cup(self, cup, n):
    acc = []
    for _ in range(n):
      acc.append(self.remove_one_at_cup(cup))
    return acc

  def __repr__(self):
    buff = ""
    temp = self.cupsDict[1]
    for i in range(min(len(self.cupsDict), 100)):
      buff = buff + (temp.__repr__() if i > 0 else "(" + temp.__repr__() + ")")
      temp = temp.nextCup
    return buff

  def find_cup_by_num(self, num, removed):
    if num in [cup.num for cup in removed]:
      return None    
    return self.cupsDict[num]

  def move(self):    
    removed = self.remove_n_at_cup(self.current, 3)
    temp = len(self.cupsDict) if self.current.num <= 1 else self.current.num - 1
    while self.find_cup_by_num(temp, removed) is None:
      temp = len(self.cupsDict) if temp <= 1 else temp - 1
    insert_cup = self.cupsDict[temp]
    old_nextCup = insert_cup.nextCup
    insert_cup.nextCup = removed[0]
    removed[2].nextCup = old_nextCup
    self.current = self.current.nextCup

  def make_moves(self, n, part = 1):
    for i in range(n):
      self.move()      
    if part == 1:
      print(self)
    else:
      cup1 = self.cupsDict[1]
      next1 = cup1.nextCup.num
      next2 = cup1.nextCup.nextCup.num
      print(next1, next2, next1 * next2)

t0 = time.time()

Ring("389125467").make_moves(100)
Ring("389125467", augmentTo = 1000000).make_moves(10000000, 2)

elapsed = time.time() - t0

print(elapsed)
