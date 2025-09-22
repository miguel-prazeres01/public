#include "klee/klee.h"
#include <assert.h>

void foobar(int a, int b) {
  int x=1, y=0; 
  if(a != 0) {
    y = 3 + x; 
    if (b == 0) {
      x = 2*(a+b);
    }
  } 
  assert((x-y) != 0);      
}

int main() { 
  int a, b; 

  klee_make_symbolic(&a, sizeof(a), "a");
  klee_make_symbolic(&b, sizeof(b), "b");

  foobar(a, b);

}