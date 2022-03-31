
string* splitson(string s, func predicate)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    list lst = {};

    while (pos < len)
    {
        if (predicate(s[pos])) {
            append(lst, substr(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    append(lst, substr(s, start, pos));
    return lst;
}

string* spliton(string s, func predicate)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    list lst = {};

    while (pos < len)
    {
        if (predicate(s[pos])) {
            if (pos - start >= 1)
                append(lst, substr(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    if (pos - start >= 1)
        append(lst, substr(s, start, pos));
    return lst;
}

string* split(string s)
{
    return spliton(s, this->isspace);
}

string* string_split_on_char(string s, char c)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    list lst = {};

    while (pos < len)
    {
        if (s[pos] == c) {
            append(lst, substr(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    append(lst, substr(s, start, pos));
    return lst;
}
