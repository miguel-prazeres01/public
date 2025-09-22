#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>



void test21(){
    int pri1=2147483647, pri2=2147483647 , data1=0, data2=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    
    assert(validPVList(n1) && validPVList(n2));

    Node aux = n1;
    n1 = removeNode(n1,n2);
    assert (validPVList(n1) && n1==aux);      
}

