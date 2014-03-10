#include "edge.h"

Edge::Edge()
{
	from = 0;
	to = 0;
	value = 0;
}

Edge::Edge(int _from, int _to, double _value)
{
	from = _from;
	to = _to;
	value = _value;
}

bool compareDescendingByValue(const Edge & a, const Edge & b)
{
	return a.value > b.value;
}

bool compareAscendingByFrom(const Edge & a, const Edge & b)
{
	return a.from < b.from;
}

bool compareAscendingByTo(const Edge & a, const Edge & b)
{
	return a.to < b.to;
}
