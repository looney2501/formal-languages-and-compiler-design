#include <iostream>

using namespace std;

int main() {

	int a;
	int b;
	int r;

	cin >> a;
	cin >> b;


	while (b != 0) {
		r = a % b;
		a = b;
		b = r;
	}

	cout << a;

	return 0;
}