package org.evgeny.Mapper;

public interface IMapper<F, T> {
    T map(F from);
}
