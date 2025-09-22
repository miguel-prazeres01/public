#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


/**
 * Property: Se removermos um node válido a uma queue válida, ela deve
 * permanecer válida
 */

int main(){

    int pri1, data1, pri2, data2;

    klee_make_symbolic(&pri1, sizeof(int), "pri1");
    klee_make_symbolic(&data1, sizeof(int), "data1");
    klee_make_symbolic(&pri2, sizeof(int), "pri2");
    klee_make_symbolic(&data2, sizeof(int), "data2");
    

    klee_assume(pri1 >= 0);
    klee_assume(pri2 >= 0);

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    
}