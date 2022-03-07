
bool isspace(char c)
{
    return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
}

bool isnewline(char c)
{
    return (c == '\n');
}

bool isdigit(char c)
{
	return ((c >= '0') && (c <= '9'));
}

bool isslash(char c)
{
    return (c == '/');
}

bool isupper(char c) { return c >= 'A' && c <= 'Z'; }
bool islower(char c) { return c >= 'a' && c <= 'z'; }

char toupper(char c)
{
    if (islower(c))
        return c - ('a' - 'A');
    return c;
}
