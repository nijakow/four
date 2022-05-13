inherits "/lib/char/char.c";

int Conversion_StringToInt(string s)
{
    int index = 0;
	int factor = 1;
	int num = 0;
	int len = s.length;

	while (index < len && Char_IsSpace(s[index]))
		index++;
	if (index < len && s[index] == '-') {
		factor = -1;
		index++;
	} else if (index < len && s[index] == '+') {
		index++;
	}
	while (index < len && Char_IsDigit(s[index])) {
		num = (num * 10) + (s[index] - '0');
		index++;
	}
	return num * factor;
}
