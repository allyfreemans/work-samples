Implement the First-Come First-Served, preemptive Shortest Job First, and Round-Robin algorithms as for single processors.

Input: Your program will read a file from the current directory called processes.in, which will be formatted as follows.  Your program should ignore everything on a line after a # mark and ignore additional spaces in input.

Output: Generate a file called processes.out.

This version of Round-Robin should not run the scheduler immediately upon the arrival of a new process, unless the CPU is currently idle.

Your program will not be given an input that results in an ambiguous decision, such as identical arrival times for Round-Robin or identical burst lengths for SJF; you should avoid generating an error in that case on general principles but it will not appear in either the example inputs or the grading inputs.