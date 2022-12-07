package com.wwd.modules.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catelog2Vo {
    
    private String catelog1Id;
    private String id;
    private String name;
    private List<Catelog3Vo> catelog3List;

    /**
     * 三级
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catelog3Vo {

        private String catelog2Id;
        private String id;
        private String name;

    }
}
