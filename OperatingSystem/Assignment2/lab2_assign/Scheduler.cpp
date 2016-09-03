// Scheduler.cpp : Defines the entry point for the console application.

#include <stdio.h>
#include <iostream>
#include <math.h>
#include <vector>
#include <iostream>
#include <sstream>
#include <fstream>
#include <string>
#include <iomanip>
#include <stdexcept>
#include <cstdlib> 
#include <stdlib.h>

using namespace std;
enum processState {Created, Ready, Running, Block, Done};
class Process
{
public :
	int arrivalTime;
	int cpuTimeRemaining;
	int totalCPUTime;
	int cpuBurst;
	int ioBurst;
	int processId;
	int staticPriority;
	int dynamicPriority;
	int finishingTime;
	int ioTime;
	int cpuWaitingTime;
	
	int currentBurstStartTimeStamp;
	int currentBurstTime;
	int previousBurstTime;
	int currentQueuePosition;
	processState currentState;

	int getExpectedEndTime()
	{
		return currentBurstStartTimeStamp + currentBurstTime;
	}
};

class Transition
{
public:
	int processId;
	int timeStamp;
	int transitionDuration;
	processState oldState;
	processState newState;
	int currentBurstTime;
	int previousBurstTime;
	int cpuTimeRemaining;
	int priority;

};

class IScheduler
{
public:
	virtual Transition* getEvent() = 0;
	virtual void putEvent(Transition*) = 0;
};

class Scheduler : public IScheduler
{
public:
	int currentTime;
	string filePath;
	string randFilePath;
	vector<Process*> eventQueue;
	vector<Process*> readyQueue;
	vector<int> trackQueue;
	int eventQueueLength = 0;
	int readyQueueLength = 0;
	bool wasProcessPreempted = false;
	
	vector<int> randomValues;
	int currentRandomValuePointer = -1;
	int totalRandomValues;

	bool isIOGoingOn = false;
	int countOfProcessInIO = 0;
	int lastProcessInIOTimeStamp;
	double totalIOTime = 0;

	int actualReadyQueueLength;
	int quantum = 99999;

	bool isCPUFree = true;

	Process* getMoreWaitingInSameStateProcess(Process* process1, Process* process2)
	{
		int i = trackQueue.size() - 1;
		while (i >= 0)
		{
			if (process1->processId == trackQueue[i])
			{
				return process2;
			}
			if (process2->processId == trackQueue[i])
			{
				return process1;
			}
			i--;
		}
		return NULL;
	}
	
	void populateEventQueue()
	{
		ifstream read(filePath);
		int arrivalTime, cpuTime, cpuBurst, ioBurst;
		int i = 0;
		while (read >> arrivalTime >> cpuTime >> cpuBurst >> ioBurst)
		{
			Process* process = new Process();
			process->processId = i;
			process->arrivalTime = arrivalTime;
			process->currentBurstStartTimeStamp = arrivalTime;
			process->currentBurstTime = 0;
			process->cpuTimeRemaining = cpuTime;
			process->totalCPUTime = cpuTime;
			process->cpuBurst = cpuBurst;
			process->ioBurst = ioBurst;
			process->staticPriority = getNextRandomNumber(4);
			process->dynamicPriority = process->staticPriority - 1;
			eventQueue.push_back(process);
			eventQueueLength++;
			i++;
		}
	}

	
	Transition* updateTransition(Process* process, Transition* transition)
	{
		transition->priority = process->staticPriority;
		transition->processId = process->processId;
		transition->timeStamp = process->currentBurstStartTimeStamp;
		transition->cpuTimeRemaining = process->cpuTimeRemaining;
		transition->newState = process->currentState;
		transition->priority = process->dynamicPriority;
		transition->currentBurstTime = process->currentBurstTime;
		transition->previousBurstTime = process->previousBurstTime;
		return transition;
	}

	int getNextRandomNumber(int max)
	{
		if (currentRandomValuePointer == -1)
		{
			int randomValue;
			ifstream read(randFilePath);
			read >> totalRandomValues;
			while (read >> randomValue)
			{
				randomValues.push_back(randomValue);
			}
			currentRandomValuePointer = 0;
			return (1 + randomValues[currentRandomValuePointer] % max);
		}

		else
		{
			currentRandomValuePointer++;
			if (currentRandomValuePointer >= totalRandomValues)
			{
				currentRandomValuePointer = 0;
			}
			return 1 + randomValues[currentRandomValuePointer] % max;
		}
	}
	
	Process* getNextProcessToRun(Process* first, Process* second)
	{
		if (first == NULL)
		{
			if (second != NULL)
			{
				return second;
			}
		}
		if (second == NULL)
		{
			return first;
		}
		int endTimeForFirstProcess;
		if (first->currentState == Ready)
		{
			endTimeForFirstProcess = currentTime;
		}
		else if (first->currentState == Running && first->currentBurstTime > quantum)
		{
			endTimeForFirstProcess = first->currentBurstStartTimeStamp + quantum;
		}
		else
		{
			endTimeForFirstProcess = first->getExpectedEndTime();
		}

		int endTimeForSecondProcess;
		if (second->currentState == Ready)
		{
			endTimeForSecondProcess = currentTime;
		}
		else if (second->currentState == Running && second->currentBurstTime > quantum)
		{
			endTimeForSecondProcess = second->currentBurstStartTimeStamp + quantum;
		}
		else
		{
			endTimeForSecondProcess = second->getExpectedEndTime();
		}
		if (endTimeForFirstProcess <= endTimeForSecondProcess)
		{
			if (endTimeForFirstProcess == endTimeForSecondProcess)
			{
				if (first->currentState == Created)
				{
					return first;
				}
				/*if (first->currentBurstStartTimeStamp <= second->currentBurstStartTimeStamp)
				{
				if (first->currentBurstStartTimeStamp == second->currentBurstStartTimeStamp)
				{
				if (first->previousBurstTime < second->previousBurstTime)
				{
				return second;
				}
				}
				return first;
				}


				return second;
				*/
				return getMoreWaitingInSameStateProcess(first, second);
			}
			else
			{
				return first;
			}
		}
		return second;
	}

	Process* getNextEventProcess()
	{
		Process* nextProcess = eventQueue[0];
		int i = 0;
		while (i < eventQueueLength)
		{
			if (eventQueue[i]->arrivalTime >= currentTime && eventQueue[i]->currentState == Created)
			{
				return eventQueue[i];
			}

			i++;
		}
		return NULL;
	}

	Process* getNextRunningOrBlockedProcess()
	{
		Process* process = NULL;
		int i = 0;
		while (i < readyQueueLength)
		{
			if (readyQueue[i]->currentState == Running || readyQueue[i]->currentState == Block)
			{
				if (process == NULL)
				{
					process = readyQueue[i];
				}
				else
				{
					process = getNextProcessToRun(readyQueue[i], process);
				}
			}
			i++;
		}
		return process;
	}
	
	void printSummary()
	{
		int i = 0;
		int arrivalTimeOfFirstProcess = 999999;
		int finishingTimeOfLastProcess = 0;
		int totalRunTime = 0;
		double totalCPUUtilization = 0.0;
		double totalTurnAroundTime = 0.0;
		double totalWaitTime = 0.0;
		double averageThroughPut = 0.0;
		while (i < readyQueueLength)
		{
			if (finishingTimeOfLastProcess < readyQueue[i]->finishingTime)
			{
				finishingTimeOfLastProcess = readyQueue[i]->finishingTime;
			}
			if (arrivalTimeOfFirstProcess > readyQueue[i]->arrivalTime)
			{
				arrivalTimeOfFirstProcess = readyQueue[i]->arrivalTime;
			}
			totalCPUUtilization = totalCPUUtilization + readyQueue[i]->totalCPUTime;
			int turnAroundTime = readyQueue[i]->finishingTime - readyQueue[i]->arrivalTime;
			totalTurnAroundTime = totalTurnAroundTime + turnAroundTime;
			totalWaitTime = totalWaitTime + readyQueue[i]->cpuWaitingTime;
			

			cout << setw(4) << setfill('0') <<readyQueue[i]->processId << ":";
			cout << setw(5) << setfill(' ') << readyQueue[i]->arrivalTime;
			cout << setw(5) << setfill(' ') << readyQueue[i]->totalCPUTime;
			cout << setw(5) << setfill(' ') << readyQueue[i]->cpuBurst;
			cout << setw(5) << setfill(' ') << readyQueue[i]->ioBurst;
			cout << setw(2) << setfill(' ') << readyQueue[i]->staticPriority << " |";
			cout << setw(6) << setfill(' ') << readyQueue[i]->finishingTime;
			cout << setw(6) << setfill(' ') << turnAroundTime;
			cout << setw(6) << setfill(' ') << readyQueue[i]->ioTime;
			cout << setw(6) << setfill(' ') << readyQueue[i]->cpuWaitingTime;
			cout << endl;
			i++;
		}
		
		totalRunTime = finishingTimeOfLastProcess - arrivalTimeOfFirstProcess;
		cout << "SUM:";
		cout << " " << fixed << setprecision(2) << finishingTimeOfLastProcess;
		cout << " " << fixed << setprecision(2) << totalCPUUtilization / finishingTimeOfLastProcess * 100;
		cout << " " << fixed << setprecision(2) << (totalIOTime / finishingTimeOfLastProcess) * 100;
		cout << " " << fixed << setprecision(2) << totalTurnAroundTime / readyQueueLength;
		cout << " " << fixed << setprecision(2) << totalWaitTime / readyQueueLength;
		cout << " " << fixed << setprecision(3) << readyQueueLength * 100.0 / finishingTimeOfLastProcess << endl;
	}
};

class RR : public virtual Scheduler
{
public:
	ofstream myfile;
	Transition* getEvent()
	{
		Process* nextEventQueueProcess = getNextEventProcess();
		Process* nextRunningOrBlockedProcess = getNextRunningOrBlockedProcess();

		Process* nextProcessBtwRunBlockAndEventQ = getNextProcessToRun(nextEventQueueProcess, nextRunningOrBlockedProcess);
		Process* nextReadyProcess = getNextReadyProcess();

		Process* nextProcess = NULL;
		// This is because, there is no point in checking ready queue if current process is running.
		if (nextProcessBtwRunBlockAndEventQ != NULL && nextProcessBtwRunBlockAndEventQ->currentState == Running)
		{
			if (nextProcessBtwRunBlockAndEventQ->currentBurstTime > quantum)
			{
				wasProcessPreempted = true;
			}
			nextProcess = nextProcessBtwRunBlockAndEventQ;
		}
		else
		{
			nextProcess = getNextProcessToRun(nextProcessBtwRunBlockAndEventQ, nextReadyProcess);
		}

		if (nextProcess == NULL)
		{
			return NULL;
		}
		return RunProcess(nextProcess);
	}
	void updateQueuePositionForAllElements()
	{
		int i = 0;
		while (i < readyQueue.size())
		{
			if (readyQueue[i]->currentState == Ready)
			{
				readyQueue[i]->currentQueuePosition--;
			}
			i++;
		}
	}
	void putEvent(Transition* transition)
	{
		myfile.open("example.txt", ios::out | ios::app);

		myfile << transition->timeStamp << " " << transition->processId << " " << transition->previousBurstTime << ": ";
		if (transition->oldState == Created)
		{
			myfile << " " << "CREATED -> READY";
		}

		else if (transition->oldState == Block)
		{
			myfile << " " << "BLOCK -> READY";
		}

		else if (transition->oldState == Ready)
		{
			myfile << " READY -> RUNNING cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority;
		}

		else if (transition->oldState == Running && transition->newState == Ready)
		{
			myfile << " RUNNG -> READY cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority;
		}
		else if (transition->oldState == Running && transition->newState == Block)
		{
			myfile << " RUNNG -> BLOCK ib=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else
		{
			myfile << " Done";
		}
		myfile << endl;
		myfile.close();
	}
	Transition* RunProcess(Process* process)
	{
		Transition* trans = new Transition();
		trans->oldState = process->currentState;
		trackQueue.push_back(process->processId);
		if (process->currentState == Ready)
		{
			int burst;
			if (process->currentBurstTime > 0)
			{
				burst = process->currentBurstTime;
				//process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
			}
			else
			{
				burst = getNextRandomNumber(process->cpuBurst);
				//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			}
			process->previousBurstTime = currentTime - process->currentBurstStartTimeStamp;
			process->cpuWaitingTime = process->cpuWaitingTime + process->previousBurstTime;
			process->currentBurstStartTimeStamp = currentTime;

			if (process->cpuTimeRemaining < burst)
			{
				burst = process->cpuTimeRemaining;
			}
			process->currentBurstTime = burst;
			process->currentState = Running;
			process->currentQueuePosition = -1;
			actualReadyQueueLength--;
			//In LIFO we want to reduce the position of all elements
			updateQueuePositionForAllElements();

			isCPUFree = false;
			trans->oldState = Ready;
			return updateTransition(process, trans);
		}

		if (process->currentState == Running)
		{
			if (!wasProcessPreempted)
			{
				if (process->cpuTimeRemaining - process->currentBurstTime > 0)
				{
					int burst = getNextRandomNumber(process->ioBurst);
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
					process->previousBurstTime = process->currentBurstTime;
					process->currentBurstTime = burst;
					process->currentState = Block;

					isCPUFree = true;
					isIOGoingOn = true;
					if (countOfProcessInIO == 0)
					{
						lastProcessInIOTimeStamp = currentTime;
					}
					countOfProcessInIO++;


					trans->oldState = Running;
					return updateTransition(process, trans);
				}
				else
				{
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->previousBurstTime = process->currentBurstTime;
					process->finishingTime = currentTime;
					process->currentState = Done;
					isCPUFree = true;
					return updateTransition(process, trans);
				}
			}
			else
			{
				currentTime = process->currentBurstStartTimeStamp + quantum;
				process->currentBurstStartTimeStamp = currentTime;
				process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
				process->previousBurstTime = quantum;
				process->currentBurstTime = process->currentBurstTime - quantum;
				process->currentState = Ready;
				process->currentQueuePosition = actualReadyQueueLength;
				actualReadyQueueLength++;
				wasProcessPreempted = false;
				isCPUFree = true;
				isIOGoingOn = false;
				trans->oldState = Running;
				return updateTransition(process, trans);
			}
		}

		if (process->currentState == Block)
		{
			currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
			process->currentBurstStartTimeStamp = currentTime;
			//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			process->previousBurstTime = process->currentBurstTime;
			process->ioTime = process->ioTime + process->previousBurstTime;
			process->currentBurstTime = 0;
			process->currentState = Ready;

			countOfProcessInIO--;
			if (countOfProcessInIO == 0)
			{
				isIOGoingOn = false;
				totalIOTime = totalIOTime + currentTime - lastProcessInIOTimeStamp;
			}
			process->currentQueuePosition = actualReadyQueueLength;
			actualReadyQueueLength++;
			trans->oldState = Block;
			return updateTransition(process, trans);
		}

		if (process->currentState == Created)
		{
			currentTime = process->arrivalTime;
			process->currentBurstStartTimeStamp = currentTime;
			process->currentState = Ready;
			process->previousBurstTime = process->currentBurstTime;
			trans->oldState = Created;
			process->currentQueuePosition = actualReadyQueueLength;
			readyQueue.push_back(process);
			readyQueueLength++;
			actualReadyQueueLength++;
			return updateTransition(process, trans);
		}
	}
private:
	Process* getNextReadyProcess()
	{
		Process* process = NULL;
		int i = readyQueueLength - 1;
		if (!isCPUFree)
		{
			return NULL;
		}
		while (i >= 0)
		{
			if (readyQueue[i]->currentState == Ready)
			{
				if (process == NULL)
				{
					process = readyQueue[i];
				}
				//else
				//{
				//	// This is the LIFO part.
				//	int processCBTS = process->currentBurstStartTimeStamp;
				//	int currentProcCBTS = readyQueue[i]->currentBurstStartTimeStamp;
				//	if (processCBTS >= currentProcCBTS)
				//	{
				//		if (processCBTS == currentProcCBTS)
				//		{
				//			if ((processCBTS - process->previousBurstTime < currentProcCBTS - readyQueue[i]->previousBurstTime))
				//			{
				//				process = readyQueue[i];
				//			}
				//		}
				//	}
				//	else
				//		process = readyQueue[i];
				//}
				else
				{
					if (process->currentQueuePosition > readyQueue[i]->currentQueuePosition)
					{
						process = readyQueue[i];
					}
				}
			}
			i--;
		}
		return process;
	}
};

class FIFO : public virtual Scheduler
{
public:
	ofstream myfile;
	Transition* getEvent()
	{
		Process* nextEventQueueProcess = getNextEventProcess();
		Process* nextRunningOrBlockedProcess = getNextRunningOrBlockedProcess();

		Process* nextProcessBtwRunBlockAndEventQ = getNextProcessToRun(nextEventQueueProcess, nextRunningOrBlockedProcess);
		Process* nextReadyProcess = getNextReadyProcess();

		Process* nextProcess = NULL;
		// This is because, there is no point in checking ready queue if current process is running.
		if (nextProcessBtwRunBlockAndEventQ != NULL && nextProcessBtwRunBlockAndEventQ->currentState == Running)
		{
			if (nextProcessBtwRunBlockAndEventQ->currentBurstTime > quantum)
			{
				wasProcessPreempted = true;
			}
			nextProcess = nextProcessBtwRunBlockAndEventQ;
		}
		else
		{
			nextProcess = getNextProcessToRun(nextProcessBtwRunBlockAndEventQ, nextReadyProcess);
		}

		if (nextProcess == NULL)
		{
			return NULL;
		}
		return RunProcess(nextProcess);
	}
	void updateQueuePositionForAllElements()
	{
		int i = 0;
		while (i < readyQueue.size())
		{
			if (readyQueue[i]->currentState == Ready)
			{
				readyQueue[i]->currentQueuePosition--;
			}
			i++;
		}
	}
	void putEvent(Transition* transition)
	{
		myfile.open("example.txt", ios::out | ios::app);

		myfile << transition->timeStamp << " " << transition->processId << " " << transition->previousBurstTime << ": ";
		if (transition->oldState == Created)
		{
			myfile << " " << "CREATED -> READY";
		}

		else if (transition->oldState == Block)
		{
			myfile << " " << "BLOCK -> READY";
		}

		else if (transition->oldState == Ready)
		{
			myfile << " READY -> RUNNING cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority;
		}

		else if (transition->oldState == Running && transition->newState == Ready)
		{
			myfile << " RUNNG -> READY cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else if (transition->oldState == Running && transition->newState == Block)
		{
			myfile << " RUNNG -> BLOCK ib=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else
		{
			myfile << " Done";
		}
		myfile << endl;
		myfile.close();
	}
	Transition* RunProcess(Process* process)
	{
		Transition* trans = new Transition();
		trans->oldState = process->currentState;
		trackQueue.push_back(process->processId);
		if (process->currentState == Ready)
		{
			int burst;
			if (process->currentBurstTime > 0)
			{
				burst = process->currentBurstTime;
				//process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
			}
			else
			{
				burst = getNextRandomNumber(process->cpuBurst);
				//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			}
			process->previousBurstTime = currentTime - process->currentBurstStartTimeStamp;
			process->cpuWaitingTime = process->cpuWaitingTime + process->previousBurstTime;
			process->currentBurstStartTimeStamp = currentTime;

			if (process->cpuTimeRemaining < burst)
			{
				burst = process->cpuTimeRemaining;
			}
			process->currentBurstTime = burst;
			process->currentState = Running;
			process->currentQueuePosition = -1;
			actualReadyQueueLength--;
			//In LIFO we want to reduce the position of all elements
			updateQueuePositionForAllElements();

			isCPUFree = false;
			trans->oldState = Ready;
			return updateTransition(process, trans);
		}

		if (process->currentState == Running)
		{
			if (!wasProcessPreempted)
			{
				if (process->cpuTimeRemaining - process->currentBurstTime > 0)
				{
					int burst = getNextRandomNumber(process->ioBurst);
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
					process->previousBurstTime = process->currentBurstTime;
					process->currentBurstTime = burst;
					process->currentState = Block;

					isCPUFree = true;
					isIOGoingOn = true;
					if (countOfProcessInIO == 0)
					{
						lastProcessInIOTimeStamp = currentTime;
					}
					countOfProcessInIO++;


					trans->oldState = Running;
					return updateTransition(process, trans);
				}
				else
				{
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->previousBurstTime = process->currentBurstTime;
					process->finishingTime = currentTime;
					process->currentState = Done;
					isCPUFree = true;
					return updateTransition(process, trans);
				}
			}
			else
			{
				currentTime = process->currentBurstStartTimeStamp + quantum;
				process->currentBurstStartTimeStamp = currentTime;
				process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
				process->previousBurstTime = quantum;
				process->currentBurstTime = process->currentBurstTime - quantum;
				process->currentState = Ready;
				process->currentQueuePosition = actualReadyQueueLength;
				actualReadyQueueLength++;
				wasProcessPreempted = false;
				isCPUFree = true;
				isIOGoingOn = false;
				trans->oldState = Running;
				return updateTransition(process, trans);
			}
		}

		if (process->currentState == Block)
		{
			currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
			process->currentBurstStartTimeStamp = currentTime;
			//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			process->previousBurstTime = process->currentBurstTime;
			process->ioTime = process->ioTime + process->previousBurstTime;
			process->currentBurstTime = 0;
			process->currentState = Ready;

			countOfProcessInIO--;
			if (countOfProcessInIO == 0)
			{
				isIOGoingOn = false;
				totalIOTime = totalIOTime + currentTime - lastProcessInIOTimeStamp;
			}
			process->currentQueuePosition = actualReadyQueueLength;
			actualReadyQueueLength++;
			trans->oldState = Block;
			return updateTransition(process, trans);
		}

		if (process->currentState == Created)
		{
			currentTime = process->arrivalTime;
			process->currentBurstStartTimeStamp = currentTime;
			process->currentState = Ready;
			process->previousBurstTime = process->currentBurstTime;
			trans->oldState = Created;
			process->currentQueuePosition = actualReadyQueueLength;
			readyQueue.push_back(process);
			readyQueueLength++;
			actualReadyQueueLength++;
			return updateTransition(process, trans);
		}
	}
private:
	Process* getNextReadyProcess()
	{
		Process* process = NULL;
		int i = readyQueueLength - 1;
		if (!isCPUFree)
		{
			return NULL;
		}
		while (i >= 0)
		{
			if (readyQueue[i]->currentState == Ready)
			{
				if (process == NULL)
				{
					process = readyQueue[i];
				}
				//else
				//{
				//	// This is the LIFO part.
				//	int processCBTS = process->currentBurstStartTimeStamp;
				//	int currentProcCBTS = readyQueue[i]->currentBurstStartTimeStamp;
				//	if (processCBTS >= currentProcCBTS)
				//	{
				//		if (processCBTS == currentProcCBTS)
				//		{
				//			if ((processCBTS - process->previousBurstTime < currentProcCBTS - readyQueue[i]->previousBurstTime))
				//			{
				//				process = readyQueue[i];
				//			}
				//		}
				//	}
				//	else
				//		process = readyQueue[i];
				//}
				else
				{
					if (process->currentQueuePosition > readyQueue[i]->currentQueuePosition)
					{
						process = readyQueue[i];
					}
				}
			}
			i--;
		}
		return process;
	}
};

class LIFO : public virtual Scheduler
{
public:
	ofstream myfile;
	Transition* getEvent()
	{
		Process* nextEventQueueProcess = getNextEventProcess();
		Process* nextRunningOrBlockedProcess = getNextRunningOrBlockedProcess();

		Process* nextProcessBtwRunBlockAndEventQ = getNextProcessToRun(nextEventQueueProcess, nextRunningOrBlockedProcess);
		Process* nextReadyProcess = getNextReadyProcess();

		Process* nextProcess = NULL;
		// This is because, there is no point in checking ready queue if current process is running.
		if (nextProcessBtwRunBlockAndEventQ != NULL && nextProcessBtwRunBlockAndEventQ->currentState == Running)
		{
			if (nextProcessBtwRunBlockAndEventQ->currentBurstTime > quantum)
			{
				wasProcessPreempted = true;
			}
			nextProcess = nextProcessBtwRunBlockAndEventQ;
		}
		else
		{
			nextProcess = getNextProcessToRun(nextProcessBtwRunBlockAndEventQ, nextReadyProcess);
		}

		if (nextProcess == NULL)
		{
			return NULL;
		}
		return RunProcess(nextProcess);
	}
	void putEvent(Transition* transition)
	{
		myfile.open("example.txt", ios::out | ios::app);

		myfile << transition->timeStamp << " " << transition->processId << " " << transition->previousBurstTime << ": ";
		if (transition->oldState == Created)
		{
			myfile << " " << "CREATED -> READY";
		}

		else if (transition->oldState == Block)
		{
			myfile << " " << "BLOCK -> READY";
		}

		else if (transition->oldState == Ready)
		{
			myfile << " READY -> RUNNING cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority;
		}

		else if (transition->oldState == Running && transition->newState == Ready)
		{
			myfile << " RUNNG -> READY cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else if (transition->oldState == Running && transition->newState == Block)
		{
			myfile << " RUNNG -> BLOCK ib=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else
		{
			myfile << " Done";
		}
		myfile << endl;
		myfile.close();
	}
	Transition* RunProcess(Process* process)
	{
		Transition* trans = new Transition();
		trans->oldState = process->currentState;
		trackQueue.push_back(process->processId);
		if (process->currentState == Ready)
		{
			int burst;
			if (process->currentBurstTime > 0)
			{
				burst = process->currentBurstTime;
				//process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
			}
			else
			{
				burst = getNextRandomNumber(process->cpuBurst);
				//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			}
			process->previousBurstTime = currentTime - process->currentBurstStartTimeStamp;
			process->cpuWaitingTime = process->cpuWaitingTime + process->previousBurstTime;
			process->currentBurstStartTimeStamp = currentTime;

			if (process->cpuTimeRemaining < burst)
			{
				burst = process->cpuTimeRemaining;
			}
			process->currentBurstTime = burst;
			process->currentState = Running;
			process->currentQueuePosition = -1;
			actualReadyQueueLength--;
			//In LIFO we do not want to reduce the position of all elements
			//updateQueuePositionForAllElements();

			isCPUFree = false;
			trans->oldState = Ready;
			return updateTransition(process, trans);
		}

		if (process->currentState == Running)
		{
			if (!wasProcessPreempted)
			{
				if (process->cpuTimeRemaining - process->currentBurstTime > 0)
				{
					int burst = getNextRandomNumber(process->ioBurst);
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
					process->previousBurstTime = process->currentBurstTime;
					process->currentBurstTime = burst;
					process->currentState = Block;

					isCPUFree = true;
					isIOGoingOn = true;
					if (countOfProcessInIO == 0)
					{
						lastProcessInIOTimeStamp = currentTime;
					}
					countOfProcessInIO++;


					trans->oldState = Running;
					return updateTransition(process, trans);
				}
				else
				{
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->previousBurstTime = process->currentBurstTime;
					process->finishingTime = currentTime;
					process->currentState = Done;
					isCPUFree = true;
					return updateTransition(process, trans);
				}
			}
			else
			{
				currentTime = process->currentBurstStartTimeStamp + quantum;
				process->currentBurstStartTimeStamp = currentTime;
				process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
				process->previousBurstTime = quantum;
				process->currentBurstTime = process->currentBurstTime - quantum;
				process->currentState = Ready;
				process->currentQueuePosition = actualReadyQueueLength;
				actualReadyQueueLength++;
				wasProcessPreempted = false;
				isCPUFree = true;
				isIOGoingOn = false;
				trans->oldState = Running;
				return updateTransition(process, trans);
			}
		}

		if (process->currentState == Block)
		{
			currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
			process->currentBurstStartTimeStamp = currentTime;
			//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			process->previousBurstTime = process->currentBurstTime;
			process->ioTime = process->ioTime + process->previousBurstTime;
			process->currentBurstTime = 0;
			process->currentState = Ready;

			countOfProcessInIO--;
			if (countOfProcessInIO == 0)
			{
				isIOGoingOn = false;
				totalIOTime = totalIOTime + currentTime - lastProcessInIOTimeStamp;
			}
			process->currentQueuePosition = actualReadyQueueLength;
			actualReadyQueueLength++;
			trans->oldState = Block;
			return updateTransition(process, trans);
		}

		if (process->currentState == Created)
		{
			currentTime = process->arrivalTime;
			process->currentBurstStartTimeStamp = currentTime;
			process->currentState = Ready;
			process->previousBurstTime = process->currentBurstTime;
			trans->oldState = Created;
			process->currentQueuePosition = actualReadyQueueLength;
			readyQueue.push_back(process);
			readyQueueLength++;
			actualReadyQueueLength++;
			return updateTransition(process, trans);
		}
	}
private:
	Process* getNextReadyProcess()
	{
		Process* process = NULL;
		int i = readyQueueLength - 1;
		if (!isCPUFree)
		{
			return NULL;
		}
		while (i >= 0)
		{
			if (readyQueue[i]->currentState == Ready)
			{
				if (process == NULL)
				{
					process = readyQueue[i];
				}
				//else
				//{
				//	// This is the LIFO part.
				//	int processCBTS = process->currentBurstStartTimeStamp;
				//	int currentProcCBTS = readyQueue[i]->currentBurstStartTimeStamp;
				//	if (processCBTS >= currentProcCBTS)
				//	{
				//		if (processCBTS == currentProcCBTS)
				//		{
				//			if ((processCBTS - process->previousBurstTime < currentProcCBTS - readyQueue[i]->previousBurstTime))
				//			{
				//				process = readyQueue[i];
				//			}
				//		}
				//	}
				//	else
				//		process = readyQueue[i];
				//}
				else
				{
					if (process->currentQueuePosition < readyQueue[i]->currentQueuePosition)
					{
						process = readyQueue[i];
					}
				}
			}
			i--;
		}
		return process;
	}
};

class SJF : public virtual Scheduler
{
public:
	// Do not bother about readyQueue
	ofstream myfile;
	Transition* getEvent()
	{
		Process* nextEventQueueProcess = getNextEventProcess();
		Process* nextRunningOrBlockedProcess = getNextRunningOrBlockedProcess();

		Process* nextProcessBtwRunBlockAndEventQ = getNextProcessToRun(nextEventQueueProcess, nextRunningOrBlockedProcess);
		Process* nextReadyProcess = getNextReadyProcess();

		Process* nextProcess = NULL;
		// This is because, there is no point in checking ready queue if current process is running.
		if (nextProcessBtwRunBlockAndEventQ != NULL && nextProcessBtwRunBlockAndEventQ->currentState == Running)
		{
			if (nextProcessBtwRunBlockAndEventQ->currentBurstTime > quantum)
			{
				wasProcessPreempted = true;
			}
			nextProcess = nextProcessBtwRunBlockAndEventQ;
		}
		else
		{
			nextProcess = getNextProcessToRun(nextProcessBtwRunBlockAndEventQ, nextReadyProcess);
		}

		if (nextProcess == NULL)
		{
			return NULL;
		}
		return RunProcess(nextProcess);
	}
	void updateQueuePositionForAllElements()
	{
		int i = 0;
		while (i < readyQueue.size())
		{
			if (readyQueue[i]->currentState == Ready)
			{
				readyQueue[i]->currentQueuePosition--;
			}
			i++;
		}
	}
	void putEvent(Transition* transition)
	{
		myfile.open("example.txt", ios::out | ios::app);

		myfile << transition->timeStamp << " " << transition->processId << " " << transition->previousBurstTime << ": ";
		if (transition->oldState == Created)
		{
			myfile << " " << "CREATED -> READY";
		}

		else if (transition->oldState == Block)
		{
			myfile << " " << "BLOCK -> READY";
		}

		else if (transition->oldState == Ready)
		{
			myfile << " READY -> RUNNING cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority;
		}

		else if (transition->oldState == Running && transition->newState == Ready)
		{
			myfile << " RUNNG -> READY cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else if (transition->oldState == Running && transition->newState == Block)
		{
			myfile << " RUNNG -> BLOCK ib=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else
		{
			myfile << " Done";
		}
		myfile << endl;
		myfile.close();
	}
	Transition* RunProcess(Process* process)
	{
		Transition* trans = new Transition();
		trans->oldState = process->currentState;
		trackQueue.push_back(process->processId);
		if (process->currentState == Ready)
		{
			int burst;
			if (process->currentBurstTime > 0)
			{
				burst = process->currentBurstTime;
				//process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
			}
			else
			{
				burst = getNextRandomNumber(process->cpuBurst);
				//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			}
			process->previousBurstTime = currentTime - process->currentBurstStartTimeStamp;
			process->cpuWaitingTime = process->cpuWaitingTime + process->previousBurstTime;
			process->currentBurstStartTimeStamp = currentTime;

			if (process->cpuTimeRemaining < burst)
			{
				burst = process->cpuTimeRemaining;
			}
			process->currentBurstTime = burst;
			process->currentState = Running;
			process->currentQueuePosition = -1;
			actualReadyQueueLength--;
			//In LIFO we do not want to reduce the position of all elements
			updateQueuePositionForAllElements();

			isCPUFree = false;
			trans->oldState = Ready;
			return updateTransition(process, trans);
		}

		if (process->currentState == Running)
		{
			if (!wasProcessPreempted)
			{
				if (process->cpuTimeRemaining - process->currentBurstTime > 0)
				{
					int burst = getNextRandomNumber(process->ioBurst);
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
					process->previousBurstTime = process->currentBurstTime;
					process->currentBurstTime = burst;
					process->currentState = Block;

					isCPUFree = true;
					isIOGoingOn = true;
					if (countOfProcessInIO == 0)
					{
						lastProcessInIOTimeStamp = currentTime;
					}
					countOfProcessInIO++;


					trans->oldState = Running;
					return updateTransition(process, trans);
				}
				else
				{
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->previousBurstTime = process->currentBurstTime;
					process->finishingTime = currentTime;
					process->currentState = Done;
					isCPUFree = true;
					return updateTransition(process, trans);
				}
			}
			else
			{
				currentTime = process->currentBurstStartTimeStamp + quantum;
				process->currentBurstStartTimeStamp = currentTime;
				process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
				process->previousBurstTime = quantum;
				process->currentBurstTime = process->currentBurstTime - quantum;
				process->currentState = Ready;
				process->currentQueuePosition = actualReadyQueueLength;
				actualReadyQueueLength++;
				wasProcessPreempted = false;
				isCPUFree = true;
				isIOGoingOn = false;
				trans->oldState = Running;
				return updateTransition(process, trans);
			}
		}

		if (process->currentState == Block)
		{
			currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
			process->currentBurstStartTimeStamp = currentTime;
			//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			process->previousBurstTime = process->currentBurstTime;
			process->ioTime = process->ioTime + process->previousBurstTime;
			process->currentBurstTime = 0;
			process->currentState = Ready;

			countOfProcessInIO--;
			if (countOfProcessInIO == 0)
			{
				isIOGoingOn = false;
				totalIOTime = totalIOTime + currentTime - lastProcessInIOTimeStamp;
			}
			process->currentQueuePosition = actualReadyQueueLength;
			actualReadyQueueLength++;
			trans->oldState = Block;
			return updateTransition(process, trans);
		}

		if (process->currentState == Created)
		{
			currentTime = process->arrivalTime;
			process->currentBurstStartTimeStamp = currentTime;
			process->currentState = Ready;
			process->previousBurstTime = process->currentBurstTime;
			trans->oldState = Created;
			process->currentQueuePosition = actualReadyQueueLength;
			readyQueue.push_back(process);
			readyQueueLength++;
			actualReadyQueueLength++;
			return updateTransition(process, trans);
		}
	}
private:
	Process* getNextReadyProcess()
	{
		Process* process = NULL;
		int i = readyQueueLength - 1;
		if (!isCPUFree)
		{
			return NULL;
		}
		while (i >= 0)
		{
			if (readyQueue[i]->currentState == Ready)
			{
				if (process == NULL)
				{
					process = readyQueue[i];
				}
				else
				{
					if (readyQueue[i]->cpuTimeRemaining < process->cpuTimeRemaining)
					{
						process = readyQueue[i];
					}
					else if (readyQueue[i]->cpuTimeRemaining == process->cpuTimeRemaining)
					{
						process = getNextProcessToRun(readyQueue[i], process);
					}
				}
			}
			i--;
		}
		return process;
	}
};

class PQ : public virtual Scheduler
{
public:
	vector<Process*> activeQueue;
	vector<Process*> expiredQueue;
	ofstream myfile;
	Transition* getEvent()
	{
		Process* nextEventQueueProcess = getNextEventProcess();
		Process* nextRunningOrBlockedProcess = getNextRunningOrBlockedProcess();

		Process* nextProcessBtwRunBlockAndEventQ = getNextProcessToRun(nextEventQueueProcess, nextRunningOrBlockedProcess);
		Process* nextReadyProcess = getNextReadyProcess();

		Process* nextProcess = NULL;
		// This is because, there is no point in checking ready queue if current process is running.
		if (nextProcessBtwRunBlockAndEventQ != NULL && nextProcessBtwRunBlockAndEventQ->currentState == Running)
		{
			if (nextProcessBtwRunBlockAndEventQ->currentBurstTime > quantum)
			{
				wasProcessPreempted = true;
			}
			nextProcess = nextProcessBtwRunBlockAndEventQ;
		}
		else
		{
			nextProcess = getNextProcessToRun(nextProcessBtwRunBlockAndEventQ, nextReadyProcess);
		}

		if (nextProcess == NULL)
		{
			return NULL;
		}
		return RunProcess(nextProcess);
	}
	void updateQueuePositionForAllElements()
	{
		int i = 0;
		while (i < readyQueue.size())
		{
			if (readyQueue[i]->currentState == Ready)
			{
				readyQueue[i]->currentQueuePosition--;
			}
			i++;
		}
	}
	void putEvent(Transition* transition)
	{
		myfile.open("example.txt", ios::out | ios::app);

		myfile << transition->timeStamp << " " << transition->processId << " " << transition->previousBurstTime << ": ";
		if (transition->oldState == Created)
		{
			myfile << " " << "CREATED -> READY";
		}

		else if (transition->oldState == Block)
		{
			myfile << " " << "BLOCK -> READY";
		}

		else if (transition->oldState == Ready)
		{
			myfile << " READY -> RUNNING cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority;
		}

		else if (transition->oldState == Running && transition->newState == Ready)
		{
			myfile << " RUNNG -> READY cb=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining << " prio=" << transition->priority + 1;
		}
		else if (transition->oldState == Running && transition->newState == Block)
		{
			myfile << " RUNNG -> BLOCK ib=" << transition->currentBurstTime << " rem=" << transition->cpuTimeRemaining;
		}
		else
		{
			myfile << " Done";
		}
		myfile << endl;
		myfile.close();
	}
	Transition* RunProcess(Process* process)
	{
		Transition* trans = new Transition();
		trans->oldState = process->currentState;
		trackQueue.push_back(process->processId);
		if (process->currentState == Ready)
		{
			int burst;
			if (process->currentBurstTime > 0)
			{
				burst = process->currentBurstTime;
				//process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
			}
			else
			{
				burst = getNextRandomNumber(process->cpuBurst);
				//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			}
			process->previousBurstTime = currentTime - process->currentBurstStartTimeStamp;
			process->cpuWaitingTime = process->cpuWaitingTime + process->previousBurstTime;
			process->currentBurstStartTimeStamp = currentTime;

			if (process->cpuTimeRemaining < burst)
			{
				burst = process->cpuTimeRemaining;
			}
			process->currentBurstTime = burst;
			process->currentState = Running;
			process->currentQueuePosition = -1;
			actualReadyQueueLength--;
			//In LIFO we want to reduce the position of all elements
			updateQueuePositionForAllElements();

			isCPUFree = false;
			trans->oldState = Ready;
			return updateTransition(process, trans);
		}

		if (process->currentState == Running)
		{
			if (!wasProcessPreempted)
			{
				if (process->cpuTimeRemaining - process->currentBurstTime > 0)
				{
					int burst = getNextRandomNumber(process->ioBurst);
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
					process->previousBurstTime = process->currentBurstTime;
					process->currentBurstTime = burst;
					process->currentState = Block;
					process->dynamicPriority--;
					isCPUFree = true;
					isIOGoingOn = true;
					if (countOfProcessInIO == 0)
					{
						lastProcessInIOTimeStamp = currentTime;
					}
					countOfProcessInIO++;


					trans->oldState = Running;
					return updateTransition(process, trans);
				}
				else
				{
					currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
					process->currentBurstStartTimeStamp = currentTime;
					process->previousBurstTime = process->currentBurstTime;
					process->finishingTime = currentTime;
					process->currentState = Done;
					isCPUFree = true;
					return updateTransition(process, trans);
				}
			}
			else
			{
				currentTime = process->currentBurstStartTimeStamp + quantum;
				process->currentBurstStartTimeStamp = currentTime;
				process->cpuTimeRemaining = process->cpuTimeRemaining - quantum;
				process->previousBurstTime = quantum;
				process->currentBurstTime = process->currentBurstTime - quantum;
				process->currentState = Ready;
				process->currentQueuePosition = actualReadyQueueLength;
				process->dynamicPriority--;
				actualReadyQueueLength++;
				wasProcessPreempted = false;
				isCPUFree = true;
				isIOGoingOn = false;
				trans->oldState = Running;
				return updateTransition(process, trans);
			}
		}

		if (process->currentState == Block)
		{
			currentTime = process->currentBurstStartTimeStamp + process->currentBurstTime;
			process->currentBurstStartTimeStamp = currentTime;
			//process->cpuTimeRemaining = process->cpuTimeRemaining - process->currentBurstTime;
			process->previousBurstTime = process->currentBurstTime;
			process->ioTime = process->ioTime + process->previousBurstTime;
			process->currentBurstTime = 0;
			process->currentState = Ready;

			countOfProcessInIO--;
			if (countOfProcessInIO == 0)
			{
				isIOGoingOn = false;
				totalIOTime = totalIOTime + currentTime - lastProcessInIOTimeStamp;
			}
			process->currentQueuePosition = actualReadyQueueLength;
			process->dynamicPriority = process->staticPriority - 1;
			actualReadyQueueLength++;
			trans->oldState = Block;
			return updateTransition(process, trans);
		}

		if (process->currentState == Created)
		{
			currentTime = process->arrivalTime;
			process->currentBurstStartTimeStamp = currentTime;
			process->currentState = Ready;
			process->previousBurstTime = process->currentBurstTime;
			trans->oldState = Created;
			process->currentQueuePosition = actualReadyQueueLength;
			readyQueue.push_back(process);
			readyQueueLength++;
			actualReadyQueueLength++;
			return updateTransition(process, trans);
		}
	}
	void switchAndActiveAndExpiredQueue()
	{
		int i = 0;
		while (i < readyQueueLength)
		{
			if (readyQueue[i]->currentState == Ready)
			{
				readyQueue[i]->dynamicPriority = readyQueue[i]->staticPriority - 1;
			}
			i++;
		}
	}
private:
	
	Process* getNextReadyProcess()
	{
		Process* process = NULL;
		bool areThereProcessesInReadyQueue = false;
		int i = readyQueueLength - 1;
		if (!isCPUFree)
		{
			return NULL;
		}

		while (i >= 0)
		{
			if (readyQueue[i]->currentState == Ready)
			{
				areThereProcessesInReadyQueue = true;
				if (readyQueue[i]->dynamicPriority != -1)
				{
					if (process == NULL)
					{
						process = readyQueue[i];
					}
					else
					{
						if (process->dynamicPriority < readyQueue[i]->dynamicPriority)
						{
							process = readyQueue[i];
						}
						if (process->dynamicPriority == readyQueue[i]->dynamicPriority)
						{
							process = getNextProcessToRun(readyQueue[i], process);
						}
					}
				}
			}
			i--;
		}
		if (areThereProcessesInReadyQueue == true && process == NULL)
		{
			switchAndActiveAndExpiredQueue();
			return getNextReadyProcess();
		}
		return process;
	}
};

int convertToInteger(string str)
{
	int i;
	std::stringstream s_str(str);
	s_str >> i;
	return i;
}

int main(int argc, char* argv[])
{
	//string filePath = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment2\lab2_assign\input5)";
	//string randFilePath = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment2\lab2_assign\rfile)";
	if (argc == 4)
	{
		string schedulerSelect = argv[1];
		string filePath = argv[2];
		string randFilePath = argv[3];
		Scheduler* scheduler = NULL;
		switch (schedulerSelect[2])
		{

		case 'F': 
		{
			cout << "FCFS" << endl;
			scheduler = new FIFO();
			break;
		}

		case 'L' : 
		{
			cout << "LCFS" << endl;
			scheduler = new LIFO();
			break;
		}
		case 'R': 
		{
			scheduler = new RR();
			
			scheduler->quantum = convertToInteger(schedulerSelect.substr(3));
			cout << "RR " << scheduler->quantum << endl;
			break;
		}
		case 'S':
		{
			cout << "SJF" << endl;
			scheduler = new SJF();
			break;
		}
		case 'P':
		{
			
			scheduler = new PQ();
			string const quantum;
			scheduler->quantum = convertToInteger(schedulerSelect.substr(3));
			cout << "PRIO " << scheduler->quantum << endl;
			break;
		}
	}
		scheduler->filePath = filePath;
		scheduler->randFilePath = randFilePath;
		scheduler->populateEventQueue();

		while (true)
		{
			Transition* event = scheduler->getEvent();
			if (event == NULL)
			{
				break;
			}
			else
			{
				// scheduler->putEvent(event);
				scheduler->currentTime = event->timeStamp;
			}
		}

		scheduler->printSummary();
	}
	return 0;
}

