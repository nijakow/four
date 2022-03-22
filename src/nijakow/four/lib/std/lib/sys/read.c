use $syscall_read;

int read(int fd, char* buffer)
{
    return $syscall_read(fd, buffer);
}
