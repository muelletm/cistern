#include <math.h>
#include <vector>

#include "edge.h"
#include "matrix.h"

double EuklidianNorm(vector<double> & v)
{
	double result = 0;

	for (unsigned int i = 0; i < v.size(); i++)
	{
		result += v[i] * v[i];
	}

	return sqrt(result);
}

double OneNorm(vector<double> & v)
{
	double result = 0;

	for (unsigned int i = 0; i < v.size(); i++)
	{
		result += v[i];
	}

	return result;
}

void ThisAddVector(vector<double> & vector1, vector<double> & vector2, double decay1, double decay2)
{
	for (unsigned j = 0; j < vector1.size(); j++)
	{
		vector1[j] *= decay1;
		vector1[j] += decay2 * vector2[j];
	}
}

vector<double> VectorAddVector(vector<double> & vector1, vector<double> & vector2, double decay1, double decay2)
{
	vector<double> result(vector1.size());

	for (unsigned j = 0; j < vector1.size(); j++)
	{
		result[j] += decay1 * vector1[j];
		result[j] += decay2 * vector2[j];
	}

	return result;
}

vector<double> MatrixVectorMult(vector<Edge> & edges, vector<double> & v, double decay, vector<double> & personalized)
{
	vector<double> result(v.size());

	for (unsigned int i = 0; i < personalized.size(); i++)
	{
		result[i] = (1 - decay) * personalized[i];
	}

	for (unsigned int i = 0; i < edges.size(); i++)
	{
		int from = edges[i].from;
		int to = edges[i].to;
		double value = edges[i].value;

		result[to] += decay * value * v[from];
	}

	return result;
}

vector<double> MatrixVectorMult(vector<Edge> & edges, vector<double> & v, double decay)
{
	vector<double> personalized(v.size());

	return MatrixVectorMult(edges, v, decay, personalized);
}

double GetInnerProduct(vector<double> & v1, vector<double> & v2, Matrix & sims, double decay, int skipId)
{
	double cosine = 0;

	if (v1.size() == v2.size())
	{
		for (unsigned r = 0; r < v1.size(); r++)
		{
			// compare incoming pagerank
			cosine += v1[r] * v2[r] * decay;
		}
	}
	else
	{
		for (unsigned e = 0; e < sims.edges[0].size(); e++)
		{
			int from = sims.edges[0][e].from;
			int to = sims.edges[0][e].to;
			double value = sims.edges[0][e].value;

			// reflect matrix if output is englisch und input is german
			if ((unsigned) sims.rows + 1 == v1.size())
			{
				// continue if word in seed dictionary
				if (from == skipId)
					continue;

				cosine += value * v1[from] * v2[to] * decay;
			}
			else
			{
				// continue if word in seed dictionary
				if (to == skipId)
					continue;

				cosine += value * v1[to] * v2[from] * decay;
			}
		}
	}

	return cosine;
}

double GetCosineSimilarity(vector<double> & v1, vector<double> & v2, Matrix & sims, double decay, int skipId)
{
	double cosine = GetInnerProduct(v1, v2, sims, decay, skipId);

	return cosine / (EuklidianNorm(v1) * EuklidianNorm(v2));
}

vector<vector<double> > MatrixMultMatrix(vector<vector<double> > & denseMatrix, Matrix & sparseMatrix, int type, bool transpose)
{
	vector<vector<double> > result(denseMatrix.size(), vector<double>(denseMatrix[0].size()));

#pragma omp parallel for
	for (unsigned i = 0; i < sparseMatrix.edges[type].size(); i++)
	{
		int from = sparseMatrix.edges[type][i].from;
		int to = sparseMatrix.edges[type][i].to;

		if (transpose)
		{
			from = sparseMatrix.edges[type][i].to;
			to = sparseMatrix.edges[type][i].from;
		}

		double value = sparseMatrix.edges[type][i].value;

		for (unsigned j = 0; j < denseMatrix.size(); ++j)
		{
			result[j][to] += value * denseMatrix[j][from];
		}
	}

	return result;
}

vector<vector<double> > MatrixMultMatrix(Matrix & sparseMatrix, int type, vector<vector<double> > & denseMatrix)
{
	vector<vector<double> > result(denseMatrix.size(), vector<double>(denseMatrix[0].size()));

#pragma omp parallel for
	for (unsigned i = 0; i < sparseMatrix.edges[type].size(); i++)
	{
		int from = sparseMatrix.edges[type][i].from;
		int to = sparseMatrix.edges[type][i].to;
		double value = sparseMatrix.edges[type][i].value;

		for (unsigned j = 0; j < denseMatrix[from].size(); ++j)
		{
			result[from][j] += value * denseMatrix[to][j];
		}
	}

	return result;
}

void MatrixAddMatrix(vector<vector<double> > & matrix1, vector<vector<double> > & matrix2, double decay1, double decay2)
{
#pragma omp parallel for
	for (unsigned i = 0; i < matrix1.size(); i++)
	{
		for (unsigned j = 0; j < matrix1[0].size(); j++)
		{
			matrix1[i][j] *= decay1;
			matrix1[i][j] += decay2 * matrix2[i][j];
		}
	}
}

void MatrixReset(vector<vector<double> > & matrix1, Matrix & sims)
{
	if (matrix1.size() == matrix1[0].size())
	{
		for (unsigned i = 0; i < matrix1.size(); i++)
		{
			matrix1[i][i] = 1;
		}
	}
	else
	{
		for (unsigned e = 0; e < sims.edges[0].size(); e++)
		{
			int from = sims.edges[0][e].from;
			int to = sims.edges[0][e].to;
			double value = sims.edges[0][e].value;

			// reflect matrix if output is englisch und input is german
			if ((unsigned) sims.rows + 1 == matrix1.size())
			{
				matrix1[from][to] = value;
			}
			else
			{
				matrix1[to][from] = value;
			}
		}
	}
}

void MatrixReset(vector<vector<double> > & matrix1)
{
#pragma omp parallel for
	for (unsigned i = 0; i < matrix1.size(); i++)
	{
		for (unsigned j = 0; j < matrix1[0].size(); j++)
		{
			matrix1[i][j] = 0;
		}
	}
}
