package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.TransactionType;
import lombok.Builder;

@Builder
public record UserPointResultDto (
        long id,
        long result,
        TransactionType type
){


}
