// TODO: Import String_Length(...)
// TODO: Import String_IndexBasedSubstring(...)
// TODO: Import List_Append(...)

string* String_SplitOnChar(string s, char c)
{
    int pos = 0;
    int start = 0;
    int len = String_Length(s);
    list lst = {};

    while (pos < len)
    {
        if (s[pos] == c) {
            List_Append(lst, String_IndexBasedSubstring(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    List_Append(lst, List_IndexBasedSubstring(s, start, pos));
    return lst;
}
