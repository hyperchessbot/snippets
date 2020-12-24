#include <iostream>
#include <string.h>

const int MAX_RING_SIZE = 1000000;

char input[] = "389125467";

int ring[MAX_RING_SIZE + 1];

int current;

void print_ring(int upto){
  int temp = 1;
  long prod = 1;
  for(int i = 0; i < upto; i++){
    if(temp == 1) std::cout << "( ";
    //if(temp == current) std::cout << "*";
    std::cout << temp << " ";
    if(temp == 1) std::cout << ") ";
    prod = prod * temp;
    temp = ring[temp];
  }
  if(upto == 3){
    std::cout << "prod " << prod;
  }  
   std::cout << std::endl;
}

int RING_SIZE;

void move(){
  int n1 = ring[current];
  int n2 = ring[n1];
  int n3 = ring[n2];  

  int temp = current - 1;
  while((temp == n1)||(temp == n2)||(temp == n3)) {
    temp --;
    if(temp < 1) temp = RING_SIZE;
  }
  if(temp < 1) temp = RING_SIZE;
  while((temp == n1)||(temp == n2)||(temp == n3)) {
    temp --;
  }

  ring[current] = ring[n3];
  current = ring[n3];

  int old = ring[temp];  
  ring[temp] = n1;
  ring[n3] = old;
}

void solve(int part){
  current = input[0] - '0';

  RING_SIZE = strlen(input);

  if(part == 2){
    RING_SIZE = 1000000;

    for(int i = 10; i < RING_SIZE; i++){
      ring[i] = i + 1;
    }
  }

  for(int i = 1; i < strlen(input); i++){
    ring[input[i - 1] - '0'] = input[i] - '0';
  }

  if(part == 1){
    ring[input[strlen(input) - 1] - '0'] = input[0] - '0';
  }else{
    ring[input[strlen(input) - 1] - '0'] = 10;
    ring[RING_SIZE] = current;
  }

  for(int i = 0; i < (part == 1 ? 100 : 10000000); i++){    
    move();
  }  

  print_ring(part == 1 ? 9 : 3);
}

int main() {
  solve(1);

  solve(2);
}
