package com.cristian.organization.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cristian.organization.model.Organization;
import com.cristian.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    public Optional<Organization> getOrg(String organizationId) {
        return orgRepository.findById(organizationId);
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);

    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
    }

    public void deleteOrg(Organization org){
        orgRepository.deleteById( org.getId());
    }
}
