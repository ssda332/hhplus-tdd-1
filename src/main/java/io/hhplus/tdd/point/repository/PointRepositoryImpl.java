package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Override
    public UserPoint findUserPoint(Long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public UserPoint insertOrUpdate(Long id, Long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }

    @Override
    public List<PointHistory> findPointHistory(Long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public PointHistory insertHistory(Long id, Long amount, TransactionType type, Long updateMillis) {
        return pointHistoryTable.insert(id, amount, type, System.currentTimeMillis());
    }
}
