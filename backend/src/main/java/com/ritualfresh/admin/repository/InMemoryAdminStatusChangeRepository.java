package com.ritualfresh.admin.repository;

import com.ritualfresh.admin.model.AdminUserStatusChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemoryAdminStatusChangeRepository implements AdminStatusChangeRepository {
    private final List<AdminUserStatusChange> changes = new ArrayList<>();

    @Override
    public AdminUserStatusChange save(AdminUserStatusChange change) {
        change.assignIdIfMissing(changes.size() + 1L);
        changes.add(change);
        return change;
    }

    @Override
    public Page<AdminUserStatusChange> findByTargetUserId(Long targetUserId, Pageable pageable) {
        List<AdminUserStatusChange> filtered = changes.stream()
                .filter(change -> change.getTargetUserId().equals(targetUserId))
                .sorted(Comparator.comparing(AdminUserStatusChange::getChangedAt).reversed())
                .toList();
        int fromIndex = Math.min((int) pageable.getOffset(), filtered.size());
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(fromIndex, toIndex), pageable, filtered.size());
    }
}
