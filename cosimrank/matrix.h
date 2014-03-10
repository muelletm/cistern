#ifndef MATRIX_H
#define MATRIX_H

#include <cstdio>
#include <string>
#include <vector>

#include "edge.h"

using namespace std;

struct Matrix
{
	int rows, columns;
	vector<vector<Edge> > edges;
	vector<string> dictionary;
	vector<char> typeDictionary;
	vector<vector<vector<vector<double> > > > pagerank;

	void AddEdges(FILE * file, bool transpose, int type);

	void AddEdge(int from, int to, double value, int type);

	double GetValue(int from, int to, int type);

	void RowNormalize(int type);

	void ColumNormalize(int type);

	void Transpose();

	void ConstructDictionarys(FILE * file);
};

#endif
