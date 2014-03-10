#include "testset.h"

#include <cstdio>
#include <string>
#include <vector>

using namespace std;

vector<string> ConstructTestset(FILE * file, char & from, char & to)
{
	int length;
	fscanf(file, "%d\t%c\t%c\n", &length, &from, &to);

	vector<string> testset = vector<string>(length * 3);

	for (int i = 0; i < length; i++)
	{
		string pos = "                                                  ";
		string request = "                                                  ";
		string correct = "                                                                                                                                                      ";

		fscanf(file, "%s\t%s\t%s\n", &pos[0], &request[0], &correct[0]);

		testset[(3 * i)] = pos.erase(pos.find_last_not_of(" "));
		testset[(3 * i) + 1] = request.erase(request.find_last_not_of(" "));
		testset[(3 * i) + 2] = correct.erase(correct.find_last_not_of(" "));
	}

	return testset;
}
