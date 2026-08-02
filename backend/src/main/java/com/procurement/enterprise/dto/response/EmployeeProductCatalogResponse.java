package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeProductCatalogResponse {
    private List<ProductResponse> departmentProducts;
    private List<ProductResponse> otherDepartmentProducts;
}
