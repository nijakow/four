
list splitson(string s, func predicate)
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

list spliton(string s, func predicate)
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

list split(string s)
{
    return spliton(s, this->isspace);
}
