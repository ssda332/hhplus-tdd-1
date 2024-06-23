package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;

import java.util.List;

public interface PointRepository {

    UserPoint findUserPoint(Long id);
    UserPoint insertOrUpdate(Long id, Long amount);

    List<PointHistory> findPointHistory(Long id);

    PointHistory insertHistory(Long id, Long amount, TransactionType type, Long updateMillis);
}
