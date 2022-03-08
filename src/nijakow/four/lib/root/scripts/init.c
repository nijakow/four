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
    if (finduser(name)) {
        printf("User '%s' already exists!\n", name);
        return;
    } else {
        string code = itoa(rand() % 10000);
        printf("Setting up wizard %s (access code %s)...\n", name, code);
        string dir = "/home/" + name;
        adduser(name);
        mkdir(dir);
        chown(dir, name);
        chgrp(dir, "users");
        chmod(dir, 0744);
        chpass(name, code);
        chsh(name, "/bin/sh.c");
    }
}

void start()
{
    mkdir("/home");
    mkdir("/tmp");
    add_archwizard("enijakow");
    add_archwizard("mhahn");
    exit();
}
