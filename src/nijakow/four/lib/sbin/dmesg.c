inherits "/lib/app.c";
inherits "/lib/conversion/string_to_int.c";

use $getmsgs;

void main(string* argv)
{
    int length = -1;

    if (argv.length == 2)
        length = Conversion_StringToInt(argv[1]);
    foreach (string line : $getmsgs(length))
    {
        printf("%s\n", line);
    }
    exit(0);
}
