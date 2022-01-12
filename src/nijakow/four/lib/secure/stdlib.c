use $log;
use $the_object;
use $is_initialized;
use $set_initialized;
use $call;
use $listinsert;
use $listremove;
use $substr;
use $chr;
use $length;
use $statics;


object the(string id)
{
    object obj = $the_object(id);

    if (obj != nil)
    {
        if (!$is_initialized(obj)) {
            $set_initialized(obj);
            obj->_init();
        }
    }
    return obj;
}

any new(string blueprint, ...)
{
    object obj = the(blueprint);
    if (obj != nil)
        return obj->clone(...);
    return nil;
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

int length(any seq)
{
    return $length(seq);
}

void insert(list lst, int index, any value)
{
    $listinsert(lst, index, value);
}

void append(list lst, any value)
{
    insert(lst, length(lst), value);
}

any remove(list lst, int index)
{
    return $listremove(lst, index);
}

int strlen(string str)
{
    return $length(str);
}

string substr(string str, int i0, int i1)
{
    return $substr(str, i0, i1);
}

string chr(char c)
{
    return $chr(c);
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

bool isupper(char c) { return c >= 'A' && c <= 'Z'; }
bool islower(char c) { return c >= 'a' && c <= 'z'; }

char toupper(char c)
{
    if (islower(c))
        return c - ('a' - 'A');
    return c;
}

string capitalize(string s)
{
    /* TODO: Skip escape sequences */
    int len = strlen(s);
    if (len == 0)
        return s;
    else
        return chr(toupper(s[0])) + substr(s, 1, len);
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

list splitson(string s, func predicate)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    list lst = {};

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

list spliton(string s, func predicate)
{
    int pos = 0;
    int start = 0;
    int len = strlen(s);
    list lst = {};

    while (pos < len)
    {
        if (predicate(s[pos])) {
            if (pos - start > 1)
                append(lst, substr(s, start, pos));
            start = pos + 1;
        }
        pos = pos + 1;
    }
    if (pos - start >= 1)
        append(lst, substr(s, start, pos));
    return lst;
}

list split(string s)
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

string itoa(int i)
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
        s = chr((i % 10) + '0') + s;
        i = i / 10;
    }

    return pre + s;
}

bool isslash(char c)
{
    return (c == '/');
}

string pwd()
{
    if ($statics()["pwd"] == nil)
        chdir("/");
    return $statics()["pwd"];
}

void chdir(string dir)
{
    $statics()["pwd"] = dir;
}

string updir(string base)
{
    list l = spliton(base, this::isslash);
    string s = "";
    for (int i = 0; i < length(l) - 1; i++)
    {
        if (l[i] != "") {
            s = s + "/";
            s = s + l[i];
        }
    }
    if (s == "") s = "/";
    return s;
}

string resolv1(string base, string dir)
{
    if (dir == "" || dir == ".") return base;
    else if (dir == "..") return updir(base);
    else {
        if (base[-1] == '/') return base + dir;
        else return base + "/" + dir;
    }
}

string resolve(string base, string path)
{
    if (path[0] == '/') return path;

    list toks = spliton(path, this::isslash);

    foreach (string dir : toks)
    {
        base = resolv1(base, dir);
    }
    return base;
}
