inherit "/secure/stdlib.c";

use $on_connect;

void receive(any port)
{
	log("New connection!\n");
	object connection = new("/secure/connection.c", port);
	new("/secure/login.c")->start(connection);
}

void create()
{
	$on_connect(this::receive);
}
