#include <getopt.h>
#include <math.h>
#include <stdlib.h>
#include <algorithm>
#include <cstdio>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <cstring>

#include "arithmetic.h"
#include "edge.h"
#include "matrix.h"
#include "testset.h"

using namespace std;

void printUsageAndExit(const char* errormsg = NULL)
{
	cerr << "Usage: CSimRank [options] sims test A1 B1 A2 B2 ..." << endl << endl;
	cerr << "Arguments" << endl;
	cerr << "A, B: filenames of adjacency matrices (mtx format)" << endl;
	cerr << "sims: filenames of initial seed dictionary (mtx format)" << endl;
	cerr << "test: filename of testset" << endl;
	cerr << endl << "Options: " << endl;
	cerr << "--iterations: number of SimRank iterations" << endl;
	cerr << "--decay: SimRank damping factor" << endl;
	if (errormsg != NULL)
	{
		cerr << endl << "Error: " << errormsg << endl;
	}

	exit(1);
}

// command line option definitions
static const char *shortOpts = "i:m:";
static const struct option longOpts[] =
{
{ "iterations", required_argument, NULL, 'i' },
{ "decay", required_argument, NULL, 'm' } };

struct AdjMatNames
{
	string A;
	string B;
};

struct GlobalArgs_t
{
	// required positional args
	const char *sims_filename;
	const char *test_filename;

	// optional keyword args
	bool multiEdge;
	unsigned iterations;
	double decay;
	int typeCount;
	const char* equivs_filename;
	vector<AdjMatNames> adjmat_filenames;

} globalArgs;

void getOptions(int argc, char* const argv[])
// get options from command line and store them into the global variable globalArgs
{
	// initialize globalArgs required args with NULL so missing required args can be detected
	globalArgs.sims_filename = NULL;
	globalArgs.test_filename = NULL;

	// initialize globalArgs with program defaults
	globalArgs.multiEdge = false;
	globalArgs.iterations = 5;
	globalArgs.decay = 0.8;
	globalArgs.equivs_filename = NULL;

	// read options from command line to overide the defaults
	int opt = 0;
	int longIndex;
	opt = getopt_long(argc, argv, shortOpts, longOpts, &longIndex);
	while (opt != -1)
	{
		switch (opt)
		{
		case 'i': // iterations
			globalArgs.iterations = atoi(optarg);
			if (globalArgs.iterations <= 0)
			{
				printUsageAndExit("Invalid number of iterations");
			}
			break;

		case 'm': // decay (damping factor)
			globalArgs.decay = strtod(optarg, NULL);
			if (globalArgs.decay <= 0.0)
			{
				printUsageAndExit("Invalid value for decay");
			}
		}

		opt = getopt_long(argc, argv, shortOpts, longOpts, &longIndex);
	}

	int pos = 0;

	// process remaining positional arguments
	AdjMatNames tempAdjMatNames;
	for (int index = optind; index < argc; index++)
	{
		if (pos == 0)
		{
			globalArgs.sims_filename = argv[index];
		}
		else if (pos == 1)
		{
			globalArgs.test_filename = argv[index];
		}
		else if ((pos % 2) == 0)
		{
			tempAdjMatNames.A = argv[index];
		}
		else
		{
			tempAdjMatNames.B = argv[index];
			globalArgs.adjmat_filenames.push_back(tempAdjMatNames);
		}
		pos++;
	}
}

int findId(Matrix & matrix, string request, string request_type)
{
	for (int id = 1; id <= matrix.rows; ++id)
	{
		if ((request_type.find(matrix.typeDictionary[id]) != string::npos) && matrix.dictionary[id] == request)
		{
			return id;
		}
	}

	return 0;
}

void OutputResult(vector<Edge> & similarityEdges, Matrix & outputMatrix, string request_type, string request)
{
	sort(similarityEdges.begin(), similarityEdges.end(), compareDescendingByValue);

	unsigned skipped = 0;
	for (unsigned i = 0; i < 10 + skipped && i < similarityEdges.size(); i++)
	{
		if (similarityEdges[i].to == 0)
		{
			++skipped;
			continue;
		}

		string word = outputMatrix.dictionary[similarityEdges[i].to];
		printf("%d\t%30s\t%9.6f\n", (i - skipped + 1), word.c_str(), similarityEdges[i].value);
	}

	cout << endl;
}

int CalcResult(vector<Edge> & similarityEdges, Matrix & outputMatrix, string request_type, string request, string correct)
{
	sort(similarityEdges.begin(), similarityEdges.end(), compareDescendingByValue);

	printf("%4s\t%30s\t%150s", request_type.c_str(), request.c_str(), correct.c_str());

	unsigned skipped = 0;
	for (unsigned i = 0; i < 1000 && i < similarityEdges.size(); i++)
	{
		if (similarityEdges[i].to == 0)
		{
			++skipped;
			continue;
		}

		string word = outputMatrix.dictionary[similarityEdges[i].to];
		if (correct.find(word) != string::npos)
		{
			cout << word << "\t" << (i - skipped) << "\t" << setprecision(8) << similarityEdges[i].value << endl;
			return i - skipped;
		}
		else if (i < 10)
		{
			cout << word << ", ";
		}
	}

	cout << "...\t" << 1000 << endl;
	return 1000;
}

vector<vector<vector<vector<double> > > > GeneratePageRankMatrix(Matrix & matrix)
{
	// get variables
	int typeCount = globalArgs.typeCount;
	int iterations = globalArgs.iterations;

	// calc personal pagerank for all nodes in outputMatrix
	vector<vector<vector<vector<double> > > > pagerank(typeCount, vector<vector<vector<double> > >(1, vector<vector<double> >(matrix.rows + 1, vector<double>(matrix.rows + 1))));
	vector<vector<double> > pagerank_old(matrix.rows + 1, vector<double>(matrix.rows + 1));
	vector<vector<double> > pagerank_per(matrix.rows + 1, vector<double>(matrix.rows + 1));

	for (int i = 0; i < iterations; i++)
	{
		cout << "Iteration " << i + 1 << "/" << iterations << endl;

#pragma omp parallel for
		for (int out = 1; out <= matrix.rows; out++)
		{
			if (i == 0)
			{
				pagerank_per[out][out] = 1;
				pagerank_old[out][out] = 1;
			}
			else
			{
				for (int t = 0; t < typeCount; t++)
				{
					pagerank[t][0][out] = MatrixVectorMult(matrix.edges[t], pagerank_old[out], globalArgs.decay, pagerank_per[out]);
				}
			}

			// add all pagerank vector for next iteration
			if (typeCount == 1)
			{
				pagerank_old[out] = pagerank[0][0][out];
			}
			else
			{
				pagerank_old[out] = VectorAddVector(pagerank[0][0][out], pagerank[1][0][out], (1.0 / typeCount), (1.0 / typeCount));
				for (int t = 2; t < typeCount; t++)
				{
					ThisAddVector(pagerank_old[out], pagerank[t][0][out], 1, (1.0 / typeCount));
				}
			}
		}
	}

	return pagerank;
}

vector<vector<vector<vector<double> > > > GenerateCoSimRankMatrix(Matrix & matrix)
{
	// get variables
	int typeCount = globalArgs.typeCount;
	int iterations = globalArgs.iterations;

	// calc personal pagerank for all nodes in outputMatrix
	vector<vector<vector<vector<double> > > > pagerank(typeCount, vector<vector<vector<double> > >(iterations, vector<vector<double> >(matrix.rows + 1, vector<double>(matrix.rows + 1))));
	vector<vector<double> > pagerank_old(matrix.rows + 1, vector<double>(matrix.rows + 1));

	for (int i = 0; i < iterations; i++)
	{
		cout << "Iteration " << i + 1 << "/" << iterations << endl;

#pragma omp parallel for
		for (int out = 1; out <= matrix.rows; out++)
		{
			if (i == 0)
			{
				for (int t = 0; t < typeCount; t++)
				{
					pagerank[t][0][out][out] = 1;
				}
			}
			else
			{
				for (int t = 0; t < typeCount; t++)
				{
					pagerank[t][i][out] = MatrixVectorMult(matrix.edges[t], pagerank_old[out], 1.0);

				}
			}

			// add all pagerank vector for next iteration
			if (typeCount == 1)
			{
				pagerank_old[out] = pagerank[0][i][out];
			}
			else
			{
				pagerank_old[out] = VectorAddVector(pagerank[0][i][out], pagerank[1][i][out], (1.0 / typeCount), (1.0 / typeCount));
				for (int t = 2; t < typeCount; t++)
				{
					ThisAddVector(pagerank_old[out], pagerank[t][i][out], 1, (1.0 / typeCount));
				}
			}
		}
	}

	return pagerank;
}

void PrintResult(int acc1, int acc10, int testsetSize, int notFound, double mrr)
{
	cout << "Result:" << endl;
	cout << "P@1: " << acc1 << "/" << (testsetSize - notFound) << "/" << testsetSize << endl;
	cout << "P@10: " << acc10 << "/" << (testsetSize - notFound) << "/" << testsetSize << endl;
	cout << "MRR: " << setprecision(3) << (mrr / testsetSize) << "/" << setprecision(3) << (mrr / (testsetSize - notFound)) << endl << endl;
}

int DoRequest(Matrix & inputMatrix, Matrix & outputMatrix, string request_type, string request, string correct, Matrix & sims)
{
	// get variables
	int typeCount = globalArgs.typeCount;
	double decay = globalArgs.decay;

	// find request id
	int id = findId(inputMatrix, request, request_type);
	char request_typeC = inputMatrix.typeDictionary[id];

	// get cossim

	vector<Edge> similarityEdges(outputMatrix.rows + 1);

#pragma omp parallel for
	for (int out = 1; out <= outputMatrix.rows; out++)
	{
		// continue if type of word not requested
		if (request_typeC != outputMatrix.typeDictionary[out])
			continue;

		// continue if word is itself
		if (&inputMatrix == &outputMatrix && out == id)
			continue;

		double cosim = 0;

		for (unsigned i = 0; i < inputMatrix.pagerank[0].size(); i++)
		{
			for (int t = 0; t < typeCount; t++)
			{
				cosim += GetInnerProduct(inputMatrix.pagerank[t][i][id], outputMatrix.pagerank[t][i][out], sims, pow(decay, i), id);
			}
		}

		cosim /= typeCount;

		similarityEdges[out].to = out;
		similarityEdges[out].value = cosim;
	}

	if (correct == "")
	{
		OutputResult(similarityEdges, outputMatrix, request_type, request);
		return 0;
	}

	return CalcResult(similarityEdges, outputMatrix, request_type, request, correct);
}

void CheckTypesOnSim(Matrix & inputMatrix, Matrix & outputMatrix, vector<vector<double> > & sims)
{
	for (unsigned i = 0; i < sims.size(); i++)
	{
		for (unsigned j = 0; j < sims[i].size(); j++)
		{
			if (inputMatrix.typeDictionary[i] != outputMatrix.typeDictionary[j])
				sims[i][j] = 0;
		}
	}
}

void DoAllRequest(Matrix & inputMatrix, Matrix & outputMatrix, Matrix & sims, vector<string> & testset)
{
	if (&inputMatrix != &outputMatrix)
	{
		cout << "LEXICON EXTRACTION" << endl;
	}
	else
	{
		cout << "SYNONYM EXTRACTION" << endl;
	}

	int acc1 = 0;
	int acc10 = 0;
	double mrr = 0;
	int notFound = 0;

	int testsetSize = testset.size() / 3;
	for (int i = 0; i < 3 * testsetSize; i += 3)
	{
		string request_type = testset[i];
		string input, output;

		input = testset[i + 1];
		output = testset[i + 2];

		int result = DoRequest(inputMatrix, outputMatrix, request_type, input, output, sims);

		if (result == -1)
		{
			++notFound;
			continue;
		}

		mrr += 1.0 / (result + 1.0);

		if (result == 0)
			++acc1;
		if (result < 10)
			++acc10;
	}

	PrintResult(acc1, acc10, testsetSize, notFound, mrr);
}

void DoManualRequests(Matrix & inputMatrix, Matrix & outputMatrix, Matrix & sims)
{
	string request_type, input, output, line;

	cout << endl;
	cout << "Expected input pattern: \"[a|n|v]+ [A|B] [A|B] <word>\"" << endl;
	cout << "[a|n|v] -> part of spech (one or more)" << endl;
	cout << "[A|B]   -> source language (usually A = english, B = german)" << endl;
	cout << "[A|B]   -> target language" << endl;
	cout << "<word>  -> a word in the source language" << endl;
	cout << "Example: \"n A B house\"" << endl;

	while (1)
	{
		cout << "Enter a pattern (EXIT to break):" << endl;
		getline(cin, line);

		if (line == "EXIT")
			break;

		istringstream iss(line);
		vector<string> result;
		do
		{
			string sub;
			iss >> sub;
			if (sub != "")
				result.push_back(sub);
		} while (iss);

		string ab = "AB";
		if (result.size() != 4 || ab.find(result[1]) == string::npos || ab.find(result[2]) == string::npos)
		{
			cout << "Could not process input" << endl;
			continue;
		}

		request_type = result[0];
		input = result[3];

		if (result[1] == "A" && result[2] == "A")
			DoRequest(inputMatrix, inputMatrix, request_type, input, "", sims);
		else if (result[1] == "A" && result[2] == "B")
			DoRequest(inputMatrix, outputMatrix, request_type, input, "", sims);
		else if (result[1] == "B" && result[2] == "A")
			DoRequest(outputMatrix, inputMatrix, request_type, input, "", sims);
		else if (result[1] == "B" && result[2] == "B")
			DoRequest(outputMatrix, outputMatrix, request_type, input, "", sims);
	}
}

void TestPprCos(Matrix & inputMatrix, Matrix & outputMatrix, Matrix & sims, vector<string> & testset)
{
	cout << endl << "PPR+COS (change code to perform COSIMRANK)" << endl;

	inputMatrix.pagerank = GeneratePageRankMatrix(inputMatrix);

	if (&inputMatrix != &outputMatrix)
		outputMatrix.pagerank = GeneratePageRankMatrix(outputMatrix);

	if (testset.size() > 0)
		DoAllRequest(inputMatrix, outputMatrix, sims, testset);
	else
		DoManualRequests(inputMatrix, outputMatrix, sims);
}

void TestVectorRepresentation(Matrix & inputMatrix, Matrix & outputMatrix, Matrix & sims, vector<string> & testset)
{
	cout << endl << "COSIMRANK (change code to perform PPR+cos)" << endl;

	inputMatrix.pagerank = GenerateCoSimRankMatrix(inputMatrix);

	if (&inputMatrix != &outputMatrix)
		outputMatrix.pagerank = GenerateCoSimRankMatrix(outputMatrix);

	if (testset.size() > 0)
		DoAllRequest(inputMatrix, outputMatrix, sims, testset);
	else
		DoManualRequests(inputMatrix, outputMatrix, sims);
}

Matrix createSeedFile()
{
	cout << "Seed file: " << globalArgs.sims_filename << endl;
	FILE * simFile = fopen(globalArgs.sims_filename, "r");
	Matrix sims = Matrix();
	sims.AddEdges(simFile, false, 0);
	fclose(simFile);

	return sims;
}

vector<string> createTestFile(char & from, char & to)
{
	cout << "Testset file: " << globalArgs.test_filename << endl;
	FILE * testsetFile = fopen(globalArgs.test_filename, "r");
	vector<string> testset = ConstructTestset(testsetFile, from, to);
	fclose(testsetFile);

	return testset;
}

Matrix* createMatrixA(bool transpose)
{
	int typeCount = globalArgs.adjmat_filenames.size() - 1;

	Matrix * aMatrix = new Matrix();

	for (int t = 0; t < typeCount; t++)
	{
		cout << "Link file A: " << globalArgs.adjmat_filenames[t].A.c_str() << endl;

		FILE * aFile = fopen(globalArgs.adjmat_filenames[t].A.c_str(), "r");

		if (globalArgs.multiEdge)
		{
			aMatrix->AddEdges(aFile, transpose, t);
		}
		else
		{
			aMatrix->AddEdges(aFile, transpose, 0);
		}

		fclose(aFile);
	}

	cout << "Dictionary A file: " << globalArgs.adjmat_filenames[typeCount].A.c_str() << endl;
	FILE * aDictionaryFile = fopen(globalArgs.adjmat_filenames.back().A.c_str(), "r");
	aMatrix->ConstructDictionarys(aDictionaryFile);
	fclose(aDictionaryFile);

	return aMatrix;
}

Matrix* createMatrixB(bool transpose)
{
	int typeCount = globalArgs.adjmat_filenames.size() - 1;

	Matrix * bMatrix = new Matrix();

	for (int t = 0; t < typeCount; t++)
	{
		cout << "Link file B: " << globalArgs.adjmat_filenames[t].B.c_str() << endl;

		FILE * bFile = fopen(globalArgs.adjmat_filenames[t].B.c_str(), "r");

		if (globalArgs.multiEdge)
		{
			bMatrix->AddEdges(bFile, transpose, t);
		}
		else
		{
			bMatrix->AddEdges(bFile, transpose, 0);
		}

		fclose(bFile);
	}

	cout << "Dictionary B file: " << globalArgs.adjmat_filenames[typeCount].B.c_str() << endl;
	FILE * bDictionaryFile = fopen(globalArgs.adjmat_filenames.back().B.c_str(), "r");
	bMatrix->ConstructDictionarys(bDictionaryFile);
	fclose(bDictionaryFile);

	return bMatrix;
}

int main(int argc, char* const argv[])
{
	getOptions(argc, argv);

	// check if input and output arguments are present
	if (!globalArgs.sims_filename || !globalArgs.test_filename)
	{
		printUsageAndExit();
	}

	Matrix sims = createSeedFile();
	char from, to;
	int typeCount = globalArgs.adjmat_filenames.size() - 1;
	vector<string> testset;

	// if testset not given
	if (strcmp(globalArgs.test_filename, "none") == 0 )
	{
		from = 'A';
		to = 'B';
	}
	else
	{
		testset = createTestFile(from, to);
	}

	Matrix * fromMatrix;
	Matrix * toMatrix;

	if (from == 'A' && to == 'A')
	{
		fromMatrix = createMatrixA(false);
		toMatrix = fromMatrix;
	}
	else if (from == 'B' && to == 'B')
	{
		fromMatrix = createMatrixB(false);
		toMatrix = fromMatrix;
	}
	else if (from == 'A' && to == 'B')
	{
		fromMatrix = createMatrixA(true);
		toMatrix = createMatrixB(true);
	}
	else // if (from == 'B' && to == 'A')
	{
		fromMatrix = createMatrixB(false);
		toMatrix = createMatrixA(false);
	}

	if (!globalArgs.multiEdge)
		globalArgs.typeCount = 1;
	else
		globalArgs.typeCount = typeCount;

	cout << "Iterations: " << globalArgs.iterations << endl;
	cout << "Decay: " << setprecision(3) << globalArgs.decay << endl;
	cout << "Multi edge: " << boolalpha << globalArgs.multiEdge << endl;

	// Choose a test
	TestVectorRepresentation(*fromMatrix, *toMatrix, sims, testset);
	//TestPprCos(*fromMatrix, *toMatrix, sims, testset);

	delete fromMatrix;
	fromMatrix = NULL;

	if (toMatrix != NULL)
		delete toMatrix;

	return 0;
}
