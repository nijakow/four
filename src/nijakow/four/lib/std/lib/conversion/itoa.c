
string itoab(int i, int base)
{
    string pre = "";
    string s = "";

    if (i < 0) {
        pre = "-";
        i = -i;
    } else if (i == 0) {
        s = "0";
    }

    while (i != 0)
    {
        int c = i % base;
        i = i / 10;
        if (c < 10) s = chr(c + '0') + s;
        else        s = chr((c - 10) + 'a') + s;
    }

    return pre + s;
}

string itoa(int i) { return itoab(i, 10); }
string itoax(int i) { return itoab(i, 16); }
