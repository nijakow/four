inherit "/secure/object.c";
inherit "/std/thing/movement.c";

/*
 *    D e s c r i p t i v e   P r o p e r t i e s
 */

bool   is_properly_named;
string article;
string name;
string desc;

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


/*
 *    E v e n t s
 */

void evt_entering(object obj) {}
void evt_leaving(object obj) {}

/*
 *    C h e c k s
 */

bool is_container;

bool check_transparent() { return true; }
bool check_move(object actor, object target) { return true; }
bool check_object_entering(object actor, object obj, object target)  { return is_container; }
bool check_object_leaving(object actor, object obj, object target)   { return true;         }


/*
 *    A c t i o n s   a n d   M o v e m e n t
 */

void act(...)
{
    foreach (object obj : objects_here())
    {
        if (obj != this)
            obj->write(...);
    }
}

void me_act(...)
{
    act(capitalize(get_short()), " ", ...);
}

bool try_move_check_obj(object actor, object obj, object target, object cp)
{
	object current_obj = obj->get_parent();

	while (current_obj != nil)
	{
		if (!current_obj->check_object_leaving(actor, obj, target))
			return false;
		if (current_obj == cp)
			break;
		current_obj = current_obj->get_parent();
	}
	return true;
}

bool try_move_check_target(object actor, object obj, object target, object cp)
{
	object current_obj = target->get_parent();

	while (current_obj != nil)
	{
		if (!current_obj->check_object_entering(actor, obj, target))
			return false;
		if (current_obj == cp)
			break;
		current_obj = current_obj->get_parent();
	}
	return true;
}

bool try_move(object actor, object obj, object target)
{
    if (!obj->check_move(actor, target))
		return false;

	object cp = obj.find_common_parent(target);

    if (!(try_move_check_obj(actor, obj, target, cp) || try_move_check_target(actor, obj, target, cp)))
		return false;
	
	return true;
}

bool act_move(object actor, object obj, object target)
{
	if (!try_move(actor, obj, target))
	    return false;
	
	obj->me_act("leaves.\n");
    obj->move_to(target);
    obj->me_act("arrives.\n");
    
    return true;    
}

bool act_goto(object location)
{
    return act_move(this, this, location);
}

bool act_take(object obj)
{
    if (this->contains_or_is(obj)) {
        return false;
    } else if (try_move(this, obj, this)) {
        me_act("takes ", obj->get_short(), ".\n");
        obj->move_to(this);
        return true;
    }
    return false;
}

bool act_drop(object obj)
{
    object location = get_location();
    
    if (!this->contains(obj)) {
        return false;
    } else if (try_move(this, obj, location)) {
        me_act("drops ", obj->get_short(), ".\n");
        obj->move_to(location);
        return true;
    }
    return false;
}


/*
 *    L i g h t   a n d   D a r k n e s s
 */

int brightness;

void set_brightness(int value)
{
    brightness = value;
}

int get_brightness()
{
    return brightness;
}

int _query_light_level(object ignore)
{
    int max_brightness = 0;
    foreach (object obj : all_children())
    {
        int the_brightness = obj->get_brightness();
        if (the_brightness > max_brightness)
            max_brightness = the_brightness;
    }
    return max_brightness;
}

int query_light_level()
{
    return _query_light_level(this);
}

int query_light_level_here()
{
    return get_location()->query_light_level();
}


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


/*
 *    M i s c e l l a n e o u s
 */

void write(...) {}


void create()
{
    "/secure/object.c"::create();
    "/std/thing/movement.c"::create();

    is_properly_named = false;
    article = "a";
    set_name("<error>");
    set_desc("<error>");
    ids = {};
}

void _init()
{
    "/secure/object.c"::_init();
    is_container = true;
    brightness = 0;
    create();
}
