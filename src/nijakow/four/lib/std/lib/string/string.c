use $chr;
use $substr;

int strlen(string str)
{
    return $length(str);
}

string substr(string str, int i0, int i1)
{
    return $substr(str, i0, i1);
}

string chr(char c)
{
    return $chr(c);
}

string capitalize(string s)
{
    /* TODO: Skip escape sequences */
    int len = strlen(s);
    if (len == 0)
        return s;
    else
        return chr(toupper(s[0])) + substr(s, 1, len);
}

string strwid(string str, int len)
{
    string ret = "";
    int strl   = length(str);

    for (int x = 0; x < len; x++)
    {
        if (x < strl)
            ret = ret + chr(str[x]);
        else
            ret = ret + " ";
    }
    return ret;
}

string trim(string s)
{
    int start = 0;
    int end = strlen(s) - 1;

    while ((start < end) && isspace(s[start]))
        start = start + 1;
    while ((start <= end) && isspace(s[end]))
        end = end - 1;
    return substr(s, start, end + 1);
}
