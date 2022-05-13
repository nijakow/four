#include "/lib/app.c"

use $blueprints;

void main(string* argv)
{
    for (string s : $blueprints())
    {
        printf("%s\n", s);
    }
    exit(0);
}
