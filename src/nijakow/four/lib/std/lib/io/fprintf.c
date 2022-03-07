
void fprintf(object file, string format, ...)
{
    int index = 0;
    int limit = strlen(format);

    while (index < limit)
    {
        if (format[index] != '%') {
            file->write(chr(format[index]));
        } else {
            index = index + 1;
            if (index >= limit) break;
            else if (format[index] == '%') file->write("%");
            else if (format[index] == 'a') file->write(va_next);
            else if (format[index] == 's') file->write(va_next);
            else if (format[index] == 'c') file->write(chr(va_next));
            else if (format[index] == 'd') file->write(itoa(va_next));
            else if (format[index] == 'x') file->write(itoax(va_next));
        }
        index = index + 1;
    }
}
