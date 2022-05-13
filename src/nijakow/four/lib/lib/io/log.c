inherits "/lib/io/sprintf.c";

use $log;

void System_Log(...)
{
    $log(sprintf(...));
}
