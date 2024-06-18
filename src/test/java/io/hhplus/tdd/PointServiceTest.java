package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointTable userPointTable;

    @Test
    @DisplayName("Service - 특정 유저 조회")
    void findUserPoint() {
        /**
         * 특정 유저를 올바르게 반환하는지 확인한다.
         * 가장 쉽게 작성후 예를 추가하여 일반화함 (user1 -> user1, user2)
         */
        //given
        UserPoint user1 = new UserPoint(1L, 200, System.currentTimeMillis());
        given(userPointTable.selectById(1L)).willReturn(user1);
        UserPoint user2 = new UserPoint(2L, 200, System.currentTimeMillis());
        given(userPointTable.selectById(2L)).willReturn(user2);

        //when
        UserPoint result1 = pointService.findUserPoint(user1.id());
        UserPoint result2 = pointService.findUserPoint(user2.id());

        //then
        assertThat(result1.id()).isEqualTo(user1.id());
        assertThat(result1.point()).isEqualTo(user1.point());
        assertThat(result2.id()).isEqualTo(user2.id());
        assertThat(result2.point()).isEqualTo(user2.point());
    }




}
