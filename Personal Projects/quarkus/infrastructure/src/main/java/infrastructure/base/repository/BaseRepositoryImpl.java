package infrastructure.base.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

import domain.base.repository.BaseRepository;
import domain.base.entity.BaseEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Log
public abstract class BaseRepositoryImpl<T extends BaseEntity> implements BaseRepository<T>, PanacheRepositoryBase<T, Long> {

    protected Class<T> type;

    @SuppressWarnings("all")
    protected BaseRepositoryImpl() {
        Class<? extends BaseRepositoryImpl> aClass = getClass();
        while (!(aClass.getGenericSuperclass() instanceof ParameterizedType)) {
            aClass = (Class<? extends BaseRepositoryImpl>) aClass.getSuperclass();
        }

        Type paramType = ((ParameterizedType) aClass.getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            this.type = (Class<T>) Class.forName(paramType.getTypeName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    
    @Override
    public Optional<T> get(long id) {
        return find("id", id).singleResultOptional();
    }

    @Override
    public long count(long id) {
        return count("id", id);
    }

    @Override
    @Transactional
    public void deleteBy(String field, Object... value) {
        delete(field, value);
    }
    
    @Override
    @Transactional
    public void save(T entity) {
        persist(entity);
    }

    @Override
    public List<T> findAllEntities() {
        return findAll().list();
    }
    
    @Override
    public Optional<T> findBy(String field, Object... value) {
        return find(field, value).singleResultOptional();
    }
}