package org.wso2.governance.sample.executor;

import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.registry.extensions.interfaces.Execution;
import org.wso2.carbon.registry.core.Comment;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.internal.RegistryCoreServiceComponent;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.extensions.utils.CommonUtil;

import javax.xml.stream.XMLStreamException;

public class CustomExecutor implements Execution {

	private static final Log log = LogFactory.getLog(CustomExecutor.class);
    private String[] attributes = new String[0];
 
    public void init(Map parameterMap) {
        if (parameterMap != null) {
            String temp = (String) parameterMap.get("attributes");
            if (temp != null) {
                attributes = temp.split(",");
            }
        }
    }
    
    public boolean execute(RequestContext context, String currentState, String targetState) { 
    	if(attributes.length == 0){
    		return false;
    	}
    	String resourcePath = context.getResourcePath().getPath();
    	log.info(resourcePath + " Lifecycle change from : "+currentState + " to :" + targetState);

        Resource resource = context.getResource();
        try {
            String artifactString = RegistryUtils.decodeBytes((byte[]) resource.getContent());

            log.info("Resource: " + artifactString );


            String user = CarbonContext.getThreadLocalCarbonContext().getUsername();

            log.info("User:  " + user );

            OMElement xmlContent = AXIOMUtil.stringToOM(artifactString);


            Comment[] comments = context.getSystemRegistry().getComments(resourcePath);
            for (Comment comment : comments) {
                log.info("Comment: " + comment.getText());
            }



            /*
            String serviceName = CommonUtil.getServiceName(xmlContent);
            GenericArtifactManager manager = new GenericArtifactManager(
                    RegistryCoreServiceComponent.getRegistryService().getGovernanceUserRegistry(user, CarbonContext
                            .getThreadLocalCarbonContext().getTenantId()), "docker");

            GenericArtifact docker = manager.getGenericArtifact(context.getResource().getUUID());

            */



        } catch (RegistryException e) {
            log.error("Failed to publish service to API store ", e);
            return false;
        } catch (XMLStreamException e) {
            log.error("Failed to convert service to xml content");
            return false;
        }
        return true;

    	
    }

}
