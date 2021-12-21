use $log;
use $the_object;
use $call;
use $listinsert;
use $listremove;
use $listlen;
use $strlen;
use $substr;


object the(string id)
{
    return $the_object(id);
}

any new(string blueprint, ...)
{
    return the(blueprint)->clone(...);
}

any call(any f, ...)
{
    return $call(f, ...);
}

void log(...)
{
    $log(...);
}

int rand()
{
    return $random();
}

int length(any lst)
{
    return $listlen(lst);
}

void insert(any lst, int index, any value)
{
    $listinsert(lst, index, value);
}

void append(any lst, any value)
{
    insert(lst, length(lst), value);
}

any remove(any lst, int index)
{
    return $listremove(lst, index);
}

int strlen(string str)
{
    return $strlen(str);
}

string substr(string str, int i0, int i1)
{
    return $substr(str, i0, i1);
}

bool isspace(char c)
{
    return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
}

bool isnewline(char c)
{
    return (c == '\n');
}

bool isdigit(char c)
{
	return ((c >= '0') && (c <= '9'));
}

string trim(string s)
{
    int start = 0;
    int end = strlen(s) - 1;

    while ((start < end) && isspace(s[start]))
        start = start + 1;
    while ((start <= end) && isspace(s[end]))
        end = end - 1;
    return substr(s, start, end + 1);
}

any splitson(string s, func predicate)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    any lst = {};

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

any spliton(string s, func predicate)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    any lst = {};

    while (pos < len)
    {
        if (predicate(s[pos])) {
            if (pos - start > 1)
                append(lst, substr(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    if (pos - start > 1)
        append(lst, substr(s, start, pos));
    return lst;
}

any split(string s)
{
    return spliton(s, this::isspace);
}

int sfind(string s, char c)
{
    for (int x = 0; x < strlen(s); x++)
        if (s[x] == c)
            return x;
    return -1;
}

int indexof(string s, char c) { return sfind(s, c); }

int find(any lst, any e)
{
    for (int x = 0; x < length(lst); x++)
        if (lst[x] == e)
            return x;
    return -1;
}

bool member(any lst, any e)
{
    return find(lst, e) != -1;
}

int atoi(string s)
{
	int index = 0;
	int factor = 1;
	int num = 0;

	while (isspace(s[index]))
		index++;
	if (s[index] == '-') {
		factor = -1;
		index++;
	} else if (s[index] == '+') {
		index++;
	}
	while (isdigit(s[index])) {
		num = (num * 10) + (s[index] - '0');
		index++;
	}
	return num * factor;
}
