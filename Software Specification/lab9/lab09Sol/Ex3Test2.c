#include "Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/*
 * Property: Se adicionarmos n elementos ao conjunto 
   vazio, o tamanho do conjunto resultante coincide com 
   o numero de elementos diferentes 
*/

int count_diff(int a, int b, int c) {
  if (a == b && b == c) {
    return 1;
  } else {
    if (a != b && b!=c && c!=a) {
      return 3;
    } else {
      return 2; 
    }
  }
}

int main () {

  int a, b, c; 

  klee_make_symbolic(&a, sizeof(int), "a");
  klee_make_symbolic(&b, sizeof(int), "b");
  klee_make_symbolic(&c, sizeof(int), "c");

  Set s1 = set_add(NULL, a);
  s1 = set_add(s1, b); 
  s1 = set_add(s1, c); 

  int cnt = count(s1);

  assert(cnt == count_diff(a, b, c));



}