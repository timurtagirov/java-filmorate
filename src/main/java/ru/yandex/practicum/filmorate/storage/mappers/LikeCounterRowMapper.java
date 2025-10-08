package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikeCounter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikeCounterRowMapper implements RowMapper<LikeCounter> {
    @Override
    public LikeCounter mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LikeCounter likeCounter = new LikeCounter();
        likeCounter.setFilmId(resultSet.getInt("film_id"));
        likeCounter.setLikeCount(resultSet.getInt("like_count"));
        return likeCounter;
    }
}
