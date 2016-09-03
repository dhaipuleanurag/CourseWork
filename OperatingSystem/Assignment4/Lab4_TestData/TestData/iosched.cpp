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
#include <list>

using namespace std;

class request
{
public:
	int index;
	int arrivalTime;
	int trackNumber;
	bool served;
	int queueNumber;
};

class statistics
{
public:
	long totalTime;
	long totalMovement = 0;
	long totalWaitTime = 0;
	long totalTurnAroundTime = 0;
	long maxWaitTime = 0;
};

class iScheduler
{
public:
	virtual request* getRequest() = 0;
	virtual void update(request* currentRequest) = 0;
};

class ioScheduler : public iScheduler
{
public:
	string inputFile;
	vector<request*> requests;
	request* lastRequest;
	int currentTime = 1;
	statistics* stats = new statistics();
	double totalRequests = 0;

	ioScheduler()
	{
		lastRequest = new request();
		lastRequest->arrivalTime = 0;
		lastRequest->trackNumber = 0;
		lastRequest->index = -1;
	}
	
	void readRequests()
	{
		string line;
		ifstream file;
		int index = 0;
		file.open(inputFile);
		if (file.is_open())
		{
			while (getline(file, line))
			{
				if (line[0] == '#')
				{
					continue;
				}
				else
				{
					request* req = new request();
					int spacePosition = line.find(' ');
					int arrivalTime = convertToInteger(line.substr(0,spacePosition));
					int trackNumer = convertToInteger(line.substr(spacePosition+1));
					req->index = index++;
					req->arrivalTime = arrivalTime;
					req->trackNumber = trackNumer;
					req->served = false;
					req->queueNumber = -1;
					requests.push_back(req);
				}
			}
			file.close();
			totalRequests = requests.size();
		}
	}

	int convertToInteger(string str)
	{
		int i;
		std::stringstream s_str(str);
		s_str >> i;
		return i;
	}

	void update(request* currentRequest)
	{
		int movement = abs(lastRequest->trackNumber - currentRequest->trackNumber);
		int waitTime = abs(currentTime - currentRequest->arrivalTime);
		stats->totalMovement += movement;
		stats->totalWaitTime += waitTime;

		if (waitTime > stats->maxWaitTime)
		{
			stats->maxWaitTime = waitTime;
		}


		currentRequest->served = true;
		currentTime = currentTime + movement;
		int turnAroundTime = abs(currentTime - currentRequest->arrivalTime);
		stats->totalTurnAroundTime += turnAroundTime;
		lastRequest = currentRequest;
		currentRequest = NULL;
		stats->totalTime = currentTime;
	}
	
	void printSummary()
	{
		cout << "SUM: ";
		cout << stats->totalTime << " ";
		cout << stats->totalMovement << " ";
		cout <<fixed<< setprecision(2) << (stats->totalTurnAroundTime / totalRequests) << " ";
		cout << fixed<<setprecision(2) << (stats->totalWaitTime / totalRequests) << " ";
		//cout << (float)(((round)((stats->totalWaitTime / totalRequests)*100))/100) << " ";
		cout << stats->maxWaitTime << " ";
		cout << endl;
	}
};

class FIFO : public virtual ioScheduler
{
public:
	request* getRequest()
	{
		return getRequestBasedOnFIFO();
	}

	request* getRequestBasedOnFIFO()
	{
		int i = 0;
		while (i < totalRequests)
		{
			if (!requests[i]->served)
			{
				return requests[i];
			}
			i++;
		}
		return NULL;
	}

	/*request* getNextEarliestArrival()
	{
		int i = 0;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime >= currentTime && !requests[i]->served)
			{
				return requests[i];
			}
			i++;
		}
		return NULL;
	}*/

	
};


class SSTF : public virtual ioScheduler
{
public:
	request* getRequest()
	{
		request* req;
		req =  getRequestBasedOnSSTF();
		if (req == NULL)
		{
			req = getNextEarliestArrival();
		}
		return req;
	}

	request* getRequestBasedOnSSTF()
	{
		int i = 0;
		request* req = NULL;
		int previousBestSwapTime = 99999;
		while (i < totalRequests)
		{
			int swapTime = abs(lastRequest->trackNumber - requests[i]->trackNumber);
			if (requests[i]->arrivalTime <= currentTime &&  !requests[i]->served)
			{
				if (swapTime < previousBestSwapTime)
				{
					previousBestSwapTime = swapTime;
					req = requests[i];
				}
			}
			i++;
		}
		return req;
	}

	request* getNextEarliestArrival()
	{
		int i = 0;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime >= currentTime && !requests[i]->served)
			{
				return requests[i];
			}
			i++;
		}
		return NULL;
	}


};

class Scan : public virtual ioScheduler
{
public:
	bool isGoingUp = true;
	request* getRequest()
	{
		request* req;
		req = getRequestBasedOnScan();
		if (req == NULL)
		{
			req = getNextEarliestArrival();
		}
		return req;
	}

	request* getRequestBasedOnScan()
	{
		int i = 0;
		request* req = NULL;
		request* higherReq = lastRequest;
		request* lowerReq = lastRequest;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime <= currentTime &&  !requests[i]->served)
			{
				if (requests[i]->trackNumber == lastRequest->trackNumber)
				{
					return requests[i];
				}
				if (requests[i]->trackNumber > lastRequest->trackNumber)
				{
					if (higherReq->trackNumber == lastRequest->trackNumber)
					{
						// Runs only first time.
						higherReq = requests[i];
					}
					else if (abs(requests[i]->trackNumber - lastRequest->trackNumber) < abs(higherReq->trackNumber - lastRequest->trackNumber))
					{
						higherReq = requests[i];
					}
				}
				if (requests[i]->trackNumber < lastRequest->trackNumber)
				{
					if (lowerReq->trackNumber == lastRequest->trackNumber)
					{
						// Runs only first time.
						lowerReq = requests[i];
					}
					else if (abs(requests[i]->trackNumber - lastRequest->trackNumber) < abs(lowerReq->trackNumber - lastRequest->trackNumber))
					{
						lowerReq = requests[i];
					}
				}
			}
			i++;
		}

		if (higherReq->index == lastRequest->index && lowerReq->index == lastRequest->index)
		{
			return NULL;
		}
	
		if(isGoingUp)
		{
			if (higherReq->trackNumber > lastRequest->trackNumber)
			{
				return higherReq;
			}
			isGoingUp = false;
			return lowerReq;
		}
		else
		{
			if (lowerReq->trackNumber < lastRequest->trackNumber)
			{
				return lowerReq;
			}
			isGoingUp = true;
			return higherReq;
		}
		return NULL;
	}

	request* getNextEarliestArrival()
	{
		int i = 0;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime >= currentTime && !requests[i]->served)
			{
				return requests[i];
			}
			i++;
		}
		return NULL;
	}
};

class CScan : public virtual ioScheduler
{
public:
	bool isGoingUp = true;
	request* getRequest()
	{
		request* req;
		req = getRequestBasedOnScan();
		if (req == NULL)
		{
			req = getNextEarliestArrival();
		}
		return req;
	}

	request* getRequestBasedOnScan()
	{
		int i = 0;
		request* req = NULL;
		request* higherReq = lastRequest;
		request* lowestReq = lastRequest;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime <= currentTime &&  !requests[i]->served)
			{
				if (requests[i]->trackNumber == lastRequest->trackNumber)
				{
					return requests[i];
				}
				if (requests[i]->trackNumber > lastRequest->trackNumber)
				{
					if (higherReq->trackNumber == lastRequest->trackNumber)
					{
						// Runs only first time.
						higherReq = requests[i];
					}
					else if (abs(requests[i]->trackNumber - lastRequest->trackNumber) < abs(higherReq->trackNumber - lastRequest->trackNumber))
					{
						higherReq = requests[i];
					}
				}
				if (requests[i]->trackNumber < lastRequest->trackNumber)
				{
					if (lowestReq->trackNumber == lastRequest->trackNumber)
					{
						// Runs only first time.
						lowestReq = requests[i];
					}
					else if (abs(requests[i]->trackNumber - lastRequest->trackNumber) > abs(lowestReq->trackNumber - lastRequest->trackNumber))
					{
						lowestReq = requests[i];
					}
				}
			}
			i++;
		}

		if (higherReq->index == lastRequest->index && lowestReq->index == lastRequest->index)
		{
			return NULL;
		}


		if (higherReq->trackNumber > lastRequest->trackNumber)
		{
			return higherReq;
		}
		else
		{
			return lowestReq;
		}

		return NULL;
	}


	request* getNextEarliestArrival()
	{
		int i = 0;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime >= currentTime && !requests[i]->served)
			{
				return requests[i];
			}
			i++;
		}
		return NULL;
	}
};

class FScan : public virtual ioScheduler
{
public:
	/*vector<request*> firstQueue;
	int firstQueueSize = 0;
	vector<request*> secondQueue;
	int secondQueueSize = 0;
	vector<request*> currentQueue = firstQueue;
	int currentQueueSize = 0;*/
	request* getRequest()
	{
		request* req;
		updateQueues();
		req = getRequestBasedOnScan();
		if (req == NULL)
		{
			req = getNextEarliestArrival();
		}
		return req;
	}
	void updateQueues()
	{
		int i = 0;
		bool doQueueOneExists = false;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime <= currentTime &&  !requests[i]->served)
			{
				if (requests[i]->queueNumber == 1)
				{
					doQueueOneExists = true;
				}
				if (requests[i]->queueNumber == -1)
				{
					requests[i]->queueNumber = 2;
				}
			}
			i++;
		}
		if (!doQueueOneExists)
		{
			i = 0;
			while (i < totalRequests)
			{
				if (requests[i]->arrivalTime <= currentTime &&  !requests[i]->served)
				{
					if (requests[i]->queueNumber == 2)
					{
						requests[i]->queueNumber = 1;
					}
				}
				i++;
			}
		}
	}
	request* getRequestBasedOnScan()
	{
		int i = 0;
		bool isGoingUp = true;
		request* req = NULL;
		request* higherReq = lastRequest;
		request* lowerReq = lastRequest;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime <= currentTime &&  !requests[i]->served && requests[i]->queueNumber == 1)
			{
				if (requests[i]->trackNumber == lastRequest->trackNumber)
				{
					return requests[i];
				}
				if (requests[i]->trackNumber > lastRequest->trackNumber)
				{
					if (higherReq->trackNumber == lastRequest->trackNumber)
					{
						// Runs only first time.
						higherReq = requests[i];
					}
					else if (abs(requests[i]->trackNumber - lastRequest->trackNumber) < abs(higherReq->trackNumber - lastRequest->trackNumber))
					{
						higherReq = requests[i];
					}
				}
				if (requests[i]->trackNumber < lastRequest->trackNumber)
				{
					if (lowerReq->trackNumber == lastRequest->trackNumber)
					{
						// Runs only first time.
						lowerReq = requests[i];
					}
					else if (abs(requests[i]->trackNumber - lastRequest->trackNumber) < abs(lowerReq->trackNumber - lastRequest->trackNumber))
					{
						lowerReq = requests[i];
					}
				}
			}
			i++;
		}

		if (higherReq->index == lastRequest->index && lowerReq->index == lastRequest->index)
		{
			return NULL;
		}

		if (isGoingUp)
		{
			if (higherReq->trackNumber > lastRequest->trackNumber)
			{
				return higherReq;
			}
			isGoingUp = false;
			return lowerReq;
		}
		else
		{
			if (lowerReq->trackNumber < lastRequest->trackNumber)
			{
				return lowerReq;
			}
			isGoingUp = true;
			return higherReq;
		}
		return NULL;
	}

	request* getNextEarliestArrival()
	{
		int i = 0;
		while (i < totalRequests)
		{
			if (requests[i]->arrivalTime >= currentTime && !requests[i]->served)
			{
				return requests[i];
			}
			i++;
		}
		return NULL;
	}
};


int main(int argc, char  *args[])
{
	/*string outF = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment4\TestData\res.txt)";
	std::ofstream out(outF);
	std::streambuf *coutbuf = std::cout.rdbuf();
	std::cout.rdbuf(out.rdbuf());*/



	string schedulerOption = args[1];
	//string inputFile = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment4\TestData\input9)";
	string inputFile = args[2];
	ioScheduler* scheduler = NULL;

	//FIFO* scheduler = new FIFO();
	//SSTF* scheduler = new SSTF();
	//Scan* scheduler = new Scan();
	//CScan* scheduler = new CScan();
	//FScan* scheduler = new FScan();
	switch (schedulerOption[2])
	{
	case 'i': scheduler = new FIFO();
		break;
	case 'j': scheduler = new SSTF();
		break;
	case 's': scheduler = new Scan();
		break;
	case 'c': scheduler = new CScan();
		break;
	case 'f': scheduler = new FScan();
		break;
	default:
		exit(0);
	}
	scheduler->inputFile = inputFile;
	scheduler->readRequests();
	request* currentRequest = scheduler->getRequest();
	while (currentRequest != NULL)
	{
		if (currentRequest->arrivalTime > scheduler->currentTime)
		{
			scheduler->currentTime = currentRequest->arrivalTime;
		}
	//cout << scheduler->currentTime << ":     " << currentRequest->index << " issue " << currentRequest->trackNumber << " " << scheduler->lastRequest->trackNumber << endl;
		// Do all non sense with currentRequest.

		scheduler->update(currentRequest);
		currentRequest = scheduler->getRequest();
	}
	scheduler->printSummary();
	return 1;
}