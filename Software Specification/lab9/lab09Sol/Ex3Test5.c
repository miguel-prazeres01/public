#include "klee/klee.h"
#include "Ex3.h"
#include <stdlib.h> 
#include <assert.h>

/*
  Property: set_add preserves validity
*/

int main() {
  int a, b, c, d; 
  klee_make_symbolic(&a, sizeof(int), "a");
  klee_make_symbolic(&b, sizeof(int), "b");
  klee_make_symbolic(&c, sizeof(int), "c");

  Set s1 = set_add(NULL, a);
  s1 = set_add(s1, b);
  s1 = set_add(s1, c);

  assert(validSet(s1));
  return 0;
}