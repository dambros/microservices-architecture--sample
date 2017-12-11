package br.com.dataeasy.organization.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.UUID;

import br.com.dataeasy.organization.events.models.Action;
import br.com.dataeasy.organization.events.source.SimpleSourceBean;
import br.com.dataeasy.organization.model.Organization;
import br.com.dataeasy.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    @Autowired
    private Tracer tracer;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    public Organization getOrg (String organizationId) {
        Span newSpan = tracer.createSpan("getOrgDBCall");

        logger.debug("In the organizationService.getOrg() call");
        try {
            return orgRepository.findById(organizationId);
        } finally {
            newSpan.tag("peer.service", "postgres");
            newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
            tracer.close(newSpan);
        }

    }

    public void saveOrg(Organization org) {
        org.setId(UUID.randomUUID().toString());
        orgRepository.save(org);
        simpleSourceBean.publishOrgChange(Action.SAVE, org.getId());
    }

    public void updateOrg(Organization org) {
        orgRepository.save(org);
        simpleSourceBean.publishOrgChange(Action.UPDATE, org.getId());
    }

    public void deleteOrg(String orgId) {
        orgRepository.delete(orgId);
        simpleSourceBean.publishOrgChange(Action.DELETE, orgId);
    }
}
