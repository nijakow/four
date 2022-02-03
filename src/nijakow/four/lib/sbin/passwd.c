inherits "/std/app.c";

string uid;

void set_password(string password)
{
    if (chpass(uid, password)) {
        printf("Successfully updated password!\n");
    } else {
        printf("Could not change password!\n");
    }
    exit();
}

void process(string uid)
{
    if (uid == nil) {
        printf("User not found!\n");
        exit();
    } else {
        this.uid = uid;
        password(this->set_password, "Password: ");
    }
}

void start()
{
    if (length(argv) == 1)
        process(getuid());
    else if (length(argv) == 2)
        process(finduser(argv[1]));
    else
    {
        printf("Argument error!\n");
        exit();
    }
}
