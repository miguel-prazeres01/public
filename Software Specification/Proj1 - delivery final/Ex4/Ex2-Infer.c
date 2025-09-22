
#include <stdio.h>
#include <stdlib.h>

struct ret_pair_struct {
  int* fst;
  int snd;
}; 

typedef struct ret_pair_struct* ret_pair; 

int countDiff (int arr[], int size) {

  int ret = 1; 
  int prev = arr[0]; 

  for (int i=1; i<size; i++) {
    if (arr[i] != prev) {
      prev = arr[i];
      ret++;
    }
  }

  return ret; 
}

ret_pair removeRepsArr(int arr[], int size) {
    
  int new_size = countDiff(arr, size);

  int* new_arr = (int*)malloc(new_size*sizeof(int)); //Infer BUG: Nullptr Dereference. True Positive

  int prev = arr[0]; 
  new_arr[0] = prev; 
  int new_i = 1;  

  for (int i=1; i<size; i++) {
    if (arr[i] != prev) {
      prev = arr[i];
      new_arr[new_i] = prev;
      new_i++; 
    } 
  }

  ret_pair rp = (ret_pair)malloc(sizeof(struct ret_pair_struct)); 
  rp->fst = new_arr; 
  rp->snd = new_size;

  return rp;  
}


void removeRepsArrIP(int arr[], int size) {
  int prev = arr[0]; 
  int new_i = 1; 

  for (int i=1; i<size; i++) {
    if (arr[i] != prev) {
      prev = arr[i];
      arr[new_i] = prev; 
      new_i++; 
    }
  }

  for (; new_i<size; new_i++) {
    arr[new_i] = 0; 
  }
}


void printArr(int* arr, int size) {
  for (int i=0; i<size; i++) {
    printf("%d ", arr[i]);
  }
  printf("\n"); 
}

int main () {
  
  int n=0; 

  printf("Insert number of elements:\n");
  scanf("%d\n", &n);

  int* arr = (int*)malloc(sizeof(int)*n); //Infer BUG: Nullptr Dereference. True Positive

  int prev = 0; 
  for(int i=0; i<n; i++) { 
    int k=0; 
    
    while (1) {
      scanf("%d", &k);
      if (k >= prev) break;
      printf("you have to insert your numbers in increasing order\n");
    }
    
    prev = k; 
    arr[i] = k; 
  }
  
  printArr(arr, n); 

  ret_pair rp = removeRepsArr(arr, n); //Infer BUG: Memory Leak/Nullptr Dereference. True Positive
  printArr(rp->fst, rp->snd); 

  removeRepsArrIP(arr, n);
  printArr(arr, n);

  return 0; 

}
