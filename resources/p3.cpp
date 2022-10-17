#include <iostream>

using namespace std;

int main() {

	int n;
	double x;
	double s;
	int i;

	s = 0;

	cin >> n;
	i = 0;

	while (i < n) {
		cin >> x;
		s = s + x;
		i = i + 1;
	}

	cout << s;

	return 0;
}