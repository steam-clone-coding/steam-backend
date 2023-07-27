package com.clonecoding.steam.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private T data;

    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();


}
