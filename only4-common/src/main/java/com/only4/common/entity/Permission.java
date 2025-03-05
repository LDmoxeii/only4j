package com.only4.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LD_moxeii
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    private String name;

    private String code;
}
