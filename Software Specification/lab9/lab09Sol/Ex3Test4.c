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



int main() {
  int a, b, c, d; 
  klee_make_symbolic(&a, sizeof(int), "a");
  klee_make_symbolic(&b, sizeof(int), "b");
  klee_make_symbolic(&c, sizeof(int), "c");

  klee_make_symbolic(&d, sizeof(int), "d");

  Set s1 = set_add(NULL, a);
  s1 = set_add(s1, b);
  s1 = set_add(s1, c);

  int count1 = count(s1);

  s1 = remove_s(s1, d);

  int count2 = count(s1);

  if (d==a || d==b || d==c) {
    assert(count2==count1-1);
  } else {
    assert(count2==count1);
  }

  return 0;
}