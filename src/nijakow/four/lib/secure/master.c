inherits "/secure/object.c";

inherits "/lib/io/log.c";

use $on_connect;
use $on_error;

void receive(any port)
{
	System_Log("New connection!\n");
}

void handle_error(string key, string type, string msg)
{
    System_Log("Received an error!\n");
}

void create()
{
    "/secure/object.c"::create();
    $on_error(this::handle_error);
    $on_connect(this::receive);
}
