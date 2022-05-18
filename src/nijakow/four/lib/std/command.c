#include "/lib/object.c"
#include "/lib/char/char.c"
#include "/lib/list/list.c"
#include "/lib/conversion/char_to_string.c"

private string pattern;
private func   callback;

use $log;

private string* matcher(string value, int a, int b, string current)
{
    if (a >= this.pattern->length) {
        if (b >= value->length)
            return {};
        else
            return nil;
    }
    else if (b >= value.length) return nil;

    if (this.pattern[a] == '%') {
        string* result = matcher(value, a + 1, b + 1, "");
        if (result != nil)
        {
            List_Append(result, current + Conversion_CharToString(value[b]));
            return result;
        }
        return matcher(value, a, b + 1, current + Conversion_CharToString(value[b]));
    }
    else if (Char_IsSpace(this.pattern[a])) {
        if (Char_IsSpace(value[b])) return matcher(value, a, b + 1, "");
        else return matcher(value, a + 1, b, "");
    }
    else if (Char_IsSpace(value[b])) {
        return matcher(value, a, b + 1, "");
    }
    else if (this.pattern[a] == value[b]) {
        return matcher(value, a + 1, b + 1, "");
    }
    else {
        return nil;
    }
}

string* match(string value)
{
    if (this.pattern == nil)
        return nil;
    string* result = matcher(value, 0, 0, "");
    if (result != nil)
    {
        string* rev = {};
        for (int i = result->length - 1; i >= 0; i--)
            List_Append(rev, result[i]);
        result = rev;
    }
    return result;
}

void execute(string* args)
{
    if (this.callback != nil)
        call(this.callback, args);
}

void _init(string pattern, func cb)
{
    this.pattern  = pattern;
    this.callback = cb;
}
