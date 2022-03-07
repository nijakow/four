
int atoi(string s)
{
	int index = 0;
	int factor = 1;
	int num = 0;

	while (isspace(s[index]))
		index++;
	if (s[index] == '-') {
		factor = -1;
		index++;
	} else if (s[index] == '+') {
		index++;
	}
	while (isdigit(s[index])) {
		num = (num * 10) + (s[index] - '0');
		index++;
	}
	return num * factor;
}
