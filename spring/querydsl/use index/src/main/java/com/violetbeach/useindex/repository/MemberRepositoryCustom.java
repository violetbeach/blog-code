package com.violetbeach.useindex.repository;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Long> search(String name);
}
