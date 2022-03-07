
int atoi(string s)
{
	int index = 0;
	int factor = 1;
	int num = 0;
	int len = strlen(s);

	while (index < len && isspace(s[index]))
		index++;
	if (index < len && s[index] == '-') {
		factor = -1;
		index++;
	} else if (index < len && s[index] == '+') {
		index++;
	}
	while (index < len && isdigit(s[index])) {
		num = (num * 10) + (s[index] - '0');
		index++;
	}
	return num * factor;
}
