inherit "/secure/stdlib.c";

use $on_connect;

void receive(any connection)
{
	log("New connection!\n");
	new("/secure/login.c", new("/secure/connection.c", connection));
}

void create()
{
	$on_connect(this::receive);
}
