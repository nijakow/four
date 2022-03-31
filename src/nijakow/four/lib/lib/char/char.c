
bool Char_IsSpace(char c)
{
    return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
}

bool Char_IsNewline(char c)
{
    return (c == '\n');
}

bool Char_IsDigit(char c)
{
	return ((c >= '0') && (c <= '9'));
}

bool Char_IsUpperCase(char c)
{
    return c >= 'A' && c <= 'Z';
}

bool Char_IsLowerCase(char c)
{
    return c >= 'a' && c <= 'z';
}

char Char_ToUpperCase(char c)
{
    if (Char_IsLowerCase(c))
        return c - ('a' - 'A');
    return c;
}

char Char_ToLowerCase(char c)
{
    if (Char_IsUpperCase(c))
        return c + ('a' - 'A');
    return c;
}
