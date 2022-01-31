inherits "/std/cmd.c";

void examine(object the_object)
{
    string img = nil;
    if (the_object != nil)
        img = the_object->get_img();
    if (img == nil) {
        connection()->write("There is no image available!\n");
    } else {
        connection()->write("\{^" + img + "\}\n");
    }
    exit();
}

void start()
{
    if (trim(arg()) == "")
        examine(get_location());
    else
        select_and_call(arg(), this::examine);
}
