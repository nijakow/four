inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        connection()->write("Argument error!\n");
    else {
        bool user  = false;
        bool group = false;
        bool other = false;
        bool read  = false;
        bool write = false;
        bool exec  = false;
        int  mode  = 0;
        int  mini_bitmask = 0;

        int index = 0;

        string arg = argv[1];

        for (int index = 0; index < strlen(arg); index++)
        {
            if (arg[index] == 'u') user = true;
            else if (arg[index] == 'g') group = true;
            else if (arg[index] == 'o') other = true;
            else if (arg[index] == '-') mode = 1;
            else if (arg[index] == '+') mode = 2;
            else if (arg[index] == 'r') mini_bitmask = mini_bitmask | 0x04;
            else if (arg[index] == 'w') mini_bitmask = mini_bitmask | 0x02;
            else if (arg[index] == 'x') mini_bitmask = mini_bitmask | 0x01;
            else {
                connection()->write("Unexpected character in flag string: " + chr(arg[index]) + "!\n");
                exit();
                return;
            }
        }

        int bitmask = 0;
        if (user)  bitmask = bitmask | ((mini_bitmask & 0x07) << 6);
        if (group) bitmask = bitmask | ((mini_bitmask & 0x07) << 3);
        if (other) bitmask = bitmask | ((mini_bitmask & 0x07));

        for (int i = 2; i < length(argv); i++)
        {
            string file = resolve(pwd(), argv[i]);
            int flags   = stat(file);
            if (flags < 0) {
                connection()->write(argv[i], ": File not found!\n");
            } else {
                bool result = false;
                     if (mode == 1) result = chmod(file, flags & (~bitmask));
                else if (mode == 2) result = chmod(file, flags | bitmask);
                if (!result)
                    connection->write(argv[i], ": Can't set permissions!\n");
            }
        }
    }
    exit();
}
