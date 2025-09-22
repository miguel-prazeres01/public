#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/**
 * Property: Se criarmos uma lista ela deve ser válida. Se adicionarmos um node ou removermos
 * um node à lista, ela deve permanecer válida. 
 */


int main(){

    int pri1, pri2 , data1, data2, pri3, data3;

    klee_make_symbolic(&pri1, sizeof(int), "pri1");
    klee_make_symbolic(&pri2, sizeof(int), "pri2");
    klee_make_symbolic(&data1, sizeof(int), "data1");
    klee_make_symbolic(&data2, sizeof(int), "data2");
    klee_make_symbolic(&pri3, sizeof(int), "pri3");
    klee_make_symbolic(&data3, sizeof(int), "data3");

    klee_assume(pri1 >= 0);
    klee_assume(pri2 >= 0);
    klee_assume(pri3 >= 0);


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1));    

}

