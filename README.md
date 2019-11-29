# CSE_6431_restaurant
CSE 6431, Au 2019 - Programming Assignment

## Execution

To run the solution, you'll have to compile and execute it using Java. 
For example, you can use the following set of commands:

```shell
javac src/*.java
java -cp src Restaurant input/input-1.txt > output/output-1.txt
```

The first command will compile the Java files while the second command
will run them. You can change input files to give the programming different inputs. 

## Input

Input is expected to be a txt file. 
Input is expected
to be in the following format:

```text
# of diners (n)
# of tables
# of cooks
n lines of diners and their orders
```

Each diner line should be in the following format:

```text
time,burgerCount,friesCount,cokeCount
```

One sample:

```text
3
2
2
5,1,1,1
6,2,0,1
7,1,2,1
```
All the sample inputs are saved in directory *input*.

## Output

The format of the output is also the same as what described in the PDF file.
For the sample input, the sample output is:
 
```text
00:05 - Diner 1 arrives.
00:05 - Diner 1 is seated at table 1.
00:05 - Cook 2 processes Diner 1's order.
00:05 - Cook 2 uses the burger machine.
00:06 - Diner 2 arrives.
00:06 - Diner 2 is seated at table 2.
00:06 - Cook 1 processes Diner 2's order.
00:07 - Diner 3 arrives.
00:10 - Cook 1 uses the burger machine.
00:10 - Cook 2 uses the fries machine.
00:13 - Cook 2 uses the coke machine.
00:14 - Diner 1's order is ready. Diner 1 starts eating.
00:20 - Cook 1 uses the coke machine.
00:21 - Diner 2's order is ready. Diner 2 starts eating.
00:44 - Diner 1 finishes. Diner 1 leaves the restaurant.
00:44 - Diner 3 is seated at table 1.
00:44 - Cook 1 processes Diner 3's order.
00:44 - Cook 1 uses the burger machine.
00:49 - Cook 1 uses the fries machine.
00:51 - Diner 2 finishes. Diner 2 leaves the restaurant.
00:55 - Cook 1 uses the coke machine.
00:56 - Diner 3's order is ready. Diner 3 starts eating.
01:26 - Diner 3 finishes. Diner 3 leaves the restaurant.
01:26 - The last diner leaves the restaurant.
```

All the sample outputs are saved in directory *output*.

## Description

My implementation creates one thread for each arriving diner. And there is also one thread for each cook, all of them are active for the entire duration when the restaurant is open. 

When the diner thread starts, it is first added into a priority queue using arrival time as key to compete for a table. This guarantees diners come first would first get a table. Then the same strategy is used for competing for a cook. 

For cooks, when they try to make food based on one order. They should decide what to do first (e.g., burgers first fries second coke third). My strategy is first to select the burger machine or the fries machine considering which one would finish its current work at an earlier time. In my programming, I use a variable *finishedTime* to achieve my strategy (although I have to admit that my implementation is not perfect since I did not consider synchronization on this variable, it helps in some cases). Finally, the cook starts to make coke since the maximum amount of cook in one order is only 1, it would not take much time. 

In my simulation, 2 seconds stand for one minute in the PDF.

Since I use *notifyAll()* in my implementation, there would be some starvation case which may be improved later, although all the diners would finally eat food and leave the restaurant. 