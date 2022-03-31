inherits "/std/cmd.c";

void examine(object the_object)
{
    string img = nil;
    if (the_object != nil)
        img = the_object->get_img();
    if (img == nil) {
        printf("There is no image available!\n");
    } else {
        printf("\{media/image:%s\}\n", img);
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
