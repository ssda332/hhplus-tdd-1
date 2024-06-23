package io.hhplus.tdd;

import io.hhplus.tdd.exception.ChargeNegativePointException;
import io.hhplus.tdd.exception.UserNotFoundException;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class PointControllerApiTest {
    @Autowired
    private PointService pointService;

    @Test
    void 동시성테스트() throws UserNotFoundException, ChargeNegativePointException {
        // given
        pointService.insert(1L, 100L);
        pointService.chargeUserPoint(1L, 100L, TransactionType.CHARGE);
        // when
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try {
                        pointService.chargeUserPoint(1L, 10000L, TransactionType.CHARGE);
                    } catch (UserNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (ChargeNegativePointException e) {
                        throw new RuntimeException(e);
                    }
                }),
                CompletableFuture.runAsync(() -> {
                    try {
                        pointService.chargeUserPoint(1L, 4000L, TransactionType.USE);
                    } catch (UserNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (ChargeNegativePointException e) {
                        throw new RuntimeException(e);
                    }
                }),
                CompletableFuture.runAsync(() -> {
                    try {
                        pointService.chargeUserPoint(1L, 100L, TransactionType.CHARGE);
                    } catch (UserNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (ChargeNegativePointException e) {
                        throw new RuntimeException(e);
                    }
                })
        ).join(); // 제일 오래 끝나는거 끝날떄까지 기다려줌. = 내가 비동기/병렬로 실행한 함수가 전부 끝남을 보장.

        // Thread.sleep(); // 야 ? 위에서 하나가 뭔가 문제가 있어가지고 이거보다 오래 돌았어 그럼 테스트 보장이 안되잖아.

        // then
        UserPoint userPoint = pointService.findUserPoint(1L);
        // 수식으로 검증해서 테스트 작성자의 오류도 줄인다.
        assertThat(userPoint.point()).isEqualTo(100 + 100 + 10000 - 4000 + 100);
    }
}
