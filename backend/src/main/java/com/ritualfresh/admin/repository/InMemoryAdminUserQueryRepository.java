package com.ritualfresh.admin.repository;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class InMemoryAdminUserQueryRepository implements AdminUserQueryRepository {
    private final UserRepository userRepository;

    public InMemoryAdminUserQueryRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> search(String query, UserRole role, AccountStatus status, Pageable pageable) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<User> filtered = userRepository.findAll().stream()
                .filter(user -> user.getRole() != UserRole.ADMIN)
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> status == null || user.getAccountStatus() == status)
                .filter(user -> matchesQuery(user, normalizedQuery))
                .sorted(comparatorFor(pageable.getSort()))
                .toList();

        int fromIndex = Math.min((int) pageable.getOffset(), filtered.size());
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(fromIndex, toIndex), pageable, filtered.size());
    }

    @Override
    public long countAll() {
        return userRepository.findAll().size();
    }

    @Override
    public long countByRole(UserRole role) {
        return userRepository.findAll().stream().filter(user -> user.getRole() == role).count();
    }

    @Override
    public long countByStatus(AccountStatus status) {
        return userRepository.findAll().stream().filter(user -> user.getAccountStatus() == status).count();
    }

    private boolean matchesQuery(User user, String query) {
        if (query.isBlank()) {
            return true;
        }

        String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase(Locale.ROOT);
        return fullName.contains(query) || user.getEmail().toLowerCase(Locale.ROOT).contains(query);
    }

    private Comparator<User> comparatorFor(Sort sort) {
        Sort.Order order = sort.stream().findFirst().orElse(Sort.Order.desc("createdAt"));
        Function<User, Comparable> extractor = switch (order.getProperty()) {
            case "id" -> User::getId;
            case "email" -> User::getEmail;
            case "firstName" -> User::getFirstName;
            case "lastName" -> User::getLastName;
            case "accountStatus" -> user -> user.getAccountStatus().name();
            case "role" -> user -> user.getRole().name();
            default -> User::getCreatedAt;
        };

        Comparator<User> comparator = Comparator.comparing(extractor, Comparator.nullsLast(Comparator.naturalOrder()));
        return order.isDescending() ? comparator.reversed() : comparator;
    }
}
