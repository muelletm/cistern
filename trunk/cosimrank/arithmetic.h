#ifndef ARITHMETIC_H
#define ARITHMETIC_H

#include <vector>

#include "edge.h"

using namespace std;

struct Matrix;

double EuklidianNorm(vector<double> & v);

double OneNorm(vector<double> & v);

void ThisAddVector(vector<double> & vector1, vector<double> & vector2, double decay1, double decay2);

vector<double> VectorAddVector(vector<double> & vector1, vector<double> & vector2, double decay1, double decay2);

vector<double> MatrixVectorMult(vector<Edge> & edges, vector<double> & v, double decay, vector<double> & personalized);

vector<double> MatrixVectorMult(vector<Edge> & edges, vector<double> & v, double decay);

double GetInnerProduct(vector<double> & v1, vector<double> & v2, Matrix & sims, double decay, int skipId);

double GetCosineSimilarity(vector<double> & v1, vector<double> & v2, Matrix & sims, double decay, int skipId);

vector<vector<double> > MatrixMultMatrix(vector<vector<double> > & denseMatrix, Matrix & sparseMatrix, int type, bool transpose);

vector<vector<double> > MatrixMultMatrix(Matrix & sparseMatrix, int type, vector<vector<double> > & denseMatrix);

void MatrixAddMatrix(vector<vector<double> > & matrix1, vector<vector<double> > & matrix2, double decay1, double decay2);

void MatrixReset(vector<vector<double> > & matrix1, Matrix & sims);

void MatrixReset(vector<vector<double> > & matrix1);

#endif
