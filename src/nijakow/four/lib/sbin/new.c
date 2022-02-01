inherits "/std/app.c";

void start()
{
    if (length(argv) != 3)
        printf("Argument error!\n");
    else {
        object target = the(resolve(pwd(), argv[2]));
        if (target == nil) {
            printf("Could not find the target!\n");
        } else {
            string path = resolve(pwd(), argv[1]);
            printf("Instantiating %s...\n", path);
            object inst = new(path);
            inst->move_to(target);
        }
    }
    exit();
}
