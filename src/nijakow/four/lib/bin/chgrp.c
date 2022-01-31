inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        printf("Argument error!\n");
    else {
        string gid = findgroup(argv[1]);
        if (gid == nil)
            printf("Group %s not found!\n", argv[1]);
        else {
            for (int i = 2; i < length(argv); i++)
            {
                if (!chgrp(resolve(pwd(), argv[i]), gid))
                    printf("%s: Can't change group!\n", argv[i]);
            }
        }
    }
    exit();
}
