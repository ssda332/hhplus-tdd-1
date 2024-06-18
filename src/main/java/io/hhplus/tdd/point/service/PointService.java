package io.hhplus.tdd.point.service;

import io.hhplus.tdd.exception.ChargeNegativePointException;
import io.hhplus.tdd.exception.UserNotFoundException;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.dto.UserPointResultDto;
import io.hhplus.tdd.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final Lock lock = new ReentrantLock();

    public UserPoint insert(Long id, Long amount) {
        return pointRepository.insertOrUpdate(id, amount);
    }

    public UserPoint findUserPoint(Long id) throws UserNotFoundException {
        UserPoint userPoint = pointRepository.findUserPoint(id);
        if (userPoint.point() == 0) throw new UserNotFoundException();

        return userPoint;
    }

    public UserPointResultDto chargeUserPoint(long id, long amount, TransactionType type) throws UserNotFoundException, ChargeNegativePointException {
        if (amount < 0) throw new ChargeNegativePointException();

        lock.lock();

        try {
            UserPoint userPoint = pointRepository.findUserPoint(id);
            if (userPoint.point() == 0) throw new UserNotFoundException();

            long cal = -1;
            if (type == TransactionType.CHARGE) cal = userPoint.point() + amount;
            else if (type == TransactionType.USE) cal = userPoint.point() - amount;

            // if (cal < 0) exception

            UserPoint result = pointRepository.insertOrUpdate(id, cal);

            UserPointResultDto dto = UserPointResultDto.builder()
                    .id(result.id())
                    .result(result.point())
                    .type(type)
                    .build();

            PointHistory pointHistory = pointRepository.insertHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return dto;

        } finally {
            lock.unlock();
        }


    }

    public List<PointHistory> findHistoriesById(long userId) {
        //PointHistory pointHistory = PointRepository.findPointHistory(userId);
        return pointRepository.findPointHistory(userId);
    }
}
