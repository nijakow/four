use $syscall_close;

void close(int fd)
{
    $syscall_close(fd);
}
