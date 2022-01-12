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


void create()
{
    brightness = 0;
}
