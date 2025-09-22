#include <zephyr/kernel.h>
#include <zephyr/kernel/thread.h>
#include <zephyr/drivers/gpio.h>

extern int scheduler_thread;

/// This represents a Task and all additional information necessary to schedule the task.
typedef struct {
    /// The id assigned to this task by Zephyr. Used internally by `set_active_task`.
    k_tid_t task_id;
    /// The maximal time the task will take to execute in ms.
    int execution_time;
    /// The scheduler should aim to execute this task every `period` ms.
    int period;
    /// Feel free to add your own fields that are used for scheduling below.
    //...

    //int relative_deadline; TODO: ask if we need this

    int number_of_executions;
    int priority;

} Task;

Task spawn_task(int execution_time, int period, const struct gpio_dt_spec *pin);

void set_active_task(Task *task);

void set_idle();

void run_scheduler(Task *tasks, int n);