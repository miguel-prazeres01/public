#include "klee/klee.h"
#include "Ex3.h"
#include <stdlib.h> 
#include <assert.h>

/*
  Property: If we create a set with n elements, x1, ..., xn, 
  and we call the function find with on the created set 
  with one of the n elements, find should return 1; otherwise, 
  it should return 0
*/

int is_contained(int a, int b, int c, int d) {
    return d==a || d==b || d==c; 
}

int main() {
  int a, b, c, d; 
  klee_make_symbolic(&a, sizeof(int), "a");
  klee_make_symbolic(&b, sizeof(int), "b");
  klee_make_symbolic(&c, sizeof(int), "c");

  klee_make_symbolic(&d, sizeof(int), "d");

  Set s1 = set_add(NULL, a);
  s1 = set_add(s1, b);
  s1 = set_add(s1, c);

  int found = find(s1, d);

  assert(found == is_contained(a, b, c, d));

  return 0;
}