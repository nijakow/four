inherits "/std/app.c";

void add_archwizard(string name)
{
    printf("Setting up wizard %s...\n", name);
    string dir = "/home/" + name;
    adduser(name);
    mkdir(dir);
    chown(dir, name);
    chgrp(dir, "users");
    chmod(dir, 0744);
    chpass(name, "42");
}

void start()
{
    mkdir("/home");
    mkdir("/tmp");
    add_archwizard("enijakow");
    add_archwizard("mhahn");
    exit();
}
