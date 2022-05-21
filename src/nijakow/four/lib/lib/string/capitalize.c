#include "/lib/string/substring.c"
#include "/lib/char/char.c"
#include "/lib/conversion/char_to_string.c"

string String_Capitalize(string s)
{
    return Conversion_CharToString(Char_ToUpperCase(s[0])) + String_IndexBasedSubstring(s, 1, s.length);
}
