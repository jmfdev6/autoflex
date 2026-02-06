package com.autoflex.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.QueryParam

class PageRequest {
    @QueryParam("page")
    @DefaultValue("0")
    @field:Min(value = 0, message = "Page number must be >= 0")
    var page: Int = 0
    
    @QueryParam("size")
    @DefaultValue("20")
    @field:Min(value = 1, message = "Page size must be >= 1")
    @field:Max(value = 100, message = "Page size must be <= 100")
    var size: Int = 20
    
    @QueryParam("sort")
    @DefaultValue("code")
    var sort: String = "code"
    
    fun getSortField(): String {
        return if (sort.startsWith("-")) {
            sort.substring(1)
        } else {
            sort
        }
    }
    
    fun isAscending(): Boolean {
        return !sort.startsWith("-")
    }
}
