package org.itrex.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.entity.Role;
import org.itrex.entity.enums.RoleType;
import org.itrex.repository.RoleRepo;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HibernateRoleRepo implements RoleRepo {
    private final SessionFactory sessionFactory;
    private Session session;

    @Override
    public List<Role> getRoles() {
        session = sessionFactory.openSession();
        List<Role> roles = session.createQuery("FROM Role", Role.class).list();
        session.close();
        return roles;
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        Optional<Role> roleResult = Optional.empty();
        List<String> roleNames = Arrays.stream(RoleType.values())
                .map(RoleType::name)
                .collect(Collectors.toList());
        if (roleNames.contains(name.toUpperCase())) {
            session = sessionFactory.openSession();
            Role role = session.createQuery("FROM Role WHERE role_name = :name", Role.class)
                    .setParameter("name", name.toUpperCase())
                    .getSingleResult();
            session.close();
            roleResult = Optional.of(role);
        }
        return roleResult;
    }

    @Override
    public Optional<Role> getRoleById(Integer id) {
        Optional<Role> role;
        session = sessionFactory.openSession();
        role = session.createQuery("FROM Role WHERE role_id = :id", Role.class)
                .setParameter("id", id)
                .list()
                .stream()
                .findAny();
        session.close();
        return role;
    }
}