/*
 *    D e s c r i p t i v e   P r o p e r t i e s
 */

bool   is_properly_named;
string article;
string name;
string desc;
string image;

string get_name()         { return name; }
void   set_name(string n) { name = n; }

string get_short()
{
    if (is_properly_named)
        return get_name();
    return article + " " + get_name();
}

string get_long() { return get_short(); }

string get_desc()         { return desc;  }
void   set_desc(string l) { desc  = l; }

string get_the_short()
{
    if (is_properly_named)
        return get_name();
    return "the " + get_name();
}

void set_properly_named() { is_properly_named = true; }

string get_img() { return image; }
void set_img(string image, string size) { this.image = size + "," + image; }


/*
 *    R e a c t i o n s   a n d   F i n d i n g
 */

list ids;

bool reacts(list words)
{
    for (int x = 0; x < length(words); x++)
    {
        if (!member(ids, words[x]))
            return false;
    }
    return length(words) != 0;
}

void add_IDs(...)
{
    while (va_count > 0)
    {
        append(ids, va_next);
    }
}

void _find_thing(list names, list elems)
{
    if (this->reacts(names))
        append(elems, this);
    for (object child = get_children();
         child != nil;
         child = child->get_sibling())
    {
        child->_find_thing(names, elems);
    }
}

list find_thing(string name)
{
    list elems = {};
    _find_thing(split(name), elems);
    return elems;
}

list find_thing_here(string name)
{
    list names = split(name);
    list elems = {};
    foreach (object obj : visible_objects_here())
    {
        if (obj->reacts(names))
            append(elems, obj);
    }
    return elems;
}


void create()
{
    is_properly_named = false;
    article = "a";
    set_name("<error>");
    set_desc("<error>");
    image = nil;
    ids = {};
}
