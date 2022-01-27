/*
 *    E v e n t s
 */

void evt_entering(object obj) {}
void evt_leaving(object obj) {}

/*
 *    C h e c k s
 */

bool check_transparent() { return true; }
bool check_move(object actor, object target) { return true; }
bool check_object_entering(object actor, object obj, object target)  { return query_is_container(); }
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
    if (this->contains_or_is(obj) || obj->query_is_heavy()) {
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

void create()
{
}
