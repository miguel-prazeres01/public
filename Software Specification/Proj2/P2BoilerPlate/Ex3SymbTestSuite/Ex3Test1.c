#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/**
 * Property: Se adicionarmos um node válido à priority-value list ela deve
 * permanecer válida  
 */


int main(){

    int pri1, pri2 , data1, data2;

    klee_make_symbolic(&pri1, sizeof(int), "pri1");
    klee_make_symbolic(&pri2, sizeof(int), "pri2");
    klee_make_symbolic(&data1, sizeof(int), "data1");
    klee_make_symbolic(&data2, sizeof(int), "data2");


    Node n1 = makeNode(pr1,data1);
    Node n2 = makeNode(pri2,data2);

    n1 = insertPVPair(n1,n2);

    assert (validPVList(n1)==1);

}

