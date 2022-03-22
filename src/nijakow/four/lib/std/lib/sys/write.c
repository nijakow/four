use $syscall_write;

int write(int fd, char* buffer)
{
    return $syscall_write(fd, buffer);
}
