package com.arishi.AXAM.dto.request.filter;

import lombok.Data;

@Data
public class BaseFilterRequest {

    private int page = 0;

    private int size = 10;

    private String sortBy = "createdAt";

    private String sortDir = "desc";
}