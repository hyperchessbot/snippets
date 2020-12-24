#include <iostream>
#include <string.h>

const int RING_SIZE = 9;

char input[] = "389125467";

int ring[RING_SIZE + 1];

int current = input[0] - '0';

void print_ring(){
  int temp = 1;
  for(int i = 0; i < 9; i++){
    if(temp == current) std::cout << "*";
    std::cout << temp << " ";
    temp = ring[temp];
  }
  std::cout << std::endl;
}

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

int main() {
  
  for(int i = 10; i < RING_SIZE; i++){
    ring[i] = i + 1;
  }

  for(int i = 1; i < strlen(input); i++){
    ring[input[i - 1] - '0'] = input[i] - '0';
  }

  ring[input[strlen(input) - 1] - '0'] = input[0] - '0';
  
  //ring[RING_SIZE] = current;

  for(int i = 0; i < 100; i++){    
    move();
    print_ring();
  }  
}
