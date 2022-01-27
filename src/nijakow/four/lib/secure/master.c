inherits "/secure/object.c";

use $on_connect;
use $on_error;

void receive(any port)
{
	object connection = new("/secure/connection.c", port);
	new("/secure/login.c", connection, nil, nil)->start();
}

void handle_error(string key, string type, string msg)
{
    log("Received an error: ", key, " ", type, " ", msg, "\n");
}

void create()
{
    "/secure/object.c"::create();
    $on_error(this::handle_error);
    $on_connect(this::receive);
}
