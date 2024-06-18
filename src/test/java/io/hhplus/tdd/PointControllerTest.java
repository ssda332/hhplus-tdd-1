package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void findUserPoint() throws Exception {
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


}
