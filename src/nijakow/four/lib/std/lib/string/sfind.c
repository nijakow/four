
int sfind(string s, char c)
{
    for (int x = 0; x < strlen(s); x++)
        if (s[x] == c)
            return x;
    return -1;
}

int rsfind(string s, char c)
{
    for (int x = strlen(s) - 1; x >= 0; x--)
        if (s[x] == c)
            return x;
    return -1;
}

int indexof(string s, char c) { return sfind(s, c); }

bool endswith(string s, char c)
{
    if (strlen(s) == 0)
        return false;
    else
        return s[strlen(s) - 1] == c;
}
