#include "Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/*
 * Property: Se adicionarmos n elementos diferentes ao conjunto 
   vazio, criamos um conjunto de n elementos 
*/
int main () {

  int a, b, c; 

  klee_make_symbolic(&a, sizeof(int), "a");
  klee_make_symbolic(&b, sizeof(int), "b");
  klee_make_symbolic(&c, sizeof(int), "c");

  klee_assume(a != b); 
  klee_assume(b != c);
  klee_assume(c != a);

  Set s1 = set_add(NULL, a);
  s1 = set_add(s1, b); 
  s1 = set_add(s1, c); 

  int cnt = count(s1);

  assert(cnt == 3);



}