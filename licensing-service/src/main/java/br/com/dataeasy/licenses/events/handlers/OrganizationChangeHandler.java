package br.com.dataeasy.licenses.events.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import br.com.dataeasy.licenses.events.CustomChannels;
import br.com.dataeasy.licenses.events.models.OrganizationChangeModel;
import br.com.dataeasy.licenses.repository.OrganizationRedisRepository;

@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {

    @Autowired
    private OrganizationRedisRepository organizationRedisRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);
    private static final String PATTERN = "Received a {} event from the organization service for organization id {}";

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel orgChange) {
        logger.debug("Received a message of type " + orgChange.getType());

        switch (orgChange.getAction()) {
            case "UPDATE":
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            case "DELETE":
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
        }

        String action = orgChange.getAction() != null ? orgChange.getAction() : "UNKNOWN";
        logger.debug(PATTERN, action, orgChange.getOrganizationId());
    }

}