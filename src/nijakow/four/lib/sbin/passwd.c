#include "/lib/app.c"
#include "/lib/sys/identity/user.c"

private void setpass(string pw, string name)
{
    if (User_ChangePassword(name, pw))
        printf("Password updated.\n");
    else
        printf("Error, password could not be set!\n");
    exit(0);
}

void main(string* argv)
{
    string name;

    if (argv.length == 1)
        name = User_Whoami();
    else if (argv.length == 2) {
        if (User_AmIRoot())
            name = argv[1];
        else {
            printf("Only root can do this!\n");
            exit(1);
            return;
        }
    } else {
        printf("usage: %s [username]\n", argv[0]);
        exit(1);
        return;
    }
    password(this::(name)setpass, "Password for %s: ", name);
}
