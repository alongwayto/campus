package com.campus.device.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private Long id;

    /**
     * ROLE_ADMIN, ROLE_MAINTAINER, ROLE_USER
     */
    private String name;

    private String description;
}
