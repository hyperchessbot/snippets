import time

def make_circle(cupstring):
  length = len(cupstring)
  next_cup = [-1]*(length+1)
  current = int(cupstring[0])
  length = len(cupstring)
  for i, cup in enumerate(cupstring):
    next_cup[int(cup)] = int(cupstring[(i+1) % length])
  return next_cup, current, length

def make_circle2(cupstring, circle_len):
  next_cup=[-1]*(circle_len+1)
  current = int(cupstring[0])
  for i, cup in enumerate(cupstring[:-1]):
    next_cup[int(cup)] = int(cupstring[(i+1)])
  #print(next_cup)
  next_cup[int(cupstring[-1])] =  len(cupstring)+1
  for i in range(len(cupstring)+1, circle_len+1):
    #print(i)
    next_cup[i] = i+1
  next_cup[circle_len]=int(cupstring[0])
  return next_cup, current


def print_circle(next_cup,current, length, end1 = False):
  cups = ''
  cup_on = current if not(end1) else next_cup[1]
  for i in range(length):
    cups += str(cup_on)
    if not(end1): cups+= ','
    cup_on = next_cup[cup_on]
  if end1: cups = cups[:-1]
  print(cups)

def one_round(next_cup, current, length, debug = False):
  pickup = [next_cup[current]] #the cups you *will pick up*
  for i in range(2): 
    pickup.append(next_cup[pickup[-1]])
  dest = current-1 if current > 1 else length
  while dest in pickup:
    dest = (dest-1) if dest > 1 else length 
  if debug: print(pickup, dest)
  old_next = next_cup[dest]
  next_cup[current] = next_cup[pickup[-1]] #currnet cup points to the one after the one picked up 
  next_cup[dest] = pickup[0]
  next_cup[pickup[-1]] = old_next
  current = next_cup[current]
  if debug: print_circle(next_cup)
  return current

def n_rounds(cupstr, n):
  cups, current, length = make_circle(cupstr)
  for _ in range(n):
    current = one_round(cups, current, length)
    #print(cups)
  print('ending string:')
  print_circle(cups, current, length, end1 = True)

def n_rounds2(cupstr, n, circle_size):
  cups, current = make_circle2(cupstr, circle_size)
  for _ in range(n):
    current = one_round(cups, current, circle_size)
  print('after the 1:')
  print(cups[1],'*',cups[cups[1]],' = ', cups[1]*cups[cups[1]])

t0 = time.time()

n_rounds("389125467", 100)
n_rounds2("389125467", 10000000, 1000000)

elapsed = int((time.time() - t0) / 1000000)

print(elapsed)
