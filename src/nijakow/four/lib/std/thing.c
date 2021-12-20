inherit "/secure/object.c";

string short;
string long;

string get_short() { return short; }
string get_long()  { return long;  }

void set_short(string s) { short = s; }
void set_long(string l)  { long  = l; }

void create()
{
    "/secure/object.c"::create();
    set_short("<error>");
    set_long("<error>");
}
