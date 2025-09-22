package application.base.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<T, R> {

    R toDto(T content);

    default List<R> toDto(Collection<T> contents) {
        return contents.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
