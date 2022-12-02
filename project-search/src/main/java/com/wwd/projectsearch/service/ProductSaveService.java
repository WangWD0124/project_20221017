package com.wwd.projectsearch.service;

import com.wwd.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
