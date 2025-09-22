#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/**
 * Property: Se adicionarmos um node válido a uma queue válida, ela deve
 * permanecer válida
 */

int main(){

    int pri1, pri2, data1, data2, pri3, data3, pri4, data4;

    klee_make_symbolic(&pri1, sizeof(int), "pri1");
    klee_make_symbolic(&pri2, sizeof(int), "pri2");
    klee_make_symbolic(&data1, sizeof(int), "data1");
    klee_make_symbolic(&data2, sizeof(int), "data2");
    klee_make_symbolic(&pri3, sizeof(int), "pri3");
    klee_make_symbolic(&data3, sizeof(int), "data3");
    

    klee_assume(pri1 >= 0);
    klee_assume(pri2 >= 0);
    klee_assume(pri3 >= 0);


    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
    
}