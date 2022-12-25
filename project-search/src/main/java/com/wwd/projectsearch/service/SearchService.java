package com.wwd.projectsearch.service;

import com.wwd.projectsearch.Vo.SearchParam;
import com.wwd.projectsearch.Vo.SearchResult;

public interface SearchService {


    SearchResult search(SearchParam searchParam);
}
