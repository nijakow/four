/*
 *     N a m e   S e c t i o n
 */

private string article;
private string short_text;
private string long_text;
private string desc;
private string* identifiers;

string get_article() { return this.article; }
string set_article(string article) { this.article = article; }

string get_short() { return this.short_text; }
void set_short(string new_short) { this.short_text = new_short; }

string get_long() { return this.long_text; }
void set_long(string new_long) { this.long_text = new_long; }

string get_desc() { return this.desc; }
void set_desc(string new_desc) { this.desc = new_desc; }

string get_name() { return ((get_article() == "") ? "" : (get_article() + " ")) + get_short(); }

void add_id(string id) { List_Append(this.identifiers, id); }

use $log;

bool reacts_to_id(string id)
{
    for (string s : this.identifiers)
    {
        if (s == id)
            return true;
    }
    return false;
}

void reset()
{
    set_article("a");
    set_short("thing");
    set_long("A thing lies here. This is most likely a bug.");
    set_desc("This is a thing. It has no description. It is probably a bug.");
    this.identifiers = {};
}

void _init()
{
}
