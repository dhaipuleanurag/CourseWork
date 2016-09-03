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
struct symbolStruct
{
	int absoluteAddress;
	bool used;
	int moduleNumber;
	string errorMessage;
};

int dataSize = 0;
string fileData;
int position;
int lineNumber;
int offset;
int previousLineLastOffset;

int symbolValue[3];
vector<pair<string, symbolStruct>> symbolTable;
int symbolTableSize = 0;

int currentMemoryAddress;
vector<pair<string, bool>> currentModuleUseList;
vector<pair<string, int>> currentModuleSymbolList;
int currentModuleBaseAddress;
int currentModuleSize;
int currentModuleNumber;
int lastSymbolOffset;
int lastSymbolLine;
vector<string> warningsBeforeSymbolTablePrint;
vector<string> warnings;




void checkAndIncrementNextLine()
{
	if (fileData[position] == '\n')
	{
		lineNumber++;
		previousLineLastOffset = offset;
		offset = 0;
	}
}

void incrementPointers()
{
	position++;
}

bool isLastCharacter()
{
	if (position == dataSize - 1)
	{
		return true;
	}
	else
	{
		return false;
	}
}
int jumpToNextToken()
{
	while (fileData[position] == ' ' || fileData[position] == '\n' || fileData[position] == '\t')
	{
		offset++;
		checkAndIncrementNextLine();
		if (isLastCharacter())
		{

			return -1;
		}
		incrementPointers();
	}
	return 0;
}

int getTokenLength()
{
	int length = 0;
	while (fileData[position] != ' ' && fileData[position] != '\n' && fileData[position] != '\t')
	{
		offset++;
		length++;
		if (isLastCharacter())
		{
			break;
		}
		incrementPointers();

	}
	return length;
}

void readDataFromFile(string fileName)
{
	string line;
	ifstream file;
	file.open(fileName);
	if (file.is_open())
	{
		while (getline(file, line))
		{
			fileData.append(line);
			fileData.append("\n");
		}
		file.close();
	}
	dataSize = fileData.size();
}



void printParseError(int lineNumber, int offset, int errCode)
{
	string errorStrings[7] = {
		"NUM_EXPECTED", // Number expect
		"SYM_EXPECTED", // Symbol Expected
		"ADDR_EXPECTED", // Addressing Expected
		"SYM_TOLONG", // Symbol Name is to long
		"TO_MANY_DEF_IN_MODULE", // > 16
		"TO_MANY_USE_IN_MODULE", // > 16
		"TO_MANY_INSTR" // total num_instr exceeds memory size (512)
	};
	cout << "Parse Error line " << lineNumber << " offset " << offset << ": " << errorStrings[errCode];
	exit(0);
}

string getUseListVaribleReferencedAndUpdateUsed(int relativePositionInList)
{
	currentModuleUseList[relativePositionInList].second = true;
	return currentModuleUseList[relativePositionInList].first;
}

int getAddressOfSymbol(string symbol)
{
	int i = 0;
	while (i < symbolTableSize)
	{
		if (symbolTable[i].first == symbol)
		{
			return symbolTable[i].second.absoluteAddress;
			break;
		}
		i++;
	}
	return -1;
}

void updateErrorMessageInSymbolTable(string symbolName, string errorMessage)
{
	int i = 0;
	while (i < symbolTableSize)
	{
		if (symbolTable[i].first == symbolName)
		{
			symbolTable[i].second.errorMessage = errorMessage;
			break;
		}
	}
}

void updateModuleNumberInSymbolTable(string symbol, int moduleNumber)
{
	int i = 0;
	while (i < symbolTableSize)
	{
		if (symbolTable[i].first == symbol)
		{
			symbolTable[i].second.moduleNumber = moduleNumber;
		}
		i++;
	}
}

void updateUsedInSymbolTable(string symbol)
{
	int i = 0;
	while (i < symbolTableSize)
	{
		if (symbolTable[i].first == symbol)
		{
			symbolTable[i].second.used = true;
		}
		i++;
	}
}

void updateValueInSymbolTable(string symbolName, int value)
{
	int i = 0;
	while (i < symbolTableSize)
	{
		if (symbolTable[i].first == symbolName)
		{
			symbolTable[i].second.absoluteAddress = value;
		}
		i++;
	}
}

void setLastSymbolPointers()
{
	lastSymbolLine = lineNumber;
	lastSymbolOffset = offset + 1;
}

void initialize(string fileName)
{
	position = 0;
	lineNumber = 1;
	offset = 0;
	lastSymbolLine = 0;
	lastSymbolOffset = 0;
	currentModuleBaseAddress = 0;
	currentModuleSize = 0;
	currentMemoryAddress = 0;
	currentModuleNumber = 0;
	previousLineLastOffset = 0;
	fileData = "";
	readDataFromFile(fileName);
}


int readIntegerToken()
{
	if (jumpToNextToken() == -1)
	{
		printParseError(lineNumber - 1, previousLineLastOffset, 0);
	}
	setLastSymbolPointers();
	int tokenLength = getTokenLength();
	int integerToken = -1;
	bool errorExist = false;
	integerToken = atoi((fileData.substr(position - tokenLength, tokenLength).c_str()));
	if (integerToken == 0 || integerToken >= pow(10, (tokenLength - 1)))
	{
		if (integerToken == 0 && tokenLength == 1)
		{
			if ((int)fileData[position - 1] != 48)
			{
				errorExist = true;
			}
		}
	}
	if (errorExist)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 0);
	}
	return integerToken;
}

int readAddressToken()
{
	if (jumpToNextToken() == -1)
	{
		printParseError(lineNumber - 1, previousLineLastOffset, 2);
	}
	setLastSymbolPointers();
	int tokenLength = getTokenLength();
	int integerToken = -1;
	bool errorExist = false;
	integerToken = atoi(fileData.substr(position - tokenLength, tokenLength).c_str());
	if (integerToken == 0 || integerToken >= pow(10, (tokenLength - 1)))
	{
		if (integerToken == 0 && tokenLength == 1)
		{
			if ((int)fileData[position - 1] != 48)
			{
				errorExist = true;
			}
		}
	}
	if (errorExist)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 0);
	}
	return integerToken;
}

string readStringToken(bool isInstruction)
{
	if (jumpToNextToken() == -1)
	{
		if (isInstruction)
		{
			printParseError(lineNumber - 1, previousLineLastOffset, 2);
		}
		else
		{
			printParseError(lineNumber - 1, previousLineLastOffset, 1);
		}
	}
	setLastSymbolPointers();
	int tokenLength = getTokenLength();
	if (isInstruction && tokenLength != 1)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 2);
	}
	string stringToken = fileData.substr(position - tokenLength, tokenLength);
	if ((int)stringToken[0] > 47 && (int)stringToken[0] < 58)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 1);
	}
	if (tokenLength > 16)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 3);
	}
	return stringToken;
}

string convertToString(int number)
{
	ostringstream convert;
	convert << number;
	return convert.str();
}

int convertToInteger(string a)
{
	return 0;
}


void printWarningsForUseListNotUsed()
{
	int i = 0;
	while (i < currentModuleUseList.size())
	{
		if (currentModuleUseList[i].second == false)
		{
			string warningMessage = "Warning: Module ";

			warningMessage += convertToString(currentModuleNumber + 1);
			warningMessage += ": " + currentModuleUseList[i].first + " appeared in the uselist but was not actually used\n";
			cout << warningMessage;
		}
		i++;
	}
}

void updateWarningsForSymbolsDefinedButNotUsed()
{
	int i = 0;
	while (i < symbolTableSize)
	{
		if (symbolTable[i].second.used == false)
		{
			string warningMessage = "Warning: Module ";
			warningMessage += convertToString(symbolTable[i].second.moduleNumber + 1);
			warningMessage += ": " + symbolTable[i].first + " was defined but never used\n";
			warnings.push_back(warningMessage);
		}
		i++;
	}
}

void printWarnings()
{
	int i = 0;
	cout << endl;
	while (i < warnings.size())
	{
		cout << warnings[i];
		i++;
	}
}

void printWarningsBeforeSymbolTable()
{
	int i = 0;
	while (i < warningsBeforeSymbolTablePrint.size())
	{
		cout << warningsBeforeSymbolTablePrint[i];
		i++;
	}
}

void checkWarningsForExceedingAddressLimitOfSymbols()
{
	int i = 0;
	while (i < currentModuleSymbolList.size())
	{
		if (currentModuleSymbolList[i].second > currentModuleSize)
		{
			updateValueInSymbolTable(currentModuleSymbolList[i].first, currentModuleBaseAddress);
			string warningMessage = "Warning: Module ";
			warningMessage += convertToString(currentModuleNumber + 1);
			warningMessage += ": ";
			warningMessage += currentModuleSymbolList[i].first;
			warningMessage += " to big ";
			warningMessage += convertToString(currentModuleSymbolList[i].second);
			warningMessage += " (max=";
			warningMessage += convertToString(currentModuleSize - 1);
			warningMessage += ") assume zero relative\n";
			warningsBeforeSymbolTablePrint.push_back(warningMessage);
		}
		i++;
	}
}

void endModuleOperation()
{
	checkWarningsForExceedingAddressLimitOfSymbols();
	printWarningsForUseListNotUsed();
	currentModuleBaseAddress += currentModuleSize;
	currentModuleSize = 0;
	currentModuleNumber++;
	currentModuleSymbolList.clear();
	currentModuleUseList.clear();
}

void readDefList(bool isFirstPass)
{
	int defCount = readIntegerToken();
	if (defCount == -1)
	{
		return;
	}
	if (defCount > 16)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 4);
	}
	int i = 0;
	while (i < defCount)
	{
		string symbolName = readStringToken(false);
		int relativeSymbolAddress = readIntegerToken();
		if (isFirstPass)
		{
			symbolStruct entry;
			int AddressIfAlreadyExists = getAddressOfSymbol(symbolName);
			if (getAddressOfSymbol(symbolName) == -1)
			{
				int absoluteAddress = currentModuleBaseAddress + relativeSymbolAddress;
				entry = { absoluteAddress, false, currentModuleNumber, "" };
				pair<string, symbolStruct> symbolEntry = make_pair(symbolName, entry);
				symbolTable.push_back(symbolEntry);
				currentModuleSymbolList.push_back(make_pair(symbolName, relativeSymbolAddress));
				symbolTableSize++;
			}
			else
			{
				updateErrorMessageInSymbolTable(symbolName, "Error: This variable is multiple times defined; first value used");
			}
		}
		else
		{
			updateModuleNumberInSymbolTable(symbolName, currentModuleNumber);
		}
		i++;
	}
}

void generateMemoryMap(string instructType, int instruction)
{
	int opcode;
	int operand;
	char instructionType = instructType[0];

	if (instructionType == 'I')
	{
		if (instruction > 9999)
		{
			operand = 9999;
			cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
			cout << setfill('0') << setw(4) << operand;
			cout << " Error: Illegal immediate value; treated as 9999";
			cout << endl;
			return;
		}
		operand = instruction;
		cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
		cout << setfill('0') << setw(4) << operand << endl;
	}
	else
	{
		opcode = instruction / 1000;
		if (instructionType == 'A')
		{
			bool absoluteAddressExceedsError = false;
			operand = instruction - opcode * 1000;
			if (operand > 511)
			{
				absoluteAddressExceedsError = true;
				operand = 0;
			}
			cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
			cout << setfill('0') << setw(4) << operand + opcode * 1000;
			if (absoluteAddressExceedsError)
			{
				cout << " Error: Absolute address exceeds machine size; zero used";
			}
			cout << endl;
		}
		else if (instructionType == 'R')
		{
			if (instruction > 9999)
			{
				operand = 9999;
				cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
				cout << setfill('0') << setw(4) << operand;
				cout << " Error: Illegal opcode; treated as 9999";
				cout << endl;
				return;
			}
			bool relativeAddressExceedsError = false;
			operand = (instruction - opcode * 1000);
			if (!(operand < currentModuleSize))
			{
				operand = 0;
				relativeAddressExceedsError = true;
			}
			operand = operand + currentModuleBaseAddress;

			cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
			cout << setfill('0') << setw(4) << operand + opcode * 1000;
			if (relativeAddressExceedsError)
			{
				cout << " Error: Relative address exceeds module size; zero used";
			}
			cout << endl;
		}
		else if (instructionType == 'E')
		{
			bool useListNotDefinedError = false;
			int relativePositionInList = (instruction - opcode * 1000);
			if (relativePositionInList < currentModuleUseList.size())
			{
				string useListVariableReferenced = getUseListVaribleReferencedAndUpdateUsed(relativePositionInList);
				operand = getAddressOfSymbol(useListVariableReferenced);
				if (operand == -1)
				{
					operand = 0;
					useListNotDefinedError = true;
				}
				cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
				cout << setfill('0') << setw(4) << operand + opcode * 1000;
				if (useListNotDefinedError)
				{
					cout << " " << "Error: " + useListVariableReferenced + " is not defined; zero used";
				}
				cout << endl;
			}
			else
			{
				operand = instruction;
				cout << setfill('0') << setw(3) << currentMemoryAddress << ": ";
				cout << setfill('0') << setw(4) << operand;
				cout << " Error: External address exceeds length of uselist; treated as immediate";
				cout << endl;
			}
		}
	}
}

void readUseList(bool isFirstPass)
{
	int useCount = readIntegerToken();
	if (useCount > 16)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 5);
	}
	int i = 0;
	while (i < useCount)
	{
		string name = readStringToken(false);
		if (!isFirstPass)
		{
			updateUsedInSymbolTable(name);
			pair<string, bool> useListEntry = make_pair(name, false);
			currentModuleUseList.push_back(useListEntry);
		}
		i++;
	}
}

void readInstList(bool isFirstPass)
{
	int codeCount = readIntegerToken();
	if (codeCount + currentModuleBaseAddress > 512)
	{
		printParseError(lastSymbolLine, lastSymbolOffset, 6);
	}
	currentModuleSize = codeCount;
	int i = 0;
	while (i < codeCount)
	{
		string instructionType = readStringToken(true);
		int instruction = readAddressToken();
		if (!isFirstPass)
		{
			generateMemoryMap(instructionType, instruction);
			currentMemoryAddress++;
		}
		i++;
	}
	endModuleOperation();
}

bool areThereMoreCharacters()
{
	int pos = position;
	while (fileData[pos] == ' ' || fileData[pos] == '\n' || fileData[pos] == '\t')
	{
		if (pos == dataSize - 1)
		{
			return false;
		}
		pos++;


	}
	return true;
}

bool createModule(bool isFirstPass)
{
	readDefList(isFirstPass);
	readUseList(isFirstPass);
	readInstList(isFirstPass);

	return areThereMoreCharacters();
}


void printSymbolTable()
{
	cout << "Symbol Table" << endl;
	int i = 0;
	while (i < symbolTableSize)
	{
		cout << symbolTable[i].first << "=" << symbolTable[i].second.absoluteAddress << " " << symbolTable[i].second.errorMessage << endl;
		i++;
	}
}

int main(int argc, char  *fileName[])
{

	//string fileName1 = R"(D:\Academics\Courses\Operating Systems\Assignments\Assignment1\Assignment1_lab1_input_samples\labsamples\input-10)";
	initialize(fileName[1]);
	// Open file and start reading data.
	// Do all necessary initializing.
	//initialize(fileName[1]);
	while (true)
	{
		// Start parsing. Call functions for symbol data, use list data and instruction data. 
		if (!createModule(true))
		{
			break;
		}
	}

	printWarningsBeforeSymbolTable();
	printSymbolTable();

	// All initializations done again including opening file and start reading data
	initialize(fileName[1]);
	cout << endl << "Memory Map" << endl;
	while (true)
	{
		if (!createModule(false))
		{
			break;
		}
	}
	updateWarningsForSymbolsDefinedButNotUsed();
	printWarnings();
	return 0;
}