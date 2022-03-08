inherits "/std/app.c";

/*
 * This executable was developed to make setting up
 * the default server configuration easier.
 */

/**
 * Adds a new arch wizard.
 * Called by the main function.
 * Can also be called from outside.
 */
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
