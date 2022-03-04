use $log;
use $type;
use $the_object;
use $is_initialized;
use $set_initialized;
use $call;
use $random;
use $listinsert;
use $listremove;
use $substr;
use $chr;
use $length;
use $statics;
use $filechildren;
use $filetext;
use $filetext_set;
use $touch;
use $recompile;
use $mkdir;
use $rm;
use $mv;
use $checkexec;
use $stat;
use $getown;
use $getgrp;
use $chmod;
use $chown;
use $chgrp;
use $getuid;
use $uname;
use $gname;
use $members;
use $groups;
use $finduser;
use $findgroup;
use $adduser;
use $addgroup;
use $chpass;
use $getshell;
use $chsh;
use $eval;
use $getmsgs;


string type(any obj)
{
    return $type(obj);
}

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

any _new(string blueprint, ...)
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

any invoke(any f, ...)
{
    if (type(f) == "function")
        return call(f, ...);
    else
        return f;
}

void log(...)
{
    $log(...);
}

int rand()
{
    return $random();
}

any select_random(list lst)
{
    if (length(lst) == 0)
        return nil;
    else
        return lst[rand() % length(lst)];
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

string strwid(string str, int len)
{
    string ret = "";
    int strl   = length(str);

    for (int x = 0; x < len; x++)
    {
        if (x < strl)
            ret = ret + chr(str[x]);
        else
            ret = ret + " ";
    }
    return ret;
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
            if (pos - start >= 1)
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

int rsfind(string s, char c)
{
    for (int x = strlen(s) - 1; x >= 0; x--)
        if (s[x] == c)
            return x;
    return -1;
}

int indexof(string s, char c) { return sfind(s, c); }

bool endswith(string s, char c)
{
    if (strlen(s) == 0)
        return false;
    else
        return s[strlen(s) - 1] == c;
}

int find(any lst, any e)
{
    for (int x = 0; x < length(lst); x++)
        if (lst[x] == e)
            return x;
    return -1;
}

int rfind(any lst, any e)
{
    for (int x = length(lst) - 1; x >= 0; x--)
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

string basename(string path)
{
    while (endswith(path, '/'))
        path = substr(path, 0, strlen(path) - 1);
    if (path == "")
        return "/";
    int index = rsfind(path, '/');
    if (index < 0)
        return path;
    else
        return substr(path, index + 1, strlen(path));
}

list ls(string path)
{
    return $filechildren(path);
}

bool is_dir(string path)
{
    return ls(path) != nil;
}

bool is_file(string path)
{
    return cat(path) != nil;
}

string cat(string path)
{
    return $filetext(path);
}

bool echo_into(string path, string text)
{
    return $filetext_set(path, text);
}

bool touch(string path)
{
    return $touch(path);
}

bool mkdir(string path)
{
    return $mkdir(path);
}

bool rm(string path)
{
    return $rm(path);
}

bool mv(string from, string to)
{
    return $mv(from, to);
}

bool recompile(string path, func cb)
{
    return $recompile(path, cb);
}

bool checkexec(string path)
{
    return $checkexec(path);
}

int stat(string path)
{
    return $stat(path);
}

bool exists(string path)
{
    return $stat(path) >= 0;
}

string getown(string path)
{
    return $getown(path);
}

string getgrp(string path)
{
    return $getgrp(path);
}

int chmod(string path, int flags)
{
    return $chmod(path, flags);
}

int chown(string path, string uid)
{
    return $chown(path, uid);
}

int chgrp(string path, string gid)
{
    return $chgrp(path, gid);
}

string getuid()
{
    return $getuid();
}

string uname(string id)
{
    return $uname(id);
}

string gname(string id)
{
    return $gname(id);
}

list getmembers(string group) { return $members(group); }
list getgroups() { return $groups(); }

string finduser(string name)
{
    return $finduser(name);
}

string findgroup(string name)
{
    return $findgroup(name);
}

string adduser(string name)
{
    return $adduser(name);
}

string addgroup(string name)
{
    return $addgroup(name);
}

bool chpass(string user, string pass)
{
    return $chpass(user, pass);
}

string getshell(string user)
{
    return $getshell(user);
}

bool chsh(string user, string shell)
{
    return $chsh(user, shell);
}

bool isroot()
{
    return getuid() == finduser("root");
}

any eval(any target, string code, ...)
{
    return $eval(target, code, ...);
}

void fprintf(object file, string format, ...)
{
    int index = 0;
    int limit = strlen(format);

    while (index < limit)
    {
        if (format[index] != '%') {
            file->write(chr(format[index]));
        } else {
            index = index + 1;
            if (index >= limit) break;
            else if (format[index] == '%') file->write("%");
            else if (format[index] == 'a') file->write(va_next);
            else if (format[index] == 's') file->write(va_next);
            else if (format[index] == 'c') file->write(chr(va_next));
            else if (format[index] == 'd') file->write(itoa(va_next));
            else if (format[index] == 'x') file->write(itoax(va_next));
        }
        index = index + 1;
    }
}

list getmsgs()
{
    return $getmsgs();
}
