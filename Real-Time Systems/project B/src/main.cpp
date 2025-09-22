/*
 * Copyright (c) 2016 Intel Corporation
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include "Switch.hpp"
#include "audio.h"
#include "key.hpp"
#include "leds.h"
#include "peripherals.h"
#include "synth.hpp"
#include "usb.h"
//#include "Scheduler.h"
#include <math.h>
#include <zephyr/kernel.h>
#include <zephyr/logging/log.h>

#define STACK_SIZE 1024
#define MAX_THREADS 3

#define TH1_PRIORITY 3
#define TH2_PRIORITY 2
#define TH3_PRIORITY 4
//#define TH4_PRIORITY 4

#define TH1_PERIOD 5
#define TH2_PERIOD 100
#define TH3_PERIOD 50
//#define TH4_PERIOD 50


K_THREAD_STACK_DEFINE(stack1, STACK_SIZE);
K_THREAD_STACK_DEFINE(stack2, STACK_SIZE);
K_THREAD_STACK_DEFINE(stack3, STACK_SIZE);
//K_THREAD_STACK_DEFINE(stack4, STACK_SIZE);


int priorities[MAX_THREADS] = {TH1_PRIORITY, TH2_PRIORITY, TH3_PRIORITY};
k_thread_stack_t *stacks[MAX_THREADS] = {stack1, stack2, stack3};
//int leds[MAX_THREADS] = {&debug_led0, &debug_led1, &debug_led2, &debug_led3};
struct k_work_q work_q[MAX_THREADS];

// Syntheizer instance
Synthesizer synth;
// Buffer for writing to audio driver
void *mem_block;
int current_block = 1;

void check_keyboard() {
  char character;
  while (usbRead(&character, 1)) {
    auto key = Key::char_to_key(character);
    bool key_pressed = false;
    for (int i = 0; i < MAX_KEYS; i++) {
      if (key == keys[i].key && keys[i].state != IDLE) {
        keys[i].state = PRESSED;
        keys[i].hold_time = sys_timepoint_calc(K_MSEC(500));
        keys[i].release_time = sys_timepoint_calc(K_MSEC(500));
        key_pressed = true;
      }
    }
    // The second loop is necessary to avoid selecting an IDLE key when a
    // PRESSED or RELEASED key is located further away on the array
    if (!key_pressed) {
      for (int i = 0; i < MAX_KEYS; i++) {
        if (keys[i].state == IDLE) {
          keys[i].key = key;
          keys[i].state = PRESSED;
          keys[i].hold_time = sys_timepoint_calc(K_MSEC(500));
          keys[i].release_time = sys_timepoint_calc(K_MSEC(500));
          keys[i].phase1 = 0;
          keys[i].phase2 = 0;
          break;
        }
      }
    }
  }
}


void peripherals_update_work_handler(struct k_work *work) {

  set_led(&debug_led0);
  peripherals_update();
  reset_led(&debug_led0);
}

void check_keyboard_work_handler(struct k_work *work) {

  set_led(&debug_led1);
  check_keyboard();
  reset_led(&debug_led1);
}

void makesynth_work_handler(struct k_work *work) {

  //Timer to prevent overloding
  k_timepoint_t timeout = sys_timepoint_calc(K_MSEC(TH3_PERIOD - 2));

  set_led(&debug_led2);
  synth.makesynth(((uint8_t *) mem_block) + (int) BLOCK_SIZE * current_block, timeout);
  reset_led(&debug_led2);

  current_block = (current_block + 1) % 2;

  set_led(&debug_led3);
  writeBlock(((uint8_t *) mem_block) + (int) BLOCK_SIZE * current_block);
  reset_led(&debug_led3);
}

/* void writeBlock_work_handler(struct k_work *work) {

  set_led(&debug_led3);
  writeBlock(((uint8_t *) mem_block) + (int) BLOCK_SIZE * current_block);
  reset_led(&debug_led3);
} */


K_WORK_DEFINE(peripherals_update_work, peripherals_update_work_handler);
K_WORK_DEFINE(check_keyboard_work, check_keyboard_work_handler);
K_WORK_DEFINE(makesynth_work, makesynth_work_handler);
//K_WORK_DEFINE(writeBlock_work, writeBlock_work_handler);


void peripherals_update_timer_handler(struct k_timer *timer) {
  k_work_submit_to_queue(&work_q[0], &peripherals_update_work);
}

void check_keyboard_timer_handler(struct k_timer *timer) {
  k_work_submit_to_queue(&work_q[1], &check_keyboard_work);
}

void makesynth_timer_handler(struct k_timer *timer) {
  k_work_submit_to_queue(&work_q[2], &makesynth_work);
}

/* void writeBlock_timer_handler(struct k_timer *timer) {
  k_work_submit_to_queue(&work_q[3], &writeBlock_work);
} */

K_TIMER_DEFINE(peripherals_update_timer, peripherals_update_timer_handler, NULL); 
K_TIMER_DEFINE(check_keyboard_timer, check_keyboard_timer_handler, NULL);
K_TIMER_DEFINE(makesynth_timer, makesynth_timer_handler, NULL);
//K_TIMER_DEFINE(writeBlock_timer, writeBlock_timer_handler, NULL);


int main(void) {
  initUsb();
  waitForUsb();

  init_leds();
  initAudio();
  init_peripherals();

  synth.initialize();

  mem_block = allocBlock(); 
  memset(mem_block, 0, BLOCK_SIZE * 2);

  reset_led(&debug_led0);
  reset_led(&debug_led1);
  reset_led(&debug_led2);
  reset_led(&debug_led3);

  for (int i = 0; i < MAX_THREADS; i++) {
    k_work_queue_init(&work_q[i]);
    k_work_queue_start(&work_q[i], stacks[i], STACK_SIZE, priorities[i], NULL);
  }

  k_timer_start(&peripherals_update_timer, K_NO_WAIT, K_MSEC(TH1_PERIOD));
  k_timer_start(&check_keyboard_timer, K_NO_WAIT, K_MSEC(TH2_PERIOD));
  k_timer_start(&makesynth_timer, K_NO_WAIT, K_MSEC(TH3_PERIOD));
  //k_timer_start(&writeBlock_timer, K_NO_WAIT, K_MSEC(TH4_PERIOD));

  k_thread_suspend(k_current_get());

  /* int64_t time = k_uptime_get();

  int state = 0;
  while (1) {
    // Run the superloop slightly faster than once every 50 ms
    if (k_uptime_get() - time > BLOCK_GEN_PERIOD_MS-1) {
      time = k_uptime_get();

      if (state) {
        set_led(&status_led0);
      } else {
        reset_led(&status_led0);
      }
      state = !state;

      // Check the peripherals input
      set_led(&debug_led0);
      peripherals_update();
      reset_led(&debug_led0);

      // Get user input from the keyboard
      set_led(&debug_led1);
      check_keyboard();
      reset_led(&debug_led1);

      // Make synth sound
      set_led(&debug_led2);
      synth.makesynth((uint8_t *)mem_block);
      reset_led(&debug_led2);

      // Write audio block
      set_led(&debug_led3);
      writeBlock(mem_block);
      reset_led(&debug_led3);
    }
  }

  printuln("== Finished initialization =="); */

  return 0;
}



