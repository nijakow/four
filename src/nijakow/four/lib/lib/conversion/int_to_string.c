// TODO: Import Char_ToString
// TODO: Import String_Length

string Conversion_IntToStringWithBaseString(int i, string base_string)
{
    int base   = String_Length(base_string);
    string pre = "";
    string s   = "";

    if (i < 0) {
        pre = "-";
        i = -i;
    } else if (i == 0) {
        s = "0";
    }

    while (i != 0)
    {
        s = Char_ToString(base_string[i % base]) + s;
        i = i / 10;
    }

    return pre + s;
}

string Conversion_IntToString(int i)
{
    return Conversion_IntToStringWithBaseString(i, "0123456789");
}

string Conversion_IntToHexString(int i)
{
    return Conversion_IntToStringWithBaseString(i, "0123456789abcdef");
}
