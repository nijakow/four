package nijakow.four.serialization.base;

public interface IMappingSerializer extends IBasicSerializer {
    ISerializer openEntry(String name);
}
