// TODO: Import String_Length(...)
// TODO: Import Char_IsSpace(...)

use $substr;

string String_IndexBasedSubstring(string str, int i0, int i1)
{
    return $substr(str, i0, i1);
}

string String_LengthBasedSubstring(string str, int index, int length)
{
    return $substr(str, index, index + length);
}

string String_Trim(string s)
{
    int start = 0;
    int end = String_Length(s) - 1;

    while ((start < end) && Char_IsSpace(s[start]))
        start = start + 1;
    while ((start <= end) && Char_IsSpace(s[end]))
        end = end - 1;
    return String_IndexBasedSubstring(s, start, end + 1);
}
