use $syscall_open;

int open(string file, int flags)
{
    return $syscall_open(file, flags);
}
