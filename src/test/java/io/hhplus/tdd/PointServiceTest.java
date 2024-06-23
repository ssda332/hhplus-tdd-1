package io.hhplus.tdd;

import io.hhplus.tdd.exception.ChargeNegativePointException;
import io.hhplus.tdd.exception.UserNotFoundException;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.dto.UserPointResultDto;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Test
    @DisplayName("Service - 특정 유저 조회")
    void findUserPoint() throws UserNotFoundException {
        /*
         * 특정 유저를 올바르게 반환하는지 확인한다.
         * 가장 쉽게 작성후 예를 추가하여 일반화함 (user1 -> user1, user2)
         */
        //given
        UserPoint user1 = new UserPoint(1L, 200, System.currentTimeMillis());
        given(pointRepository.findUserPoint(1L)).willReturn(user1);
        UserPoint user2 = new UserPoint(2L, 200, System.currentTimeMillis());
        given(pointRepository.findUserPoint(2L)).willReturn(user2);

        //when
        UserPoint result1 = pointService.findUserPoint(user1.id());
        UserPoint result2 = pointService.findUserPoint(user2.id());

        //then
        assertThat(result1.id()).isEqualTo(user1.id());
        assertThat(result1.point()).isEqualTo(user1.point());
        assertThat(result2.id()).isEqualTo(user2.id());
        assertThat(result2.point()).isEqualTo(user2.point());
    }

    @Test
    @DisplayName("Service - 특정 유저 조회 - 유저가 존재하지 않음")
    void userNotFound() throws UserNotFoundException {
        //given
        long id = 1L;

        UserPoint user1 = new UserPoint(id, 0, System.currentTimeMillis());
        given(pointRepository.findUserPoint(id)).willReturn(user1);

        //then
        assertThatThrownBy(() -> pointService.findUserPoint(id))
                .isInstanceOf(UserNotFoundException.class);

    }


    @Test
    @DisplayName("Service - 특정 유저 포인트 충전")
    void chargeUserPoint() throws UserNotFoundException, ChargeNegativePointException {
        //given
        long id = 1L;
        long initialPoint = 200L;
        long amount1 = 100L;
        long amount2 = 200L;

        UserPoint user1 = new UserPoint(id, initialPoint, System.currentTimeMillis());
        UserPoint updateUser1 = new UserPoint(id, initialPoint + amount1, System.currentTimeMillis());
        UserPoint updateUser2 = new UserPoint(id, initialPoint + amount2, System.currentTimeMillis());
        PointHistory history = new PointHistory(id, id, amount1, TransactionType.CHARGE, System.currentTimeMillis());

        given(pointRepository.findUserPoint(id)).willReturn(user1);
        given(pointRepository.insertOrUpdate(user1.id(), initialPoint + amount1)).willReturn(updateUser1);
        given(pointRepository.insertOrUpdate(user1.id(), initialPoint + amount2)).willReturn(updateUser2);
        given(pointRepository.insertHistory(any(), any(), any(), any())).willReturn(history);

        //when
        UserPointResultDto result1 = pointService.chargeUserPoint(id, amount1, TransactionType.CHARGE);
        UserPointResultDto result2 = pointService.chargeUserPoint(id, amount2, TransactionType.CHARGE);

        //then
        assertThat(result1.id()).isEqualTo(id);
        assertThat(result1.result()).isEqualTo(initialPoint + amount1);
        assertThat(result1.type()).isEqualTo(TransactionType.CHARGE);

        assertThat(result2.id()).isEqualTo(id);
        assertThat(result2.result()).isEqualTo(initialPoint + amount2);
        assertThat(result2.type()).isEqualTo(TransactionType.CHARGE);

    }

    @Test
    @DisplayName("Service - 특정 유저 포인트 충전 - 음수 포인트 충전 경우")
    void chargeNegativePoint() {
        //given
        long id = 1L;
        long negativeAmount = -100L;

        //then
        assertThatThrownBy(() -> pointService.chargeUserPoint(1L, negativeAmount, TransactionType.CHARGE))
                .isInstanceOf(ChargeNegativePointException.class);
    }

    @Test
    @DisplayName("Service - 특정 유저 포인트 이력 조회")
    void findUserPointHistory() {
        /**
         * 특정 유저 포인트 이력을 올바르게 반환하는지 확인한다.
         * 가장 쉽게 작성후 예를 추가하여 일반화함 (history1 -> history1, history2)
         */
        //given
        PointHistory history1 = new PointHistory(1L, 1L, 100, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory history2 = new PointHistory(2L, 1L, 200, TransactionType.USE, System.currentTimeMillis());
        PointHistory history3 = new PointHistory(3L, 1L, 300, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory history4 = new PointHistory(4L, 2L, 200, TransactionType.USE, System.currentTimeMillis());
        PointHistory history5 = new PointHistory(5L, 2L, 100, TransactionType.CHARGE, System.currentTimeMillis());
        List<PointHistory> list1 = new ArrayList<>();
        List<PointHistory> list2 = new ArrayList<>();
        list1.add(history1);
        list1.add(history2);
        list1.add(history3);
        list2.add(history4);
        list2.add(history5);

        given(pointRepository.findPointHistory(1L)).willReturn(list1);
        given(pointRepository.findPointHistory(2L)).willReturn(list2);

        //when
        List<PointHistory> result1 = pointService.findHistoriesById(1L);
        List<PointHistory> result2 = pointService.findHistoriesById(2L);

        //then
        assertThat(result1).hasSize(3);
        assertThat(result1).containsExactly(history1, history2, history3);

        assertThat(result2).hasSize(2);
        assertThat(result2).containsExactly(history4, history5);

    }


}
