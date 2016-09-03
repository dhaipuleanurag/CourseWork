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
class pageTableEntry
{
public:
	/*unsigned present : 1;
	unsigned modified : 1;
	unsigned referenced : 1;
	unsigned pagedout : 1;*/
	int present;
	int modified;
	int referenced;
	int pagedout;
	int physicalFrame;
};

class instruction
{
public: 
	bool isRead;
	int pageNumber;
};

class statistics
{
public:
	long unmaps;
	long maps;
	long ins;
	long outs;
	long zero;
	
	statistics()
	{
		unmaps = 0;
		maps = 0;
		ins = 0;
		outs = 0;
		zero = 0;
	}
};

class iPager
{
public:
	virtual int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionsBitSet) = 0;
	virtual void update(pageTableEntry* pte, instruction* instruct) = 0;
};

class memoryManager : public iPager
{
public :
	int const virtualMemorySize = 64;
	long instructionFilePointer = 0;
	long currentInstruction = -1;
	long instructionFileTotalLines;
	
	int freeListPointer = 0;
	int physicalFramesSize;
	vector<string> fileData;
	vector<pageTableEntry*> pageTable;
	vector<long> randomValues;
	long totalRandomValues;
	long currentRandomValuePointer = -1;
	string randFilePath;
	int *frameTable = new int[virtualMemorySize];
	statistics* stats = new statistics();

	void readDataFromFile(string fileName)
	{
		string line;
		ifstream file;
		file.open(fileName);
		if (file.is_open())
		{
			while (getline(file, line))
			{
				fileData.push_back(line);
			}
			file.close();
		}
		instructionFileTotalLines = fileData.size();
	}

	instruction* getNextInstruction()
	{
		while (instructionFilePointer < instructionFileTotalLines)
		{
			string instruct = fileData[instructionFilePointer];
			instructionFilePointer++;
			instruction* instructn = new instruction();
			if (instruct[0] == '#')
			{
				continue;
			}
			else 
			{
				if (instruct[0] == '0')
				{
					instructn->isRead = true;
				}
				else
				{
					instructn->isRead = false;
				}
				instructn->pageNumber = convertToInteger(instruct.substr(2));
			}
			currentInstruction++;
			return instructn;
		}
		return NULL;
	}

	bool isPagePresentInPhysicalMemory(int pageNumber)
	{
		if (pageNumber >= virtualMemorySize)
		{
			cout << "Page out of range!" << endl;
			exit(0);
		}
		return pageTable[pageNumber]->present;
	}

	long getNextRandomNumber(long max)
	{
		if (currentRandomValuePointer == -1)
		{
			long randomValue;
			ifstream read(randFilePath);
			read >> totalRandomValues;
			while (read >> randomValue)
			{
				randomValues.push_back(randomValue);
			}
			currentRandomValuePointer = 0;
			return (randomValues[currentRandomValuePointer] % max);
		}

		else
		{
			currentRandomValuePointer++;
			if (currentRandomValuePointer >= totalRandomValues)
			{
				currentRandomValuePointer = 0;
			}
			return randomValues[currentRandomValuePointer] % max;
		}
	}

	int convertToInteger(string str)
	{
		int i;
		std::stringstream s_str(str);
		s_str >> i;
		return i;
	}

	int allocateFrameFromFreeList()
	{
		int physicalFrame;
		if (freeListPointer < physicalFramesSize)
		{
			physicalFrame = freeListPointer;
			freeListPointer++;
		}
		else
		{
			physicalFrame = -1;
		}
		return physicalFrame;
	}
	
	void initializeFrameTableAndPageTable()
	{
		for (int i = 0; i < virtualMemorySize; i++)
		{
			pageTableEntry* pte = new pageTableEntry();
			pte->present = 0;
			pte->modified = 0;
			pte->referenced = 0;
			pte->pagedout = 0;
			pageTable.push_back(pte);
			frameTable[i] = -1;
		}
	}
	
	void pageIn(int page, int frame, bool isOOptionSet)
	{
		stats->ins++;
		if (isOOptionSet)
		{
			cout << currentInstruction << ": IN";
			cout << setw(7) << page;
			cout << setw(4) << frame;
			cout << endl;
		}
	}

	void zero(int frame, bool isOOptionSet)
	{
		stats->zero++;
		if (isOOptionSet)
		{
			cout << currentInstruction << ": ZERO";
			cout << setw(9) << frame;
			cout << endl;
		}
	}

	void map(int page, int frame, bool isOOptionSet)
	{

		stats->maps++;
		int previousPage = frameTable[frame];
		if (previousPage != -1)
		{
			pageTableEntry* previousPte = pageTable[previousPage];
			previousPte->referenced = 0;
			previousPte->modified = 0;
		}
		// page here is the new page coming in.
		pageTableEntry* pte = pageTable[page];
		pte->present = 1;
		pte->physicalFrame = frame;

		frameTable[frame] = page;
		if (isOOptionSet)
		{
			cout << currentInstruction << ": MAP";
			cout << setw(6) << page;
			cout << setw(4) << frame;
			cout << endl;
		}
	}

	void unmap(int frame, bool isOOptionSet)
	{
		stats->unmaps++;
		int page = frameTable[frame];
		pageTableEntry* pte = pageTable[page];
		pte->present = 0;
		if (isOOptionSet)
		{
			cout << currentInstruction;
			cout << ": UNMAP";
			cout << setw(4) << page;
			cout << setw(4) << frame;
			cout << endl;
		}
	}

	void out(int frame, bool isOOptionSet)
	{
		stats->outs++;
		int page = frameTable[frame];
		pageTableEntry* pte = pageTable[page];
		pte->pagedout = 1;
		if (isOOptionSet)
		{
			cout << currentInstruction << ": OUT";
			cout << setw(6) << frameTable[frame];
			cout << setw(4) << frame;
			cout << endl;
		}
	}

	void updatePte(pageTableEntry* pte, instruction* instruct)
	{
		if (instruct->isRead)
		{
			//read operation
			pte->referenced = 1;
		}
		else
		{
			//write operation
			pte->modified = 1;
			pte->referenced = 1;
		}
	}

	void printFrameTable()
	{
		for (int i = 0; i < physicalFramesSize; i++)
		{
			if (frameTable[i] == -1)
			{
				cout << "* ";
			}
			else
			{
				cout << frameTable[i] << " ";
			}
		}
	}

	void printSummary()
	{
		unsigned long totalCycles;
		totalCycles = (stats->maps + stats->unmaps) * 400 + (stats->ins + stats->outs) * 3000 + stats->zero * 150 + currentInstruction + 1;
		cout << "SUM " << currentInstruction + 1;
		cout << " U=" << stats->unmaps;
		cout << " M=" << stats->maps;
		cout << " I=" << stats->ins;
		cout << " O=" << stats->outs;
		cout << " Z=" << stats->zero;
		cout << " ===> " << totalCycles << endl;
	}

	void printPageTable()
	{
		int i = 0;
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			
			if (pte->present)
			{
				cout << i << ":";
				if (pte->referenced == 1)
				{
					cout << "R";
				}
				else
				{
					cout << "-";
				}
				if (pte->modified == 1)
				{
					cout << "M";
				}
				else
				{
					cout << "-";
				}
				if (pte->pagedout == 1)
				{
					cout << "S ";
				}
				else
				{
					cout << "- ";
				}
			}
			else
			{
				if (pte->pagedout == 1)
				{
					cout << "# ";
				}
				else
				{
					cout << "* ";
				}
			}
			i++;
		}
		cout << endl;
	}
};

class NRU : public virtual memoryManager
{
	long pageFaultCount = 0;
	bool shouldClear = false;
public:
	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionsBitSet)
	{
		int frame;
		frame = allocateFrameFromFreeList();
		if (frame == -1)
		{
			pageFaultCount++;
			int pageToBeReplaced = getPageToBeReplacedFromNRU();
			resetReferenceBits();
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionsBitSet);
			if (pte->modified)
			{
				out(frame, isOOptionsBitSet);
			}
			if ((pageFaultCount+1) % 10 == 0)
			{
				shouldClear = true;
			}
		}
		return frame;
	}

	int getCountOfPages(int referenced, int modified)
	{
		int count = 0;
		int i = 0;
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			if (pte->present == 1 && pte->referenced == referenced && pte->modified == modified)
			{
				count++;
			}
			i++;
		}
		return count;
	}

	int getPageToBeReplacedFromNRU()
	{
		int countZeroZero = getCountOfPages(0, 0);
		int countZeroOne = getCountOfPages(0, 1);
		int countOneZero = getCountOfPages(1, 0);
		int countOneOne = getCountOfPages(1, 1);
		int page; 
		if (countZeroZero > 0)
		{
			long rand = getNextRandomNumber(countZeroZero);
			page = getPageToBeReplaced(0, 0, rand);
		}
		else if (countZeroOne > 0)
		{
			long rand = getNextRandomNumber(countZeroOne);
			page = getPageToBeReplaced(0, 1, rand);
		}
		else if (countOneZero > 0)
		{
			long rand = getNextRandomNumber(countOneZero);
			page = getPageToBeReplaced(1, 0, rand);
		}
		else
		{
			long rand = getNextRandomNumber(countOneOne);
			page = getPageToBeReplaced(1, 1, rand);
		}
		return page;
	}

	int getPageToBeReplaced(int referenced, int modified, int position)
	{
		int count = 0;
		int i = 0;
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			if (pte->present == 1 && pte->referenced == referenced && pte->modified == modified)
			{
				if (count == position)
				{
					return i;
				}
				else
				{
					count++;
				}
			}
			i++;
		}
	}

	void resetReferenceBits()
	{
		if (shouldClear)
		{
			int i = 0;
			while (i < virtualMemorySize)
			{
				pageTable[i]->referenced = 0;
				i++;
			}
		}
		shouldClear = false;
	}
	void update(pageTableEntry* pte, instruction* instruct)
	{
		
	}
};

class LRU : public virtual memoryManager
{
	class LinkedListNode
	{
	public:
		int value;
		LinkedListNode* pointer;
	};
public:
	LinkedListNode* headNode = NULL;
	
	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionSet)
	{
		int frame;
		frame = allocateFrameFromFreeList();
		if (frame == -1)
		{
			int pageToBeReplaced = getPageToBeReplacedFromLRU();
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionSet);
			if (pte->modified)
			{
				out(frame, isOOptionSet);

			}
		}
		
		addToLinkedListIfNotPresent(instruct->pageNumber);
	
		return frame;
	}
	
	void update(pageTableEntry* pte, instruction* instruct)
	{
		LinkedListNode* node = headNode;
		LinkedListNode* previous = NULL;
		while (node->pointer != NULL)
		{
			if (node->value == instruct->pageNumber)
			{
				break;
			}
			previous = node;
			node = node->pointer;
		}
		// The page has to be present in the linked list
		if (previous == NULL)
		{
			return;
		}
		previous->pointer = node->pointer;
		node->pointer = headNode;
		headNode = node;
	}

	void addToLinkedListIfNotPresent(int page)
	{
		LinkedListNode* nodeToBeAdded = new LinkedListNode();
		nodeToBeAdded->value = page;
		nodeToBeAdded->pointer = headNode;
		headNode = nodeToBeAdded;
	}

	int getPageToBeReplacedFromLRU()
	{
		LinkedListNode* node = headNode; 
		LinkedListNode* previous = NULL;
		while (node->pointer != NULL)
		{
			previous = node;
			node = node->pointer;
		}
		previous->pointer = NULL;
		return node->value;
	}
};

class FIFO : public virtual memoryManager
{
	class LinkedListNode
	{
	public:
		int value;
		LinkedListNode* pointer;
	};
public:
	LinkedListNode* headNode = NULL;

	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionSet)
	{
		int frame;
		frame = allocateFrameFromFreeList();
		if (frame == -1)
		{
			int pageToBeReplaced = getPageToBeReplacedFromFIFO();
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionSet);
			if (pte->modified)
			{
				out(frame, isOOptionSet);
			}
		}
		AddToLinkedList(instruct->pageNumber);
		return frame;
	}

	void AddToLinkedList(int page)
	{
		LinkedListNode* nodeToBeAdded = new LinkedListNode();
		nodeToBeAdded->value = page;
		nodeToBeAdded->pointer = NULL;

		if (headNode == NULL)
		{
			headNode = nodeToBeAdded;
			return;
		}
		
		LinkedListNode* node = headNode;
		while (node->pointer != NULL)
		{
			node = node->pointer;
		}
		node->pointer = nodeToBeAdded;
	}

	int getPageToBeReplacedFromFIFO()
	{
		LinkedListNode* node = headNode;
		headNode = headNode->pointer;
		return node->value;
	}

	void update(pageTableEntry* pte, instruction* instruct)
	{
	
	}
};

class SecondChance : public virtual memoryManager
{
	class LinkedListNode
	{
	public:
		int value;
		LinkedListNode* pointer;
	};
public:
	LinkedListNode* headNode = NULL;

	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionSet)
	{
		int frame;
		frame = allocateFrameFromFreeList();
		if (frame == -1)
		{
			int pageToBeReplaced = getPageToBeReplacedFromSecondChance();
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionSet);
			if (pte->modified)
			{
				out(frame, isOOptionSet);
			}
		}
		AddToLinkedList(instruct->pageNumber);
		return frame;
	}

	void AddToLinkedList(int page)
	{
		LinkedListNode* nodeToBeAdded = new LinkedListNode();
		nodeToBeAdded->value = page;
		nodeToBeAdded->pointer = NULL;

		if (headNode == NULL)
		{
			headNode = nodeToBeAdded;
			return;
		}

		LinkedListNode* node = headNode;
		while (node->pointer != NULL)
		{
			node = node->pointer;
		}
		node->pointer = nodeToBeAdded;
	}

	int getPageToBeReplacedFromSecondChance()
	{
		LinkedListNode* node = headNode;
		int page = -1;
		while (true)
		{	
			pageTableEntry* pte = pageTable[node->value];
			if (pte->referenced == 1)
			{
				pte->referenced = 0;
				AddToLinkedList(node->value);
				LinkedListNode* nextNode = node->pointer;
				free(node);
				headNode = nextNode;
				node = nextNode;
			}
			else
			{
				headNode = node->pointer;
				page = node->value;
				free(node);
				break;
			}
		}
		return page;
	}

	void update(pageTableEntry* pte, instruction* instruct)
	{

	}
};

class Clock : public virtual memoryManager
{
	class LinkedListNode
	{
	public:
		int value;
		LinkedListNode* pointer;
	};
public:
	LinkedListNode* headNode = NULL;
	LinkedListNode* tailNode = NULL;

	int pointerToPageTable = 0;
	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionSet)
	{
		int frame;

		frame = allocateFrameFromFreeList();

		if (frame == -1)
		{

			int pageToBeReplaced;
			if (basedOnPhysicalFrame)
			{
				pageToBeReplaced = getPageToBeReplacedFromClock();
			}
			else
			{
				pageToBeReplaced = getPageToBeReplacedFromClockBasedOnVirtualFrame();
			}
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionSet);
			if (pte->modified)
			{
				out(frame, isOOptionSet);
			}
		}
		if (basedOnPhysicalFrame)
		{
			AddToLinkedList(instruct->pageNumber);
		}
		return frame;
	}

	void AddToLinkedList(int page)
	{
		
		LinkedListNode* nodeToBeAdded = new LinkedListNode();
		nodeToBeAdded->value = page;
		nodeToBeAdded->pointer = NULL;

		if (headNode == NULL)
		{
			headNode = nodeToBeAdded;
			tailNode = nodeToBeAdded;
			tailNode->pointer = headNode;
			return;
		}

		tailNode->pointer = nodeToBeAdded;
		tailNode = nodeToBeAdded;
		tailNode->pointer = headNode;
	}

	int getPageToBeReplacedFromClock()
	{
		int page = -1;
		while (true)
		{
			pageTableEntry* pte = pageTable[headNode->value];
			if (pte->referenced == 1)
			{
				pte->referenced = 0;
				tailNode = headNode;
				headNode = headNode->pointer;
			}
			else
			{
				LinkedListNode* currentNode = headNode; 
				page = currentNode->value;
				headNode = headNode->pointer;
				free(currentNode);
				tailNode->pointer = headNode;
				break;
			}
		}
		return page;
	}
	
	int getPageToBeReplacedFromClockBasedOnVirtualFrame()
	{
		int page = -1;
		while (true)
		{
			if (pageTable[pointerToPageTable]->present == 1 && pageTable[pointerToPageTable]->referenced == 0)
			{
				page = pointerToPageTable;
				break;
			}
			else
			{
				pageTable[pointerToPageTable]->referenced = 0;
				pointerToPageTable++;
				if (pointerToPageTable == virtualMemorySize)
				{
					pointerToPageTable = 0;
				}
			}
		}
		return page;
	}

	void update(pageTableEntry* pte, instruction* instruct)
	{

	}
};

class Random : public virtual memoryManager
{
	class LinkedListNode
	{
	public:
		int value;
		LinkedListNode* pointer;
	};
public:
	LinkedListNode* headNode = NULL;

	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionSet)
	{
		int frame;
		frame = allocateFrameFromFreeList();
		if (frame == -1)
		{
			int pageToBeReplaced = getPageToBeReplacedFromRandom();
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionSet);
			if (pte->modified)
			{
				out(frame, isOOptionSet);

			}
		}
		return frame;
	}

	void update(pageTableEntry* pte, instruction* instruct)
	{
	}

	int getPageToBeReplacedFromRandom()
	{
		int frame = getNextRandomNumber(physicalFramesSize);
		return frameTable[frame];
	}
};

class Aging : public virtual memoryManager
{
	
public:
	vector<unsigned int> ageBitForPages;
	vector<unsigned int> ageBitForFrames;
	Aging(int physicalFrameSize)
	{
		int i = 0;
		while (i<virtualMemorySize)
		{

			ageBitForPages.push_back(0);
			i++;
		}
		physicalFramesSize = physicalFrameSize;
		i = 0;
		while (i < physicalFramesSize)
		{
			ageBitForFrames.push_back(0);
			i++;
		}
	}

	
	int getFrame(pageTableEntry* pte, instruction* instruct, bool basedOnPhysicalFrame, bool isOOptionSet)
	{
		int frame;
		frame = allocateFrameFromFreeList();
		if (frame == -1)
		{
			int pageToBeReplaced;
			if(basedOnPhysicalFrame)
			{ 
				resetReferenceBitsWithPhysicalFrame();
				pageToBeReplaced = getPageToBeReplacedFromAgingWithPhysicalFrame();
			}
			else
			{
				resetReferenceBits();
				pageToBeReplaced = getPageToBeReplacedFromAging();
			}
			pageTableEntry* pte = pageTable[pageToBeReplaced];
			frame = pte->physicalFrame;
			unmap(frame, isOOptionSet);
			if (pte->modified)
			{
				out(frame, isOOptionSet);
			}
		}
		return frame;
	}

	int getCountOfPages(int referenced, int modified)
	{
		int count = 0;
		int i = 0;
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			if (pte->present == 1 && pte->referenced == referenced && pte->modified == modified)
			{
				count++;
			}
			i++;
		}
		return count;
	}

	void printFrameTable()
	{
		memoryManager::printFrameTable();
		
		int i = 0;
		cout << " ||";
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			if (pte->present == 1)
			{
				cout << " " << i;
				cout << ":";
				cout << hex << ageBitForPages[i];
				cout << dec;
			}
			else
			{
				cout << " *";
			}

			i++;
		}
	}
	
	int getPageToBeReplacedFromAging()
	{
		int page = -1;
		int i = 0;
		bool firstValue = true;
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			if (pte->present == 1)
			{
				if (firstValue)
				{	
					page = i;
					firstValue = false;
				}
				if (ageBitForPages[page] > ageBitForPages[i])
				{
					page = i;
				}
			}
			i++;
		}
		ageBitForPages[page] = 0;
		return page;
	}

	int getPageToBeReplacedFromAgingWithPhysicalFrame()
	{
		int frame = -1;
		int currentPage = -1;
		int i = 0;
		bool firstValue = true;
		while (i < physicalFramesSize)
		{
			currentPage = frameTable[i];
			pageTableEntry* pte = pageTable[currentPage];
			if (pte->present == 1)
			{
				if (firstValue)
				{
					frame = i;
					firstValue = false;
				}
				if (ageBitForFrames[frame] > ageBitForFrames[i])
				{
					frame = i;
				}
			}
			i++;
		}
		ageBitForFrames[frame] = 0;
		return frameTable[frame];
	}
	
	
	void resetReferenceBitsWithPhysicalFrame()
	{
		int i = 0;
		while (i < physicalFramesSize)
		{
			int page = frameTable[i];
			pageTableEntry* pte = pageTable[page];
			ageBitForFrames[i] = ageBitForFrames[i] >> 1;
			if (pte->referenced == 1)
			{
				ageBitForFrames[i] = ageBitForFrames[i] + pow(2, 31);
			}
			pte->referenced = 0;
			i++;
		}
	}

	void resetReferenceBits()
	{
		int i = 0;
		while (i < virtualMemorySize)
		{
			pageTableEntry* pte = pageTable[i];
			ageBitForPages[i] = ageBitForPages[i] >> 1;
			if (pte->referenced == 1)
			{
				ageBitForPages[i] = ageBitForPages[i] + pow(2,31);
			}
			pte->referenced = 0;
			i++;
		}
	}
	void update(pageTableEntry* pte, instruction* instruct)
	{
	}
};


int convertToInt(string str)
{
	int i;
	std::stringstream s_str(str);
	s_str >> i;
	return i;
}

void setAllOptionsTrue(bool* options)
{
	int i = 0;
	while (i < 4)
	{
		options[i] = true;
		i++;
	}
}

void setOptions(string optionString, bool* options)
{
	int length = optionString.length();
	int i = 0;
	while (i < length)
	{
		switch (optionString[i])
		{
		case 'O': options[0] = true;
			break;
		case 'P': options[1] = true;
			break;
		case 'F': options[2] = true;
			break;
		case 'S': options[3] = true;
			break;
		}
		i++;
	}
}

int main(int argc, char  *args[])
{
	//string outF = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment3\lab3_assign.tar\lab3_assign\lab3_assign\outputs\res.txt)";
	//std::ofstream out(outF);
	//std::streambuf *coutbuf = std::cout.rdbuf(); //save old buf
	//std::cout.rdbuf(out.rdbuf());

	//cout << argc << endl;
	//int i = 0;
	//while ( i < argc)
	//{
	//	cout << args[i] << endl;
	//	i++;
	//}
	

	char algorithm = 'l';
	long physicalFrameSize = 32;
	bool* options = new bool[4]{false, false, false, false};
	string randFilePath = args[argc - 1];
	string fileName1 = args[argc -2];
	bool basedOnPhysicalFrame = true;

	bool optionsSet = false;
	int optionalArgumentsCount = argc - 3;

	int i = 1;
	while (i < optionalArgumentsCount + 1)
	{
		string optionalArgument = args[i];
		switch (optionalArgument[1])
		{
		case 'a': algorithm = optionalArgument[2];
			break;
		case 'f': physicalFrameSize = convertToInt(optionalArgument.substr(2));
			break;
		case 'o': 
			if (optionalArgument.size() > 2)
			{
				setOptions(optionalArgument.substr(2), options);
				optionsSet = true;
			}
				  else
				  {
					  setAllOptionsTrue(options);
				  }
				  break;
		default:
			break;
		}
		i++;
	}

	if (!optionsSet)
	{
		setAllOptionsTrue(options);
	}
/*
	cout << "Algo :"<< algorithm << endl;
	cout << physicalFrameSize << endl;
	cout << options[0] << endl;
	cout << options[1] << endl;
	cout << options[2] << endl;
	cout << options[3] << endl;
	*/
	
	//string fileName1 = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment3\lab3_assign.tar\lab3_assign\lab3_assign\in1M2)";
	//string randFilePath = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment3\lab3_assign.tar\lab3_assign\lab3_assign\rfile)";
	
	
	memoryManager* mmu;
	switch (algorithm)
	{
	case 'N': mmu = new NRU();
		basedOnPhysicalFrame = false;
		break;
	case 'l': mmu = new LRU();
		break;
	case 'r': mmu = new Random();
		break;
	case 'f': mmu = new FIFO();
		break;
	case 's': mmu = new SecondChance();
		break;
	case 'c': mmu = new Clock();
		break;
	case 'X': mmu = new Clock();
		basedOnPhysicalFrame = false;
		break;
	case 'a': mmu = new Aging(physicalFrameSize);
		break;
	case 'Y': mmu = new Aging(physicalFrameSize);
		basedOnPhysicalFrame = false;
		break;
	default: mmu = new LRU();
		break;
	}
	//LRU* mmu = new LRU();
	//FIFO* mmu = new FIFO();
	//SecondChance* mmu = new SecondChance();
	//Random* mmu = new Random();
	//Clock* mmu = new Clock();
	//Aging* mmu = new Aging(physicalFrameSize);
	//NRU* mmu = new NRU();
	mmu->initializeFrameTableAndPageTable();
	mmu->physicalFramesSize = physicalFrameSize;
	mmu->randFilePath = randFilePath;
	mmu->readDataFromFile(fileName1);
	instruction* nextInstruction = mmu->getNextInstruction();
	while (nextInstruction != NULL)
	{
		int pageNumber = nextInstruction->pageNumber;
		pageTableEntry* pte = mmu->pageTable[pageNumber];
		if (options[0])
		{
			cout << "==> inst: " << (nextInstruction->isRead ? "0" : "1") << " " << nextInstruction->pageNumber << endl;
		}
		if (!mmu->isPagePresentInPhysicalMemory(pageNumber))
		{
			int frame = mmu->getFrame(pte, nextInstruction, basedOnPhysicalFrame, options[0]);
			if (pte->pagedout)
			{
				mmu->pageIn(pageNumber, frame, options[0]);
			}
			else
			{
				mmu->zero(frame, options[0]);
			}
			mmu->map(pageNumber, frame, options[0]);
		}
		mmu->updatePte(pte, nextInstruction);
		//mmu->printPageTable();
		mmu->update(pte, nextInstruction);
		//mmu->printFrameTable();
		//cout << endl;
		//mmu->printAgePageTable();
		nextInstruction = mmu->getNextInstruction();
	}
	if (options[1])
	{
		mmu->printPageTable();
	}
	if (options[2])
	{
		mmu->printFrameTable();
		cout << endl;
	}
	if (options[3])
	{
		mmu->printSummary();
	}
	return 1;
}