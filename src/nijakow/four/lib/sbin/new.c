inherits "/std/app.c";

void start()
{
    if (length(argv) != 3)
        connection()->write("Argument error!\n");
    else {
        object target = the(resolve(pwd(), argv[2]));
        if (target == nil) {
            connection()->write("Could not find the target!\n");
        } else {
            string path = resolve(pwd(), argv[1]);
            connection()->write("Instantiating ", path);
            object inst = new(path);
            inst->move_to(target);
        }
    }
    exit();
}
