/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package my.arquillian.test;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenResolver {

    private static final Logger LOG = LoggerFactory.getLogger(MavenResolver.class);    

	/**
	 * Resolves an artifact from the local maven repository
	 * 
	 * @param archiveFormat
	 * @param coordinates
	 * @return
	 */
    public static <T extends Archive<T>> T resolveArchive(final Class<T> archiveFormat, final String coordinates) {
    	LOG.info("##### Resolving coordinates [{}]", coordinates);
        return Maven.resolver().offline().loadPomFromFile("pom.xml").resolve(coordinates).withoutTransitivity().asSingle(archiveFormat);
    }
        
}
