inherits "/std/cmd.c";

void start()
{
    string img = get_location()->get_img();
    if (img == nil) {
        connection()->write("There is no image available!\n");
    } else {
        connection()->write("\{^" + img + "\}\n");
    }
    exit();
}
