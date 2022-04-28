inherits "/lib/app.c";
inherits "/lib/sys/identity/user.c";

void main(string* argv)
{
    printf("%s\n", User_Whoami());
    exit(0);
}
