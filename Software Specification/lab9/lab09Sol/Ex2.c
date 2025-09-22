#include "klee/klee.h"
#include <assert.h>


void f(int x, int y) { 
  if (x > y) {
    x = x + y; 
    y = x - y; 
    x = x - y; 
    if (x - y > 0) {
      assert(0);
    }
  }
}

int main() { 
  int x, y; 

  klee_make_symbolic(&x, sizeof(x), "x");
  klee_make_symbolic(&y, sizeof(y), "y");

  f(x, y);

}