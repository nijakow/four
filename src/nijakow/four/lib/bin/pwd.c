inherits "/lib/app.c";
inherits "/lib/sys/fs/paths.c";

void main(string* argv)
{
    printf("%s\n", FileSystem_GetWorkingDirectory());
    exit(0);
}
