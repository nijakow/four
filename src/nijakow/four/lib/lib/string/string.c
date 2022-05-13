inherits "/lib/conversion/char_to_string.c";

use $length;

int String_Length(string s)
{
    return $length(s);
}

string String_Pad(string s, int l)
{
    int i;
    int d;

    d = l - String_Length(s);
    for (i = 0; i < d; i++)
        s = s + " ";
    return s;
}
