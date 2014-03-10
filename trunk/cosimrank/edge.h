#ifndef EDGE_H
#define EDGE_H

struct Edge
{
	int from;
	int to;
	double value;

	Edge();

	Edge(int _from, int _to, double _value);
};

bool compareDescendingByValue(const Edge & a, const Edge & b);

bool compareAscendingByFrom(const Edge & a, const Edge & b);

bool compareAscendingByTo(const Edge & a, const Edge & b);

#endif
