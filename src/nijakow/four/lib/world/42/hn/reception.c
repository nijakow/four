inherits "/std/room.c";

void create()
{
    "/std/room.c"::create();
    set_name("Reception of 42 Heilbronn");
    set_img("https://www.42heilbronn.de/assets/images/d/42-heilbronn-Campus_paceship_reception-29a146bc.jpg", "400x");
    set_desc("Text follows.");
}
