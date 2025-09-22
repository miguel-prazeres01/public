#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/**
 * Property: Se tentarmos eliminar um node de uma lista que não o contém,
 * esta deve permanecer igual 
 */


int main(){

    int pri1, pri2 , data1, data2;

    klee_make_symbolic(&pri1, sizeof(int), "pri1");
    klee_make_symbolic(&pri2, sizeof(int), "pri2");
    klee_make_symbolic(&data1, sizeof(int), "data1");
    klee_make_symbolic(&data2, sizeof(int), "data2");

    klee_assume(pri1 >= 0);
    klee_assume(pri2 >= 0);


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    
    assert(validPVList(n1) && validPVList(n2));

    Node aux = n1;
    n1 = removeNode(n1,n2);
    assert (validPVList(n1) && n1==aux);   

}