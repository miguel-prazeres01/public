package domain.base.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    Optional<T> get(long id);
    long count(long id);
    Optional<T> findBy(String field, Object... value);
    void save(T member);
    List<T> findAllEntities();
    void deleteBy(String field, Object... value);
}
