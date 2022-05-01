#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"

void main(string* argv)
{
    printf("%s\n", FileSystem_GetWorkingDirectory());
    exit(0);
}
