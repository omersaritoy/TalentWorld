package com.TalentWorld.backend.dto.response;

import java.util.List;

public record PaginationResponse<T>(long totalCount,List<T> users) {

}
