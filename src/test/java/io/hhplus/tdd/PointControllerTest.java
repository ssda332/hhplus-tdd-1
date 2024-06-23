package io.hhplus.tdd;

import io.hhplus.tdd.exception.ChargeNegativePointException;
import io.hhplus.tdd.exception.UserNotFoundException;
import io.hhplus.tdd.point.*;
import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.dto.UserPointResultDto;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    @DisplayName("Controller - 특정 유저 조회")
    void findUserPoint() throws Exception, UserNotFoundException {
        /**
         *  먼저 구현할 요구사항 중 가장 쉽고 우선적으로 개발되어야 할 사항부터 고려해봤을 때,
         *  포인트를 사용 및 충전하려면 특정 회원의 포인트를 조회할 수 있어야 한다고 판단해서
         *  포인트 조회부터 시작해서 내역 조회, 그 후 충전 및 사용 요구사항을 개발하기로 했다.
         *
         *  2-1. Service 단위 테스트를 완료한 후 Controller 단위 테스트 작성
         *  2-2. user1만 테스트
         *  2-2. user2도 추가해서 구현을 일반화시킴
         */
        //given
        UserPoint user1 = new UserPoint(1L, 100, System.currentTimeMillis());
        given(pointService.findUserPoint(1L)).willReturn(user1);
        UserPoint user2 = new UserPoint(2L, 200, System.currentTimeMillis());
        given(pointService.findUserPoint(2L)).willReturn(user2);
        String url = "/point/{userId}";

        //when
        ResultActions actions1 = mockMvc.perform(get(url, 1L));
        ResultActions actions2 = mockMvc.perform(get(url, 2L));

        //then
        actions1.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user1.id()))
                .andExpect(jsonPath("$.point").value(user1.point()))
        ;

        actions2.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user2.id()))
                .andExpect(jsonPath("$.point").value(user2.point()))
        ;

    }

    @Test
    @DisplayName("Controller - 특정 유저 포인트 이력 조회")
    void findUserPointHistory() throws Exception {
        /**
         * 특정 유저 조회와 같이 예를 추가하면서 일반화함
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

        given(pointService.findHistoriesById(1L)).willReturn(list1);
        given(pointService.findHistoriesById(2L)).willReturn(list2);
        String url = "/point/{userId}/histories";

        //when
        ResultActions actions1 = mockMvc.perform(get(url, 1L));
        ResultActions actions2 = mockMvc.perform(get(url, 2L));

        //then
        actions1.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(list1.size()));

        actions2.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(list2.size()));
    }

    @Test
    @DisplayName("Controller - 특정 유저 조회 - 유저가 존재하지 않음")
    void userNotFound() throws Exception, UserNotFoundException {
        given(pointService.findUserPoint(1L)).willThrow(new UserNotFoundException());
        String url = "/point/{userId}";

        //when
        ResultActions actions1 = mockMvc.perform(get(url, 1L));

        //then
        String errorStatus = "$.[?(@.code == '%s')]";

        actions1.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath(errorStatus, 404).exists());

    }

    @Test
    @DisplayName("Controller - 특정 유저 포인트 충전")
    void chargeUserPoint() throws Exception, UserNotFoundException, ChargeNegativePointException {
        //given
        //given(pointService.findUserPoint(1L)).willReturn(user1);

        long id = 1L;
        long initialPoint = 200L;
        long chargePoint1 = 100L;
        long chargePoint2 = 200L;
        UserPointResultDto dto1 = new UserPointResultDto(id, initialPoint + chargePoint1, TransactionType.CHARGE);
        UserPointResultDto dto2 = new UserPointResultDto(id, initialPoint + chargePoint2, TransactionType.CHARGE);
        given(pointService.chargeUserPoint(1L, chargePoint1, TransactionType.CHARGE)).willReturn(dto1);
        given(pointService.chargeUserPoint(1L, chargePoint2, TransactionType.CHARGE)).willReturn(dto2);

        String url = "/point/{userId}/charge";

        //when
        ResultActions actions1 = mockMvc.perform(patch(url, 1L)
                .content(String.valueOf(chargePoint1))
                .contentType(MediaType.APPLICATION_JSON));

        ResultActions actions2 = mockMvc.perform(patch(url, 1L)
                .content(String.valueOf(chargePoint2))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions1.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto1.id()))
                .andExpect(jsonPath("$.result").value(dto1.result()))
        ;

        actions2.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto2.id()))
                .andExpect(jsonPath("$.result").value(dto2.result()))
        ;
    }

}
