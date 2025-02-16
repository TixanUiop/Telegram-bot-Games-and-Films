package org.evgeny.DAO;

public interface IDAO<T,F> {

    boolean create(T obj);
    boolean update(T obj);
    boolean delete(T obj);
    T find(F id);

}
