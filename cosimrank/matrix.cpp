#include "matrix.h"

#include <stdlib.h>
#include <algorithm>
#include <iostream>
#include <stdio.h>

#include "mmio.h"

using namespace std;

void Matrix::AddEdges(FILE * file, bool transpose, int type)
{
	rewind(file);
	MM_typecode matcode;

	int from, to, rowsThis, columnsThis, nonZerosThis;
	double value;

	if (mm_read_banner(file, &matcode) != 0)
	{
		printf("Could not process Matrix Market banner for A.\n");
		exit(1);
	}
	if (!(mm_is_matrix(matcode) || mm_is_coordinate(matcode) || mm_is_real(matcode) || mm_is_general(matcode)))
	{
		printf("Wrong Matrix Market type: [%s]\n", mm_typecode_to_str(matcode));
		exit(1);
	}
	if (mm_read_mtx_crd_size(file, &rowsThis, &columnsThis, &nonZerosThis) != 0)
	{
		printf("Could not read Matrix size\n");
		exit(1);
	}
	if ((columns != 0 && columns != columnsThis) || (rows != 0 && rows != rowsThis))
	{
		printf("Matrix size does not match\n");
		exit(1);
	}

	rows = rowsThis;
	columns = columnsThis;

	if (edges.size() < unsigned(type + 1))
	{
		edges.resize(type + 1);
	}

	// reading file line by line
	for (int i = 0; i < nonZerosThis; i++)
	{
		fscanf(file, "%d %d %lg\n", &from, &to, &value);

		if (transpose)
		{
			edges[type].push_back(Edge(to, from, value));
		}
		else
		{
			edges[type].push_back(Edge(from, to, value));
		}
	}
}

void Matrix::AddEdge(int from, int to, double value, int type)
{
	for (unsigned e = 0; e < edges[type].size(); e++)
	{
		if (edges[type][e].from == from && edges[type][e].to == to)
		{
			edges[type][e].value = value;
			return;
		}
	}

	edges[type].push_back(Edge(to, from, value));
}

double Matrix::GetValue(int from, int to, int type)
{
	for (unsigned e = 0; e < edges[type].size(); e++)
	{
		if (edges[type][e].from == from && edges[type][e].to == to)
		{
			return edges[type][e].value;
		}
	}

	return 0.0;
}

void Matrix::RowNormalize(int type)
{
	sort(edges[type].begin(), edges[type].end(), compareAscendingByFrom);

	double sum = edges[type][0].value;
	;
	unsigned startE = 0;

	for (unsigned e = 1; e < edges[type].size(); e++)
	{
		if (edges[type][e - 1].from != edges[type][e].from)
		{
			while (startE < e)
			{
				edges[type][startE].value /= sum;
				++startE;
			}

			startE = e;
			sum = 0;
		}

		sum += edges[type][e].value;
	}
}

void Matrix::ColumNormalize(int type)
{
	sort(edges[type].begin(), edges[type].end(), compareAscendingByTo);

	double sum = edges[type][0].value;
	;
	unsigned startE = 0;

	for (unsigned e = 1; e < edges[type].size(); e++)
	{
		if (edges[type][e - 1].to != edges[type][e].to)
		{
			while (startE < e)
			{
				edges[type][startE].value /= sum;
				++startE;
			}

			startE = e;
			sum = 0;
		}

		sum += edges[type][e].value;
	}
}

void Matrix::Transpose()
{
	for (unsigned t = 0; t < edges.size(); t++)
	{
		for (unsigned e = 0; e < edges[t].size(); e++)
		{
			int to = edges[t][e].to;
			edges[t][e].to = edges[t][e].from;
			edges[t][e].from = to;
		}
	}
}

void Matrix::ConstructDictionarys(FILE * file)
{
	dictionary.resize(rows + 1);
	typeDictionary.resize(rows + 1);

	for (int i = 0; i < rows; i++)
	{
		int id;
		char type;
		string word = "                                                ";

		fscanf(file, "%d %s %c\n", &id, &word[0], &type);

		if (id > rows)
		{
			cout << "Dictionary entry not found: " << id << endl;
			continue;
		}

		dictionary[id] = word.erase(word.find_last_not_of(" "));
		typeDictionary[id] = type;
	}
}
