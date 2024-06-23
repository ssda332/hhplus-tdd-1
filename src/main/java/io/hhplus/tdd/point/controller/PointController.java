package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.exception.ChargeNegativePointException;
import io.hhplus.tdd.exception.UserNotFoundException;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.dto.UserPointResultDto;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.domain.UserPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }


    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) throws UserNotFoundException {
        /**
         * 처음에는 고정된 UserPoint를 반환해주고 예를 추가해 구현을 일반화시킨다.
         * 구현 일반화시키면서 PointService 사용
         */
        //return new UserPoint(1L, 100, 0);
        return pointService.findUserPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {

        return pointService.findHistoriesById(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPointResultDto charge(
            @PathVariable long id,
            @RequestBody long amount
    ) throws UserNotFoundException, ChargeNegativePointException {

        return pointService.chargeUserPoint(id, amount, TransactionType.CHARGE);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPointResultDto use(
            @PathVariable long id,
            @RequestBody long amount
    ) throws UserNotFoundException, ChargeNegativePointException {

        return pointService.chargeUserPoint(id, amount, TransactionType.USE);
    }
}
