#include "scheduler_impl.h"
#include <zephyr/kernel.h>
#include "stdbool.h"
#include "gpio.h"
#include "usb.h"
#include <math.h>

// Index to indicate which task in the tasks array is active.
static int current_task = 0;

/// This is a very simple scheduler that demonstrates how the provided APIs can be used.
/// The function should call `set_active_task` to select which task should be running.
/// Then, it should return a sleep time in milliseconds, when the scheduler should next be executed.
k_timeout_t example(Task *tasks, int n, bool finished) {
    set_active_task(&tasks[current_task]);
    current_task = (current_task + 1) % n;
    return K_FOREVER;
}

static int start_time = -1;

int compare_RM (const void * a, const void * b) {
    Task *t_a = (Task *) a;
    Task *t_b = (Task *) b;

    return t_a->period - t_b->period;   
}

/// In this function you should write a rate monotonic scheduler.
/// Read the description of the `schedule` function below to read about the properties of this function.
k_timeout_t rate_monotonic(Task *tasks, int n, bool finished) {
    // You can initialize the new fields of your tasks here. This only runs the first time.
    if (start_time == -1) {
        start_time = k_uptime_get();
        current_task = -1;
        
        for (int i = 0; i < n; i++) {
            tasks[i].number_of_executions = 0;
        }   

        qsort(tasks, n, sizeof(Task), compare_RM);
    }

    if (current_task != -1 && finished){
        tasks[current_task].number_of_executions++;
    }

    //Maximum sleep time is the period of the task with the biggest period
    k_timepoint_t sleep_t_point = sys_timepoint_calc(K_MSEC(tasks[n-1].period));

    for (int i = 0; i < n; i++) {

        Task *task = &tasks[i];
        int current_time = k_uptime_get() - start_time;

        // If this timepoint passes, the scheduler will sleep for K_NO_WAIT after returning, and reexecute
        k_timepoint_t t_point = sys_timepoint_calc(K_MSEC(task->period - (current_time % task->period)));

        if (sys_timepoint_cmp(t_point, sleep_t_point) < 0)
            sleep_t_point = t_point;


        if (task->number_of_executions <= current_time / task->period) {
            //The current_time used in the condition is the same as the one used to calculate sleep, 
            //to garantee consistency between the sleep time and the task that will be executed

            set_active_task(task);
            current_task = i;

            return sys_timepoint_timeout(sleep_t_point);
        }
    }  

    // If all tasks are finished in their current period, the scheduler sleeps until the next period
    set_idle();
    current_task = -1;
    return sys_timepoint_timeout(sleep_t_point);
}

/// In this function you should write an earliest deadline first scheduler.
/// Read the description of the `schedule` function below to read about the properties of this function.
k_timeout_t earliest_deadline_first(Task *tasks, int n, bool finished) {
    // You can initialize the new fields of your tasks here. This only runs the first time.
    if (start_time == -1) {
        start_time = k_uptime_get();
        current_task = -1;
        
        for (int i = 0; i < n; i++) {
            tasks[i].number_of_executions = 0;
            tasks[i].priority = - tasks[i].period;
        }   
    }

    if (current_task != -1 && finished) {
        
        tasks[current_task].priority -= tasks[current_task].period;
        tasks[current_task].number_of_executions++;
    }

    current_task = -1;

    //Timepoints for the next period of each task
    //Note: Should use MAX_THREADS instead of 8, but it is not defined in scheduler_impl.h
    k_timepoint_t next_t_points[8];
    
    for (int i = 0; i < n; i++) {

        Task *task = &tasks[i];

        int current_time = k_uptime_get() - start_time;

        // Compute the timepoint for the next period of the task before validating if it is the next task to be executed
        // This is done to garantee consistency between the sleep time and the task that will be executed
        next_t_points[i] = sys_timepoint_calc(K_MSEC(task->period - (current_time % task->period)));

        if (( current_task == -1 || task->priority > tasks[current_task].priority ) && 
            task->number_of_executions <= current_time / task->period) {
                
                current_task = i;   
        }
    }

    //Time to sleep until the next task with higher is ready to be executed
    k_timepoint_t sleep_t_point;

    // If all tasks are finished in their current period, the scheduler sleeps until the next period
    if (current_task != -1) {
        set_active_task(&tasks[current_task]);

        sleep_t_point = next_t_points[current_task];
        
        for (int i = 0; i < n; i++) {

        if (tasks[i].priority > tasks[current_task].priority && sys_timepoint_cmp(next_t_points[i], sleep_t_point) < 0) {
            
            sleep_t_point = next_t_points[i];
        }   
    }

    } else {
        set_idle();

        sleep_t_point = next_t_points[0];

        for (int i = 1; i < n; i++) {

            if (sys_timepoint_cmp(next_t_points[i], sleep_t_point) < 0) {
                
                sleep_t_point = next_t_points[i];
            }   
        }
    }

    
    return sys_timepoint_timeout(sleep_t_point);
}

/// @brief This function can be used to choose which scheduler should be used.
///        This function is invoked when a task is finished or when the scheduler sleep timer has ended.
///        The sleep time is the duration of the timeout which the function returns (see the `\@return` for more information).
///        The called scheduler function should set the next task by calling `set_active_task` or call `set_idle` when there are no pending tasks.
/// @param tasks an array of tasks containing the full task set to schedule.
/// @param n the amount of tasks.
/// @param finished states whether the scheduler was called because a task was finished (true)
///                 or because the scheduler sleep timer expired (false).
/// @return The delay for which the scheduler thread should sleep.
///         You should make the scheduler sleep until a higher priority task is ready (preemption of the current running task)
///         or until there is a task ready to be scheduled when there were no tasks pending.
///         You must calculate this delay and return a k_timeout_t value containing this delay. Learn more about this here: 
///         https://docs.zephyrproject.org/latest/kernel/services/timing/clocks.html. Some of the functions/macros that you might find useful are
///         `K_MSEC`, `K_USEC`, `sys_timepoint_calc` and `sys_timepoint_timeout`.
k_timeout_t schedule(Task *tasks, int n, bool finished) {
    return rate_monotonic(tasks, n, finished);
}