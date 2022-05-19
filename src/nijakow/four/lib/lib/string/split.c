inherits "/lib/string/string.c";
inherits "/lib/string/substring.c";
inherits "/lib/list/list.c";

string* String_SplitOnChar(string s, char c)
{
    int pos = 0;
    int start = 0;
    int len = String_Length(s);
    string* lst = {};

    while (pos < len)
    {
        if (s[pos] == c) {
            List_Append(lst, String_IndexBasedSubstring(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    List_Append(lst, String_IndexBasedSubstring(s, start, pos));
    return lst;
}

string* String_SplitOnWhitespace(string s)
{
    return String_SplitOnChar(s, ' ');
}
