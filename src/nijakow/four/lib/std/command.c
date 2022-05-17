#include "/lib/object.c"
#include "/lib/char/char.c"

private string pattern;
private func   callback;

private bool match_loop(string pattern, string value, int a, int b)
{
    while (true)
    {
        if (a >= this.pattern->length) return true;
        else if (b >= value.length) return false;

        if (Char_IsSpace(this.pattern[a])) {
            if (Char_IsSpace(value[b])) b = b + 1;
            else a = a + 1;
        }
        else if (Char_IsSpace(value[b])) {
            b = b + 1;
        }
        else if (this.pattern[a] == '%') {
            if (match_loop(pattern, value, a + 1, b))
                return true;
            // TODO
            return match_loop(pattern, value, a, b + 1);
        }
        else if (this.pattern[a] == value[b]) {
            a = a + 1;
            b = b + 1;
        }
        else {
            return false;
        }
    }
}

bool match(string value)
{
    if (this.pattern == nil)
        return false;
    return match_loop(this.pattern, value, 0, 0);
}

void execute()
{
    if (this.callback != nil)
        call(this.callback);
}

void _init(string pattern, func cb)
{
    this.pattern  = pattern;
    this.callback = cb;
}
