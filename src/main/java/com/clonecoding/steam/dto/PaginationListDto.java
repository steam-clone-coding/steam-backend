package com.clonecoding.steam.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationListDto<T> {

    private Number count;
    private List<T> data;

}