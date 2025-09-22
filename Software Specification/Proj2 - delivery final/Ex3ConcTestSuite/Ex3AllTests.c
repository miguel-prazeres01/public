#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>
#include "Ex3TestConc1.c"
#include "Ex3TestConc2.c"
#include "Ex3TestConc3.c"
#include "Ex3TestConc4.c"



int main() {
    test11();
    test12();
    test13();
    test14();
    test15();
    test16();

    test21();

    test31();
    test32();
    test33();
    test34();
    test35();
    test36();

    test41();
    test42();


}